package dev.androiddock.prototype

import android.content.Intent
import android.provider.Settings
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AutomaticSettingsTest {
    @get:Rule val compose = createAndroidComposeRule<AutomaticSettingsActivity>()
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private val container get() = context.appContainer
    private var original = AutomaticModes()
    @Before fun prepare() {
        original = runBlocking { container.automatic.modes() }
        runBlocking { container.automatic.disableAll() }
        context.stopService(Intent(context, ChargingWatchService::class.java))
        compose.waitUntil(5_000) { !container.automatic.enabled.value }
    }
    @After fun restore() {
        runBlocking { container.automatic.setEnabled(original.onPower); container.automatic.setAlwaysOn(original.alwaysOn) }
        compose.activityRule.scenario.onActivity { if (original.active && Settings.canDrawOverlays(it)) ChargingWatchService.start(it) }
    }
    @Test fun enabledConsentSurvivesRecreationAndDisableStopsObserver() {
        // Special permission is granted only by the owner in system UI, never by this test.
        assumeTrue(Settings.canDrawOverlays(context))
        compose.onNodeWithTag("automatic-switch").performScrollTo().performClick()
        compose.waitUntil(5_000) { container.automatic.enabled.value && container.automaticRuntime.send != null }
        compose.activityRule.scenario.recreate()
        compose.onNodeWithTag("automatic-switch").assertIsOn()
        assertTrue(runBlocking { container.automatic.consent() })
        compose.onNodeWithTag("automatic-switch").performScrollTo().performClick()
        compose.waitUntil(5_000) { !container.automatic.enabled.value && container.automaticRuntime.send == null }
        assertNull(container.automaticRuntime.displaySession.value)
        assertFalse(runBlocking { container.automatic.consent() })
    }
    @Test fun missingOverlayPermissionDoesNotSaveConsent() {
        assumeTrue(!Settings.canDrawOverlays(context))
        compose.onNodeWithTag("automatic-switch").performScrollTo().performClick()
        compose.onNodeWithTag("automatic-error").assertIsDisplayed()
        assertFalse(runBlocking { container.automatic.consent() })
        assertNull(container.automaticRuntime.send)
    }
    @Test fun notificationActionDurablyDisablesModeAndReleasesObserver() {
        assumeTrue(Settings.canDrawOverlays(context))
        compose.onNodeWithTag("automatic-switch").performScrollTo().performClick()
        compose.onNodeWithTag("always-on-switch").performScrollTo().performClick()
        compose.waitUntil(5_000) { container.automatic.alwaysOn.value }
        val manager = context.getSystemService(android.app.NotificationManager::class.java)
        compose.waitUntil(5_000) { manager.activeNotifications.any { it.id == 42 } && container.automaticRuntime.send != null }
        manager.activeNotifications.single { it.id == 42 }.notification.actions.single().actionIntent.send()
        compose.waitUntil(5_000) { !container.automatic.enabled.value && container.automaticRuntime.send == null }
        assertFalse(runBlocking { container.automatic.consent() })
        compose.onNodeWithTag("automatic-switch").assertIsOff()
        compose.onNodeWithTag("always-on-switch").assertIsOff()
    }
    @Test fun alwaysOnPersistsIndependentlyAndChargingFallbackKeepsObserver() {
        assumeTrue(Settings.canDrawOverlays(context))
        compose.onNodeWithTag("always-on-switch").performScrollTo().performClick()
        compose.waitUntil(5_000) { container.automatic.alwaysOn.value && container.automaticRuntime.send != null }
        assertEquals(AutomaticModes(false, true), runBlocking { container.automatic.modes() })
        compose.activityRule.scenario.recreate()
        compose.onNodeWithTag("always-on-switch").assertIsOn()
        compose.onNodeWithTag("automatic-switch").performScrollTo().performClick()
        compose.waitUntil(5_000) { container.automatic.enabled.value }
        compose.onNodeWithTag("always-on-switch").performScrollTo().performClick()
        compose.waitUntil(5_000) { !container.automatic.alwaysOn.value }
        assertTrue(runBlocking { container.automatic.consent() })
        assertNotNull(container.automaticRuntime.send)
        compose.onNodeWithTag("automatic-switch").performScrollTo().performClick()
        compose.waitUntil(5_000) { container.automaticRuntime.send == null }
    }
}
