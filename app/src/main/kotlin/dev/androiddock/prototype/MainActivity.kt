package dev.androiddock.prototype

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.androiddock.core.model.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val clock: ClockViewModel by lazy { ViewModelProvider(this)[ClockViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(SystemBarStyle.dark(Color.BLACK), SystemBarStyle.dark(Color.BLACK))
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent {
            val time by clock.state.collectAsStateWithLifecycle()
            val configuration by appContainer.repository.state.collectAsStateWithLifecycle()
            val battery by appContainer.battery.state.collectAsStateWithLifecycle()
            val draft by clock.editor.state.collectAsStateWithLifecycle()
            val busy by clock.busy.collectAsStateWithLifecycle()
            val error by clock.error.collectAsStateWithLifecycle()
            var controlsVisible by rememberSaveable { mutableStateOf(true) }
            var monthOffset by rememberSaveable { mutableIntStateOf(0) }
            val orientation = currentDockOrientation()
            val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                uri?.let { clock.importPhoto(appContainer, it) }
            }
            DockTheme {
                val settings = (configuration as? SettingsState.Ready)?.settings
                WindowBrightness(window, if (draft != null) -1f else settings?.profile()?.windowBrightness() ?: -1f)
                BackHandler(enabled = draft != null) { if (!busy) cancelEditor() }
                if (draft != null) {
                    DockEditor(draft!!, time, battery, busy, error,
                        onChange = clock.editor::change, onOrientation = clock.editor::selectOrientation,
                        onUndo = clock.editor::undo, onCancel = ::cancelEditor,
                        onSave = { clock.saveEditor(appContainer) },
                        onPhoto = { picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) })
                } else if (settings != null) {
                    fun openEditor() { clock.clearError(); clock.editor.open(settings, orientation) }
                    LaunchedEffect(controlsVisible, settings.mode) {
                        if (controlsVisible) { delay(8_000); controlsVisible = false }
                    }
                    Box(Modifier.fillMaxSize()) {
                        DockScreen(time, Modifier.padding(bottom = if (controlsVisible && orientation == DockOrientation.PORTRAIT) 104.dp else 56.dp).pointerInput(settings, orientation) {
                            detectTapGestures(onTap = { controlsVisible = true }, onLongPress = { openEditor() })
                        },
                            settings, battery, monthOffset = monthOffset,
                            onMonthChange = if (controlsVisible) ({ monthOffset = it.coerceIn(-120, 120) }) else null)
                        if (controlsVisible) Surface(
                            modifier = Modifier.align(Alignment.BottomCenter).safeDrawingPadding().padding(horizontal = 12.dp, vertical = 8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    DockMode.entries.forEach { mode ->
                                        TextButton(onClick = { save { it.copy(mode = mode) } }, modifier = Modifier.testTag("mode-${mode.name}")) {
                                            Text(if (mode == DockMode.BEDROOM) "Спальня" else "Стол", color = if (mode == settings.mode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    TextButton(onClick = { openEditor() }) { Text("Настроить") }
                                    if (orientation == DockOrientation.LANDSCAPE) {
                                        if (monthOffset != 0) TextButton(onClick = { monthOffset = 0 }) { Text("Сегодня") }
                                        TextButton(onClick = ::openScreensaverSettings) { Text(stringResource(R.string.screensaver_settings)) }
                                    }
                                }
                                if (orientation == DockOrientation.PORTRAIT) {
                                Row {
                                    if (monthOffset != 0) TextButton(onClick = { monthOffset = 0 }) { Text("Сегодня") }
                                    TextButton(onClick = ::openScreensaverSettings) { Text(stringResource(R.string.screensaver_settings)) }
                                }
                                }
                            }
                        } else TextButton(onClick = { controlsVisible = true }, modifier = Modifier.align(Alignment.BottomCenter).safeDrawingPadding()) { Text("Управление") }
                    }
                } else if (configuration == SettingsState.Error) {
                    Box(Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("Не удалось прочитать настройки. Данные сохранены; перезапустите приложение.")
                    }
                } else Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            }
        }
    }

    private fun cancelEditor() {
        if (clock.busy.value) return
        val saved = (appContainer.repository.state.value as? SettingsState.Ready)?.settings ?: return
        val unused = appContainer.photos.unusedFiles(setOfNotNull(saved.bedroom.photoPath, saved.desktop.photoPath))
        clock.editor.cancel()
        clock.clearError()
        appContainer.scope.launch { appContainer.photos.removeFiles(unused) }
    }

    private fun save(transform: (DockSettings) -> DockSettings) {
        appContainer.scope.launch {
            try { appContainer.repository.update(transform) }
            catch (error: CancellationException) { throw error }
            catch (error: Exception) {
                Log.e("AndroidDock", "Settings write failed", error)
                Toast.makeText(this@MainActivity, "Не удалось сохранить настройки", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStart() { super.onStart(); clock.start() }
    override fun onStop() { clock.stop(); super.onStop() }

    private fun openScreensaverSettings() {
        try { startActivity(Intent(Settings.ACTION_DREAM_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)) }
        catch (error: ActivityNotFoundException) {
            Log.w("AndroidDock", "ACTION_DREAM_SETTINGS unavailable", error)
            Toast.makeText(this, R.string.screensaver_settings_unavailable, Toast.LENGTH_LONG).show()
        }
    }
}

@Composable
internal fun DockTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = darkColorScheme(
        primary = androidx.compose.ui.graphics.Color(0xFFF5F3ED),
        onPrimary = androidx.compose.ui.graphics.Color.Black,
        secondary = androidx.compose.ui.graphics.Color(0xFFFFCA80),
        surface = androidx.compose.ui.graphics.Color(0xFF17191C),
        background = androidx.compose.ui.graphics.Color.Black,
    )) { Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background, content = content) }
}
