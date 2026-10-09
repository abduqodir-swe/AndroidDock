package dev.androiddock.prototype

import android.service.dreams.DreamService
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import dev.androiddock.core.model.ClockStateHolder

class DockDreamService : DreamService() {
    private var owners: DreamOwners? = null
    private var clock: ClockStateHolder? = null
    private var composeView: ComposeView? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        releaseWindow()
        isInteractive = false
        isFullscreen = true
        isScreenBright = true

        val windowOwners = DreamOwners().also { owners = it }
        val holder = ClockStateHolder().also { clock = it }
        val view = ComposeView(this).apply {
            id = android.R.id.content
            setViewTreeLifecycleOwner(windowOwners)
            setViewTreeSavedStateRegistryOwner(windowOwners)
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val state by holder.state.collectAsStateWithLifecycle()
                val configuration by appContainer.repository.state.collectAsStateWithLifecycle()
                val battery by appContainer.battery.state.collectAsStateWithLifecycle()
                val settings = (configuration as? SettingsState.Ready)?.settings
                DockTheme {
                    WindowBrightness(window, settings?.profile()?.windowBrightness() ?: -1f)
                    if (settings != null) DockScreen(state, settings = settings, battery = battery)
                    else if (configuration == SettingsState.Error) androidx.compose.material3.Text("Не удалось прочитать настройки")
                }
            }
        }
        composeView = view
        setContentView(view)
        window.setDockImmersiveMode(true)
        Log.i("AndroidDock", "Dream window attached; Compose owners installed")
    }

    override fun onDreamingStarted() {
        super.onDreamingStarted()
        window.setDockImmersiveMode(true)
        owners?.resume()
        clock?.start()
        Log.i("AndroidDock", "Dream started")
    }

    override fun onDreamingStopped() {
        clock?.stop()
        owners?.stop()
        Log.i("AndroidDock", "Dream stopped")
        super.onDreamingStopped()
    }

    override fun onDetachedFromWindow() {
        releaseWindow()
        Log.i("AndroidDock", "Dream window detached; composition and scope released")
        super.onDetachedFromWindow()
    }

    override fun onDestroy() {
        releaseWindow()
        super.onDestroy()
    }

    private fun releaseWindow() {
        clock?.close()
        clock = null
        owners?.destroy()
        composeView?.disposeComposition()
        composeView = null
        owners = null
    }
}
