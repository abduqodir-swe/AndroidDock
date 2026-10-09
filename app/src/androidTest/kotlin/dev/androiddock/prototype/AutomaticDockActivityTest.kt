package dev.androiddock.prototype

import android.content.Intent
import android.provider.Settings
import android.view.WindowManager
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dev.androiddock.core.model.ChargingSession
import java.util.concurrent.CopyOnWriteArrayList
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Native Activity wiring only. Autostart is verified separately using real screen/power events. */
@RunWith(AndroidJUnit4::class)
class AutomaticDockActivityTest {
    @get:Rule val compose = createEmptyComposeRule()
    private val instrumentation get() = InstrumentationRegistry.getInstrumentation()
    private val context get() = instrumentation.targetContext
    private val container get() = context.appContainer
    private val events = CopyOnWriteArrayList<ChargingEvent>()
    private var original = AutomaticModes()
    private lateinit var snapshot: ChargingSession
    private var scenario: ActivityScenario<DockActivity>? = null
    private val boot get() = Settings.Global.getInt(context.contentResolver, Settings.Global.BOOT_COUNT, -1)
    private val token = 9021L

    @Before fun prepare() {
        runBlocking {
            original = container.automatic.modes()
            snapshot = container.automatic.restoreSession(boot)
            container.automatic.disableAll()
            context.stopService(Intent(context, ChargingWatchService::class.java))
            repeat(50) { if (container.automaticRuntime.send != null) delay(100) }
        }
        assertNull(container.automaticRuntime.send)
        instrumentation.runOnMainSync {
            container.automaticRuntime.display.value = token
            container.automaticRuntime.send = { events.add(it) }
        }
        scenario = ActivityScenario.launch(Intent(context, DockActivity::class.java)
            .putExtra(ChargingWatchService.EXTRA_SESSION, token).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        compose.waitUntil(5_000) { events.any { it == ChargingEvent.Shown(token) } }
        compose.onNodeWithTag("automatic-dock").assertIsDisplayed()
        events.clear()
    }

    @After fun restore() {
        scenario?.close()
        instrumentation.runOnMainSync {
            container.automaticRuntime.send = null
            container.automaticRuntime.display.value = null
        }
        runBlocking {
            container.automatic.setEnabled(original.onPower)
            container.automatic.setAlwaysOn(original.alwaysOn)
            container.automatic.saveSession(boot, snapshot)
        }
        // A visible MainActivity restores the actual observer after the test run.
    }

    @Test fun touchRecreationAndTemporaryStopDoNotSendExit() {
        scenario!!.onActivity { activity ->
            assertTrue(activity.window.attributes.flags and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON != 0)
        }
        compose.onNodeWithTag("automatic-dock").performTouchInput { click() }
        scenario!!.recreate()
        compose.onNodeWithTag("automatic-dock").assertIsDisplayed()
        scenario!!.onActivity { activity ->
            assertTrue(activity.window.attributes.flags and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON != 0)
        }
        scenario!!.moveToState(Lifecycle.State.CREATED)
        assertTrue(events.any { it is ChargingEvent.WindowStopped && it.session == token })
        assertFalse(events.any { it is ChargingEvent.Exit })
        assertEquals(token, container.automaticRuntime.displaySession.value)
        scenario!!.moveToState(Lifecycle.State.RESUMED)
        compose.onNodeWithTag("automatic-dock").assertIsDisplayed()
    }

    @Test fun backSendsExplicitExitForCurrentToken() {
        scenario!!.onActivity { it.onBackPressedDispatcher.onBackPressed() }
        compose.waitUntil(5_000) { events.any { it == ChargingEvent.Exit(token) } }
        compose.waitUntil(5_000) { scenario!!.state == Lifecycle.State.DESTROYED }
        assertEquals(Lifecycle.State.DESTROYED, scenario!!.state)
    }
}
