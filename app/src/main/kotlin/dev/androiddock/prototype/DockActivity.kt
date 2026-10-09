package dev.androiddock.prototype

import android.os.Build
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        session = intent.getLongExtra(ChargingWatchService.EXTRA_SESSION, 0)
        if (session == 0L || appContainer.automaticRuntime.displaySession.value != session) {
            finish()
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
            appContainer.automaticRuntime.displaySession.collect { if (it != session) finish() }
        }
        android.util.Log.i("AndroidDockAuto", "DockActivity created; locked=" + getSystemService(android.app.KeyguardManager::class.java).isKeyguardLocked)
    }
    override fun onStart() { super.onStart(); clock.start() }
    override fun onStop() {
        clock.stop()
        if (!isChangingConfigurations) appContainer.automaticRuntime.windowStopped(session)
        super.onStop()
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val previous = session
        setIntent(intent)
        session = intent.getLongExtra(ChargingWatchService.EXTRA_SESSION, 0)
        if (session == 0L || appContainer.automaticRuntime.displaySession.value != session) {
            finish()
        } else if (session != previous) {
            // singleTask reuse after a rotation must wake only for the new valid presentation.
            if (Build.VERSION.SDK_INT >= 27) setTurnScreenOn(true)
            else {
                @Suppress("DEPRECATION")
                window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
            }
        }
    }
    private fun exit() {
        appContainer.automaticRuntime.exit(session)
        finish()
    }
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        appContainer.automaticRuntime.userLeaveHint(session)
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && appContainer.automaticRuntime.displaySession.value == session) {
            window.setDockImmersiveMode(true)
            appContainer.automaticRuntime.shown(session)
            android.util.Log.i(ChargingWatchService.TAG, "Dock window focused; session=$session; orientation=${resources.configuration.orientation}")
        }
    }
}
