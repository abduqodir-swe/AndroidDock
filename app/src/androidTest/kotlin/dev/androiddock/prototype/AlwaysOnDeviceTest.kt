package dev.androiddock.prototype

import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.ParcelFileDescriptor
import android.provider.Settings
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dev.androiddock.core.model.ChargingPause
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Run last: SCREEN_OFF intentionally locks the device. No keyguard bypass or direct Dock launch. */
@RunWith(AndroidJUnit4::class)
class AlwaysOnDeviceTest {
    @get:Rule val compose = createAndroidComposeRule<AutomaticSettingsActivity>()
    private val instrumentation get() = InstrumentationRegistry.getInstrumentation()
    private val context get() = instrumentation.targetContext
    private val container get() = context.appContainer
    private fun shell(command: String) = ParcelFileDescriptor.AutoCloseInputStream(
        instrumentation.uiAutomation.executeShellCommand(command)
    ).bufferedReader().use { it.readText() }
    private fun top() = shell("dumpsys activity activities").lineSequence()
        .firstOrNull { "topResumedActivity=" in it }.orEmpty()
    private fun plugged() = (context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        ?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) ?: 0) != 0

    @Test fun batteryAutostartAndCableChangesPreserveShowingWindow() {
        assumeTrue(Settings.canDrawOverlays(context))
        val original = runBlocking { container.automatic.modes() }
        try {
            compose.waitUntil(5_000) { container.automatic.alwaysOn.value == original.alwaysOn }
            if (!original.alwaysOn) compose.onNodeWithTag("always-on-switch").performScrollTo().performClick()
            compose.waitUntil(5_000) { container.automatic.alwaysOn.value && container.automaticRuntime.send != null }
            assertTrue(top().contains("dev.androiddock.prototype/.AutomaticSettingsActivity"))
            shell("dumpsys battery unplug")
            compose.waitUntil(5_000) { !plugged() }
            // Check the actual foreground again immediately before sending a screen input.
            assertTrue(top().contains("dev.androiddock.prototype/.AutomaticSettingsActivity"))
            shell("input keyevent 223")
            compose.waitUntil(8_000) { container.automaticRuntime.status.value == "StandBy: Always On" }
            val token = container.automaticRuntime.displaySession.value
            assertNotNull(token)
            assertTrue(top().contains("dev.androiddock.prototype/.DockActivity"))
            assertFalse(plugged())
            shell("dumpsys battery reset")
            val boot = Settings.Global.getInt(context.contentResolver, Settings.Global.BOOT_COUNT, -1)
            compose.waitUntil(5_000) {
                val saved = runBlocking { container.automatic.restoreSession(boot) }
                saved.connected == plugged() && saved.pause == ChargingPause.RECOVERY
            }
            assertEquals(token, container.automaticRuntime.displaySession.value)
            assertEquals("StandBy: Always On", container.automaticRuntime.status.value)
        } finally {
            shell("dumpsys battery reset")
            runBlocking {
                container.automatic.setEnabled(original.onPower)
                container.automatic.setAlwaysOn(original.alwaysOn)
            }
        }
    }
}
