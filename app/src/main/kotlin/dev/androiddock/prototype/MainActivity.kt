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
            MaterialTheme(colorScheme = darkColorScheme()) {
                Box(Modifier.fillMaxSize()) {
                    DockScreen(state)
                    OutlinedButton(
                        onClick = ::openScreensaverSettings,
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .safeDrawingPadding().padding(24.dp),
                    ) {
                        Text(stringResource(R.string.screensaver_settings))
                    }
                }
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
