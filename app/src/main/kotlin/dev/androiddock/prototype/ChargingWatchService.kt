package dev.androiddock.prototype

import android.app.*
import android.content.*
import android.content.pm.ServiceInfo
import android.media.AudioManager
import android.os.*
import android.provider.Settings
import android.util.Log
import dev.androiddock.core.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

/** An observer started by a visible Activity, a system sticky restart, or consented boot/update. */
class ChargingWatchService : Service() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val events = Channel<ChargingEvent>(Channel.UNLIMITED)
    private val runtime get() = appContainer.automaticRuntime
    private var registered = false
    private var deadlineJob: Job? = null
    private var wakeLock: PowerManager.WakeLock? = null
    private var lastNotification: String? = null
    private var modes = AutomaticModes()
    private val overlayListener = AppOpsManager.OnOpChangedListener { _, packageName ->
        if (packageName == null || packageName == this.packageName) events.trySend(ChargingEvent.Refresh)
    }
    private var watchingOverlay = false
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val signal = when (intent.action) {
                Intent.ACTION_SCREEN_OFF -> ChargingScreenSignal.SCREEN_OFF
                Intent.ACTION_SCREEN_ON -> ChargingScreenSignal.SCREEN_ON
                Intent.ACTION_USER_PRESENT -> ChargingScreenSignal.USER_PRESENT
                else -> ChargingScreenSignal.NONE
            }
            // Capture origin and screen state at delivery, before suspend/persistence can reorder windows.
            val environment = readEnvironment()
            val session = runtime.display.value
            trace("broadcast=${intent.action} session=$session environment=$environment")
            events.trySend(ChargingEvent.ScreenChanged(signal, session, environment))
        }
    }

    override fun onCreate() {
        super.onCreate()
        trace("service onCreate")
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(NotificationChannel(CHANNEL, "Автопоказ StandBy", NotificationManager.IMPORTANCE_LOW).apply {
            setSound(null, null); enableVibration(false); setShowBadge(false)
        })
        try {
            val notification = notification("Проверка сохранённого согласия")
            if (Build.VERSION.SDK_INT >= 34) startForeground(ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
            else startForeground(ID, notification)
            trace("service foreground started")
        } catch (error: RuntimeException) {
            Log.e(TAG, "Foreground promotion rejected", error)
            runtime.mutableStatus.value = "Система запретила запуск наблюдения; откройте AndroidDock"
            stopFor("foreground_rejected")
            return
        }
        runtime.send = { events.trySend(it) }
        try {
            getSystemService(AppOpsManager::class.java).startWatchingMode(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW, packageName, overlayListener)
            watchingOverlay = true
        } catch (error: RuntimeException) { Log.w(TAG, "Overlay permission listener unavailable; plugged-in recheck remains active", error) }
        scope.launch {
            try {
                modes = appContainer.automatic.modes()
                if (!modes.active) { stopFor("modes_inactive_at_start"); return@launch }
                if (!Settings.canDrawOverlays(this@ChargingWatchService)) {
                    runtime.mutableStatus.value = "Разрешение показа поверх приложений отозвано"
                    stopFor("overlay_denied_at_start"); return@launch
                }
                val boot = Settings.Global.getInt(contentResolver, Settings.Global.BOOT_COUNT, -1)
                val holder = ChargingStateHolder(appContainer.automatic.restoreSession(boot), nextSessionId = SystemClock::elapsedRealtimeNanos, alwaysOn = modes.alwaysOn)
                var savedSession: ChargingSession? = null
                val filter = IntentFilter().apply {
                    addAction(Intent.ACTION_BATTERY_CHANGED)
                    addAction(Intent.ACTION_POWER_CONNECTED); addAction(Intent.ACTION_POWER_DISCONNECTED)
                    addAction(Intent.ACTION_SCREEN_OFF); addAction(Intent.ACTION_SCREEN_ON); addAction(Intent.ACTION_USER_PRESENT)
                }
                // Only protected system broadcasts; NOT_EXPORTED can miss broadcasts sent by privileged OEM apps.
                if (Build.VERSION.SDK_INT >= 33) registerReceiver(receiver, filter, RECEIVER_EXPORTED)
                else registerReceiver(receiver, filter)
                registered = true
                trace("observer ready restored=${holder.session} boot=$boot")
                events.trySend(ChargingEvent.Refresh)
                launch {
                    appContainer.automatic.modeChanges.collect { events.send(ChargingEvent.ModesChanged(it)) }
                }
                for (event in events) {
                    val now = SystemClock.elapsedRealtime()
                    val previousPhase = holder.phase
                    val previousPause = holder.pause
                    val previousDisplay = holder.activeSession
                    val effects = when (event) {
                        ChargingEvent.Disable -> {
                            appContainer.automatic.disableAll()
                            modes = AutomaticModes()
                            holder.disable()
                        }
                        is ChargingEvent.ModesChanged -> {
                            modes = event.modes
                            if (modes.active) holder.update(readEnvironment(), now) else holder.disable()
                        }
                        is ChargingEvent.Shown -> {
                            holder.shown(event.session, getSystemService(PowerManager::class.java).isInteractive)
                            emptyList()
                        }
                        is ChargingEvent.Exit -> holder.exit(event.session)
                        is ChargingEvent.UserLeaveHint -> holder.userLeaveHint(event.session)
                        is ChargingEvent.WindowStopped -> holder.windowStopped(event.session, event.interactive, event.keyguardLocked, now)
                        is ChargingEvent.ScreenChanged -> holder.update(event.environment.copy(alwaysOn = modes.alwaysOn), now, event.signal, event.session)
                        ChargingEvent.Refresh -> holder.update(readEnvironment(), now)
                        ChargingEvent.Tick -> holder.update(readEnvironment(), now) + holder.tick(now)
                    }
                    if (previousDisplay != null && previousDisplay != holder.activeSession) {
                        trace("displaySession closed reason=$event session=$previousDisplay next=${holder.activeSession}")
                    }
                    // Persist before any window launch/close, so process death cannot erase a manual exit.
                    if (savedSession != holder.session) {
                        appContainer.automatic.saveSession(boot, holder.session)
                        savedSession = holder.session
                    }
                    runtime.display.value = holder.activeSession
                    for (effect in effects) when (effect) {
                        ChargingEffect.CLOSE -> Unit // The holder has already released the matching token.
                        ChargingEffect.LAUNCH_TIMEOUT -> {
                            Log.e(TAG, "Launch not acknowledged by a focused window; check ActivityTaskManager and OEM lockscreen/background permissions")
                        }
                        ChargingEffect.RESTORE_WINDOW -> {
                            val session = holder.activeSession
                            val current = readEnvironment()
                            val audioMode = getSystemService(AudioManager::class.java).mode
                            val callActive = audioMode == AudioManager.MODE_RINGTONE ||
                                audioMode == AudioManager.MODE_IN_CALL || audioMode == AudioManager.MODE_IN_COMMUNICATION
                            if (session != null && current.interactive && current.keyguardLocked &&
                                current.allowed && (current.plugged || current.alwaysOn) && !callActive) {
                                try {
                                    trace("restoring covered DockActivity once session=$session environment=$current")
                                    startActivity(Intent(this@ChargingWatchService, DockActivity::class.java)
                                        .putExtra(EXTRA_SESSION, session).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                                } catch (error: RuntimeException) {
                                    Log.e(TAG, "Covered DockActivity restore rejected; session=$session", error)
                                }
                            } else trace("covered DockActivity restore cancelled session=$session environment=$current audioMode=$audioMode")
                        }
                        ChargingEffect.LAUNCH -> {
                            val session = checkNotNull(holder.activeSession)
                            runtime.display.value = session
                            try {
                                Log.i(TAG, "Launching DockActivity from service; session=$session; plugged=${holder.connected}")
                                startActivity(Intent(this@ChargingWatchService, DockActivity::class.java)
                                    .putExtra(EXTRA_SESSION, session).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                            } catch (error: RuntimeException) {
                                Log.e(TAG, "Activity launch threw", error)
                                holder.launchFailed(session); runtime.display.value = null
                                appContainer.automatic.saveSession(boot, holder.session)
                            }
                        }
                    }
                    val status = when {
                        holder.pause == ChargingPause.LAUNCH_ERROR -> "Система не показала StandBy; повтор после использования телефона"
                        !Settings.canDrawOverlays(this@ChargingWatchService) -> "Разрешение показа поверх приложений отозвано"
                        holder.phase == ChargingPhase.DISABLED -> "Наблюдение отключено"
                        holder.phase == ChargingPhase.WAIT_FOR_USE && holder.pause == ChargingPause.DISPLAY_OFF -> "Экран выключен; StandBy вернётся после использования и блокировки"
                        holder.phase == ChargingPhase.WAIT_FOR_USE -> "Ждём использования телефона и следующей блокировки"
                        holder.phase == ChargingPhase.SHOWING -> if (modes.alwaysOn) "StandBy: Always On" else "StandBy на зарядке"
                        holder.phase == ChargingPhase.LAUNCHING -> "Открываем StandBy"
                        holder.phase == ChargingPhase.PENDING -> "Готовимся открыть StandBy"
                        modes.alwaysOn -> "Always On включён; ждём блокировки телефона"
                        holder.connected -> "Питание подключено; ждём выключения экрана"
                        else -> "Ждём подключения питания"
                    }
                    runtime.mutableStatus.value = status
                    val notificationKey = "${modes.alwaysOn}:$status"
                    if (lastNotification != notificationKey) {
                        manager.notify(ID, notification(status)); lastNotification = notificationKey
                    }
                    if (!modes.active || !Settings.canDrawOverlays(this@ChargingWatchService)) {
                        stopFor(if (!modes.active) "modes_inactive" else "overlay_denied")
                        break
                    }
                    deadlineJob?.cancel()
                    val deadline = holder.deadline
                    // No permanent CPU lock: only a bounded lock while scheduling/confirming a launch.
                    if (holder.phase == ChargingPhase.PENDING || holder.phase == ChargingPhase.LAUNCHING) {
                        if (wakeLock?.isHeld != true) {
                            wakeLock = getSystemService(PowerManager::class.java).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AndroidDock:launch")
                                .apply { setReferenceCounted(false); acquire(10_000) }
                        }
                    } else releaseWakeLock()
                    if (deadline != null || holder.observing) deadlineJob = launch {
                        val wait = if (deadline == null) 3_000L else (deadline - SystemClock.elapsedRealtime()).coerceIn(1, 3_000)
                        delay(wait); events.send(ChargingEvent.Tick)
                    }
                    if (event != ChargingEvent.Tick || previousPhase != holder.phase || previousPause != holder.pause || previousDisplay != holder.activeSession) {
                        trace("source=$event phase=$previousPhase->${holder.phase} pause=$previousPause->${holder.pause} connected=${holder.connected} display=$previousDisplay->${holder.activeSession} deadline=${holder.deadline} effects=$effects")
                    }
                }
            } catch (error: CancellationException) { throw error }
            catch (error: Exception) {
                Log.e(TAG, "Observer failed", error)
                runtime.mutableStatus.value = "Ошибка наблюдения; откройте AndroidDock для повторного запуска"
                stopFor("observer_failed")
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        trace("service onStartCommand action=${intent?.action} startId=$startId")
        events.trySend(if (intent?.action == DISABLE) ChargingEvent.Disable else ChargingEvent.Refresh)
        return START_STICKY
    }
    override fun onDestroy() {
        trace("service onDestroy displaySession=${runtime.display.value}")
        runtime.send = null
        runtime.display.value = null
        if (registered) unregisterReceiver(receiver)
        if (watchingOverlay) getSystemService(AppOpsManager::class.java).stopWatchingMode(overlayListener)
        releaseWakeLock()
        events.close(); scope.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        trace("service stopped receiver jobs and wake lock released")
        super.onDestroy()
    }
    private fun readEnvironment(): ChargingEnvironment {
        val battery = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        return ChargingEnvironment(
            plugged = (battery?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) ?: 0) != 0,
            interactive = getSystemService(PowerManager::class.java).isInteractive,
            keyguardLocked = getSystemService(KeyguardManager::class.java).isKeyguardLocked,
            allowed = Settings.canDrawOverlays(this),
            alwaysOn = modes.alwaysOn,
        )
    }
    private fun releaseWakeLock() { wakeLock?.let { if (it.isHeld) it.release() }; wakeLock = null }
    private fun stopFor(reason: String) { trace("service stopSelf reason=$reason session=${runtime.display.value}"); stopSelf() }
    private fun trace(message: String) { Log.i(TAG, "t=${SystemClock.elapsedRealtime()} $message") }
    override fun onBind(intent: Intent?): IBinder? = null

    private fun notification(status: String): Notification {
        val open = PendingIntent.getActivity(this, 0, Intent(this, AutomaticSettingsActivity::class.java), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val stop = PendingIntent.getService(this, 1, Intent(this, ChargingWatchService::class.java).setAction(DISABLE), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        return Notification.Builder(this, CHANNEL).setSmallIcon(android.R.drawable.ic_lock_idle_charging)
            .setContentTitle(if (modes.alwaysOn) "AndroidDock: Always On" else "AndroidDock: автоматически на зарядке").setContentText(status)
            .setContentIntent(open).setOngoing(true).setOnlyAlertOnce(true).setVisibility(Notification.VISIBILITY_PRIVATE)
            .addAction(Notification.Action.Builder(null, "Отключить", stop).build()).build()
    }
    companion object {
        const val TAG = "AndroidDockAuto"
        const val EXTRA_SESSION = "charging_session"
        const val DISABLE = "dev.androiddock.prototype.DISABLE_AUTOMATIC"
        private const val CHANNEL = "automatic"
        private const val ID = 42
        /** Caller must be a visible Activity or a documented boot/update exception. Never called on POWER_CONNECTED. */
        fun start(context: Context): Boolean = try {
            context.startForegroundService(Intent(context, ChargingWatchService::class.java)); true
        } catch (error: RuntimeException) {
            Log.e(TAG, "Service start rejected", error)
            context.appContainer.automaticRuntime.mutableStatus.value = "Система запретила наблюдение; откройте AndroidDock и проверьте ограничения батареи"
            false
        }
    }
}
