package dev.androiddock.prototype

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import android.net.Uri
import android.os.SystemClock
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dev.androiddock.core.model.*
import java.io.File
import java.io.FileOutputStream
import java.io.FileInputStream
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DockActivityTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private lateinit var originalSettings: DockSettings
    private val photoBackups = mutableMapOf<String, File>()
    private val container get() = InstrumentationRegistry.getInstrumentation().targetContext.appContainer

    @Before fun prepare() {
        runBlocking {
            originalSettings = (container.repository.state.first { it is SettingsState.Ready } as SettingsState.Ready).settings
            setOfNotNull(originalSettings.bedroom.photoPath, originalSettings.desktop.photoPath).forEach { name ->
                container.photos.resolve(name)?.takeIf { it.exists() }?.let { file ->
                    val backup = File.createTempFile("dock-photo-backup", ".jpg", file.parentFile!!.parentFile)
                    file.copyTo(backup, overwrite = true)
                    photoBackups[name] = backup
                }
            }
            container.repository.update { DockSettings() }
        }
        rotate(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, Configuration.ORIENTATION_PORTRAIT)
        compose.waitUntil(5_000) { compose.onAllNodesWithTag("clock").fetchSemanticsNodes().size == 1 }
    }

    @After fun restore() {
        compose.activityRule.scenario.onActivity { ViewModelProvider(it)[ClockViewModel::class.java].editor.cancel() }
        photoBackups.forEach { (name, backup) -> backup.copyTo(container.photos.resolve(name)!!, overwrite = true); backup.delete() }
        runBlocking { container.repository.update { originalSettings } }
        compose.activityRule.scenario.onActivity { it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED }
    }

    @Test fun clockRunsInPortraitAndViewModelSurvivesLandscapeRotation() {
        val scenario = compose.activityRule.scenario
        val clockNode = compose.onNodeWithTag("clock")
        assertCleanDock()
        clockNode.assertIsDisplayed()
        compose.onNodeWithTag("calendar").assertIsDisplayed()
        compose.onNodeWithTag("today").assertIsDisplayed()
        compose.onNodeWithTag("battery").assertIsDisplayed()
        assertTrue(clockNode.fetchSemanticsNode().boundsInRoot.center.y < compose.onNodeWithTag("calendar").fetchSemanticsNode().boundsInRoot.center.y)
        val firstText = clockNode.fetchSemanticsNode().config[SemanticsProperties.Text].single().text
        compose.waitUntil(3_000) { clockNode.fetchSemanticsNode().config[SemanticsProperties.Text].single().text != firstText }
        compose.onNodeWithTag("dock-display").performTouchInput { click() }
        compose.onNodeWithTag("dock-controls").assertIsDisplayed()
        compose.onNodeWithContentDescription("Следующий месяц").performClick()
        compose.onNodeWithTag("today").assertDoesNotExist()
        compose.onNodeWithContentDescription("Предыдущий месяц").performClick()
        compose.onNodeWithTag("today").assertIsDisplayed()
        compose.onNodeWithTag("dock-display").performTouchInput { click() }
        assertCleanDock()
        lateinit var original: ClockViewModel
        lateinit var portraitActivity: MainActivity
        scenario.onActivity { portraitActivity = it; original = ViewModelProvider(it)[ClockViewModel::class.java] }
        capture("stage2-activity-portrait.png")
        rotate(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, Configuration.ORIENTATION_LANDSCAPE)
        assertCleanDock()
        scenario.onActivity { assertNotSame(portraitActivity, it); assertSame(original, ViewModelProvider(it)[ClockViewModel::class.java]) }
        compose.onNodeWithTag("calendar").assertIsDisplayed()
        assertTrue(clockNode.fetchSemanticsNode().boundsInRoot.center.x < compose.onNodeWithTag("calendar").fetchSemanticsNode().boundsInRoot.center.x)
        capture("stage2-activity-landscape.png")
        rotate(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, Configuration.ORIENTATION_PORTRAIT)
        scenario.onActivity { assertSame(original, ViewModelProvider(it)[ClockViewModel::class.java]) }
        openEditor()
        compose.waitUntil(5_000) { systemBarsVisible() }
        compose.onNodeWithText("Отмена").performClick()
        assertCleanDock()
        scenario.recreate()
        assertCleanDock()
    }

    @Test fun savedPortraitSoloAndFirstWeekdaySurviveRecreationAndDoNotChangeLandscape() {
        openEditor()
        capture("stage2-editor-portrait.png")
        compose.onNodeWithText("SOLO").performScrollTo().performClick()
        compose.onNodeWithText("Воскресенье").performScrollTo().performClick()
        compose.onNodeWithTag("save-editor").performClick()
        compose.waitUntil(5_000) { saved().desktop.portrait.kind == LayoutKind.SOLO && saved().desktop.firstDayOfWeek == 7 }
        compose.activityRule.scenario.recreate()
        compose.onNodeWithTag("clock").assertIsDisplayed()
        compose.onNodeWithTag("calendar").assertDoesNotExist()
        rotate(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, Configuration.ORIENTATION_LANDSCAPE)
        compose.onNodeWithTag("calendar").assertIsDisplayed()
        assertEquals(LayoutKind.DUO, saved().desktop.landscape.kind)
        assertEquals(7, saved().desktop.firstDayOfWeek)
        openEditor()
        capture("stage2-editor-landscape.png")
        compose.onNodeWithText("Отмена").performClick()
    }

    @Test fun analogAndFlipClocksAndCancelledEditWork() {
        openEditor()
        compose.onNodeWithText("Аналоговые").performScrollTo().performClick()
        compose.onNodeWithTag("save-editor").performClick()
        compose.waitUntil(5_000) { saved().desktop.clockStyle == ClockStyle.ANALOG }
        compose.onNodeWithTag("clock").assertContentDescriptionContains("Аналоговые часы", substring = true)
        capture("stage2-analog-portrait.png")
        rotate(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, Configuration.ORIENTATION_LANDSCAPE)
        compose.onNodeWithTag("clock").assertIsDisplayed()
        capture("stage2-analog-landscape.png")
        rotate(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, Configuration.ORIENTATION_PORTRAIT)
        openEditor()
        compose.onNodeWithText("Перекидные").performScrollTo().performClick()
        compose.onNodeWithTag("Анимация часов").performScrollTo().performClick()
        compose.onNodeWithTag("save-editor").performClick()
        compose.waitUntil(5_000) { saved().desktop.clockStyle == ClockStyle.FLIP && !saved().desktop.animations }
        compose.onNodeWithTag("clock").assertContentDescriptionContains("Перекидные часы", substring = true)
        capture("stage2-flip-portrait.png")
        rotate(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, Configuration.ORIENTATION_LANDSCAPE)
        capture("stage2-flip-landscape.png")
        openEditor()
        compose.onNodeWithText("Цифровые").performScrollTo().performClick()
        compose.onNodeWithText("Отмена").performClick()
        assertEquals(ClockStyle.FLIP, saved().desktop.clockStyle)
    }

    @Test fun modesNightBrightnessAndPrivatePhotoBackgroundWork() {
        runBlocking { container.repository.update { it.copy(mode = DockMode.BEDROOM) } }
        compose.waitUntil(5_000) { compose.activity.window.attributes.screenBrightness == .08f }
        assertEquals(DockMode.BEDROOM, saved().mode)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val source = File(context.cacheDir, "test-background.jpg")
        val bitmap = Bitmap.createBitmap(2400, 800, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(android.graphics.Color.rgb(26, 72, 105))
        source.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it) }
        bitmap.recycle()
        val name = runBlocking { container.photos.import(Uri.fromFile(source)) }
        source.delete()
        try {
            val bounds = android.graphics.BitmapFactory.Options().apply { inJustDecodeBounds = true }
            android.graphics.BitmapFactory.decodeFile(container.photos.resolve(name)!!.path, bounds)
            assertTrue(bounds.outWidth <= 2048 && bounds.outHeight <= 2048)
            runBlocking { container.repository.update { it.copy(mode = DockMode.DESKTOP, desktop = it.desktop.copy(background = DockBackground.PHOTO, photoPath = name)) } }
            compose.waitUntil(5_000) { compose.onAllNodesWithTag("photo-background").fetchSemanticsNodes().isNotEmpty() }
            compose.onNodeWithTag("photo-background").assertIsDisplayed()
            capture("stage2-photo-portrait.png")
            compose.activityRule.scenario.recreate()
            compose.onNodeWithTag("photo-background").assertIsDisplayed()
            rotate(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, Configuration.ORIENTATION_LANDSCAPE)
            compose.onNodeWithTag("photo-background").assertIsDisplayed()
            capture("stage2-photo-landscape.png")
            assertEquals(-1f, compose.activity.window.attributes.screenBrightness)
        } finally {
            runBlocking { container.repository.update { originalSettings } }
            container.photos.resolve(name)?.delete()
        }
    }

    private fun saved() = (container.repository.state.value as SettingsState.Ready).settings
    private fun openEditor() {
        compose.onNodeWithTag("dock-display").performTouchInput { longClick() }
        compose.onNodeWithText("Мой экран").assertIsDisplayed()
    }

    private fun assertCleanDock() {
        compose.onNodeWithTag("dock-display").assertIsDisplayed()
        compose.onNodeWithTag("dock-controls").assertDoesNotExist()
        compose.onNodeWithText("Управление").assertDoesNotExist()
        compose.waitUntil(5_000) { !systemBarsVisible() }
    }

    private fun systemBarsVisible(): Boolean {
        var visible = true
        compose.activityRule.scenario.onActivity { activity ->
            ViewCompat.getRootWindowInsets(activity.window.decorView)?.let { insets ->
                visible = insets.isVisible(WindowInsetsCompat.Type.statusBars()) ||
                    insets.isVisible(WindowInsetsCompat.Type.navigationBars())
            }
        }
        return visible
    }

    private fun rotate(requested: Int, expected: Int) {
        compose.activityRule.scenario.onActivity { it.requestedOrientation = requested }
        compose.waitUntil(5_000) { compose.activity.resources.configuration.orientation == expected }
        compose.waitForIdle()
        SystemClock.sleep(800)
    }

    private fun capture(name: String) {
        compose.waitForIdle()
        SystemClock.sleep(800)
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        // Window capture is separate evidence from the native Compose surface capture.
        val root = compose.onRoot().captureToImage().asAndroidBitmap()
        FileOutputStream(File(instrumentation.targetContext.getExternalFilesDir(null), "compose-root-$name")).use { root.compress(Bitmap.CompressFormat.PNG, 100, it) }
        // HyperOS takeScreenshot sometimes returned only damaged regions, even in system bars.
        // Keep screencap as separate evidence; completeness is checked after pulling it.
        instrumentation.uiAutomation.executeShellCommand("screencap -p").use { descriptor ->
            FileInputStream(descriptor.fileDescriptor).use { input ->
                FileOutputStream(File(instrumentation.targetContext.getExternalFilesDir(null), name)).use { input.copyTo(it) }
            }
        }
    }
}
