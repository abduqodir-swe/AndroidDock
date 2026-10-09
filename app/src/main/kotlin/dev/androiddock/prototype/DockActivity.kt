package dev.androiddock.prototype

import android.os.Build
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import kotlinx.coroutines.launch

/** Display only: no editor, settings, links or keyguard dismissal. */
class DockActivity : ComponentActivity() {
    private val clock by lazy { ViewModelProvider(this)[ClockViewModel::class.java] }
    private var session = 0L
    private var lastInteractionAt: Long? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        session = intent.getLongExtra(ChargingWatchService.EXTRA_SESSION, 0)
        if (session == 0L || appContainer.automaticRuntime.displaySession.value != session) {
            finishWithReason("invalid_on_create")
            return
        }
        enableEdgeToEdge(SystemBarStyle.dark(Color.BLACK), SystemBarStyle.dark(Color.BLACK))
        if (Build.VERSION.SDK_INT >= 27) {
            setShowWhenLocked(true)
            setTurnScreenOn(savedInstanceState == null)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                if (savedInstanceState == null) WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON else 0)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.setDockImmersiveMode(true)
        setContent {
            val time by clock.state.collectAsStateWithLifecycle()
            val configuration by appContainer.repository.state.collectAsStateWithLifecycle()
            val battery by appContainer.battery.state.collectAsStateWithLifecycle()
            val settings = (configuration as? SettingsState.Ready)?.settings
            DockTheme {
                BackHandler { exit() }
                WindowBrightness(window, settings?.profile()?.windowBrightness() ?: -1f)
                if (settings != null) DockScreen(time, Modifier.testTag("automatic-dock"), settings = settings, battery = battery)
                else if (configuration == SettingsState.Error) androidx.compose.material3.Text("Не удалось прочитать настройки")
            }
        }
        lifecycleScope.launch {
            // Finish this window only: an old callback must not remove a newer window's task.
            appContainer.automaticRuntime.displaySession.collect { if (it != session) finishWithReason("session_released current=$it") }
        }
        trace("onCreate restored=${savedInstanceState != null}")
    }
    override fun onStart() { super.onStart(); clock.start(); trace("onStart") }
    override fun onResume() { super.onResume(); trace("onResume") }
    override fun onPause() { trace("onPause"); super.onPause() }
    override fun onStop() {
        trace("onStop changingConfigurations=$isChangingConfigurations")
        clock.stop()
        if (!isChangingConfigurations) appContainer.automaticRuntime.windowStopped(
            session,
            getSystemService(android.os.PowerManager::class.java).isInteractive,
            getSystemService(android.app.KeyguardManager::class.java).isKeyguardLocked,
        )
        super.onStop()
    }
    override fun onDestroy() { trace("onDestroy finishing=$isFinishing"); super.onDestroy() }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val previous = session
        setIntent(intent)
        session = intent.getLongExtra(ChargingWatchService.EXTRA_SESSION, 0)
        if (session == 0L || appContainer.automaticRuntime.displaySession.value != session) {
            finishWithReason("invalid_new_intent previous=$previous")
        } else if (session != previous) {
            // singleTask reuse after a rotation must wake only for the new valid presentation.
            if (Build.VERSION.SDK_INT >= 27) setTurnScreenOn(true)
            else {
                @Suppress("DEPRECATION")
                window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
            }
        }
        trace("onNewIntent previous=$previous")
    }
    private fun exit() {
        appContainer.automaticRuntime.exit(session)
        finishWithReason("back")
    }
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        trace("onUserLeaveHint")
        appContainer.automaticRuntime.userLeaveHint(session)
    }
    override fun onUserInteraction() {
        super.onUserInteraction()
        lastInteractionAt = SystemClock.elapsedRealtime()
        trace("onUserInteraction")
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        trace("focus=$hasFocus")
        if (hasFocus && appContainer.automaticRuntime.displaySession.value == session) {
            window.setDockImmersiveMode(true)
            // The first launch may wake the screen; reusing this session must not.
            if (Build.VERSION.SDK_INT >= 27) setTurnScreenOn(false)
            else {
                @Suppress("DEPRECATION")
                window.clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
            }
            appContainer.automaticRuntime.shown(session)
        }
    }
    private fun finishWithReason(reason: String) {
        trace("finish reason=$reason")
        finish()
    }
    private fun trace(event: String) {
        val interactive = getSystemService(android.os.PowerManager::class.java).isInteractive
        val locked = getSystemService(android.app.KeyguardManager::class.java).isKeyguardLocked
        val keepScreenOn = window.attributes.flags and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON != 0
        Log.i(ChargingWatchService.TAG, "t=${SystemClock.elapsedRealtime()} activity=$event session=$session active=${appContainer.automaticRuntime.displaySession.value} interactive=$interactive locked=$locked keepScreenOn=$keepScreenOn brightness=${window.attributes.screenBrightness} lastInteraction=$lastInteractionAt orientation=${resources.configuration.orientation}")
    }
}
