package dev.androiddock.prototype

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.lifecycle.ViewModelProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import java.io.FileOutputStream
import org.junit.Assert.assertSame
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DockActivityTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    @Test fun clockRunsInPortraitAndViewModelSurvivesLandscapeRotation() {
        val scenario = compose.activityRule.scenario
        scenario.onActivity { it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT }
        compose.waitUntil(5_000) {
            compose.activity.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        }
        val clockNode = compose.onNode(SemanticsMatcher("digital clock") { node ->
            node.config.getOrNull(SemanticsProperties.Text)?.singleOrNull()?.text
                ?.matches(Regex("\\d{2}:\\d{2}:\\d{2}")) == true
        })
        clockNode.assertIsDisplayed()
        compose.onNodeWithText("Настройки заставки").assertIsDisplayed()
        val firstText = clockNode.fetchSemanticsNode().config[SemanticsProperties.Text].single().text
        compose.waitUntil(3_000) {
            clockNode.fetchSemanticsNode().config[SemanticsProperties.Text].single().text != firstText
        }
        lateinit var original: ClockViewModel
        scenario.onActivity { original = ViewModelProvider(it)[ClockViewModel::class.java] }
        capture("activity-portrait.png")
        scenario.onActivity { it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE }
        compose.waitUntil(5_000) {
            compose.activity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        }
        clockNode.assertIsDisplayed()
        scenario.onActivity { assertSame(original, ViewModelProvider(it)[ClockViewModel::class.java]) }
        capture("activity-landscape.png")
        scenario.onActivity { it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT }
        compose.waitUntil(5_000) {
            compose.activity.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        }
        scenario.onActivity { assertSame(original, ViewModelProvider(it)[ClockViewModel::class.java]) }
        clockNode.assertIsDisplayed()
    }

    private fun capture(name: String) {
        compose.waitForIdle()
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val screenshot = instrumentation.uiAutomation.takeScreenshot()
        val file = File(instrumentation.targetContext.getExternalFilesDir(null), name)
        FileOutputStream(file).use { screenshot.compress(Bitmap.CompressFormat.PNG, 100, it) }
        screenshot.recycle()
    }
}
