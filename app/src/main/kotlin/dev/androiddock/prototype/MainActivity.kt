package dev.androiddock.prototype

import android.graphics.Color
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.CircularProgressIndicator
import dev.androiddock.core.model.DockSettings
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle

class MainActivity : ComponentActivity() {
    private val clock: ClockViewModel by lazy {
        ViewModelProvider(this)[ClockViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.BLACK),
            navigationBarStyle = SystemBarStyle.dark(Color.BLACK),
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContent {
            val state by clock.state.collectAsStateWithLifecycle()
            val configuration by appContainer.repository.state.collectAsStateWithLifecycle()
            val battery by appContainer.battery.state.collectAsStateWithLifecycle()
            var editing by remember { mutableStateOf(false) }
            MaterialTheme(colorScheme = darkColorScheme()) {
                Box(Modifier.fillMaxSize()) {
                    val settings = (configuration as? SettingsState.Ready)?.settings
                    if (settings != null) {
                    DockScreen(state, settings = settings, battery = battery, modifier = Modifier.padding(bottom = 72.dp))
                    Row(Modifier.align(Alignment.BottomCenter).safeDrawingPadding().padding(8.dp)) {
                    TextButton(onClick = { editing = true }) { Text("Настроить") }
                    OutlinedButton(
                        onClick = ::openScreensaverSettings,
                    ) {
                        Text(stringResource(R.string.screensaver_settings))
                    }
                    }
                    if (editing) AlertDialog(onDismissRequest = { editing = false }, title = { Text("Первый день недели") }, text = {
                        Column {
                            TextButton(onClick = { save { it.withProfile(it.mode, it.profile().copy(firstDayOfWeek = 1)) }; editing = false }) { Text("Понедельник") }
                            TextButton(onClick = { save { it.withProfile(it.mode, it.profile().copy(firstDayOfWeek = 7)) }; editing = false }) { Text("Воскресенье") }
                        }
                    }, confirmButton = { TextButton(onClick = { editing = false }) { Text("Закрыть") } })
                    } else if (configuration == SettingsState.Error) {
                        Text("Не удалось прочитать настройки. Данные сохранены; перезапустите приложение.", Modifier.align(Alignment.Center).padding(24.dp))
                    } else CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
            }
        }
    }

    private fun save(transform: (DockSettings) -> DockSettings) {
        appContainer.scope.launch {
            try { appContainer.repository.update(transform) }
            catch (error: Exception) {
                Log.e("AndroidDock", "Settings write failed", error)
                Toast.makeText(this@MainActivity, "Не удалось сохранить настройки", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        clock.start()
    }

    override fun onStop() {
        clock.stop()
        super.onStop()
    }

    private fun openScreensaverSettings() {
        try {
            startActivity(Intent(Settings.ACTION_DREAM_SETTINGS))
        } catch (error: ActivityNotFoundException) {
            Log.w("AndroidDock", "ACTION_DREAM_SETTINGS unavailable", error)
            Toast.makeText(this, R.string.screensaver_settings_unavailable, Toast.LENGTH_LONG).show()
        }
    }
}
