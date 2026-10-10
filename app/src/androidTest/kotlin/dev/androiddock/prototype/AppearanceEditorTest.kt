package dev.androiddock.prototype

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.longClick
import androidx.lifecycle.ViewModelProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import dev.androiddock.core.model.*
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppearanceEditorTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private val container get() = InstrumentationRegistry.getInstrumentation().targetContext.appContainer
    private lateinit var originalSettings: DockSettings
    private val createdPhotos = mutableListOf<File>()
    private val originalPhotoBackups = mutableMapOf<String, File>()

    @Before fun prepare() {
        originalSettings = runBlocking {
            (container.repository.state.first { it is SettingsState.Ready } as SettingsState.Ready).settings
        }
        setOfNotNull(originalSettings.bedroom.photoPath, originalSettings.desktop.photoPath).forEach { name ->
            container.photos.resolve(name)?.takeIf(File::isFile)?.let { source ->
                val backup = File.createTempFile("appearance-original-photo", ".jpg", source.parentFile!!.parentFile)
                source.copyTo(backup, overwrite = true)
                originalPhotoBackups[name] = backup
            }
        }
        runBlocking { container.repository.update { DockSettings() } }
        waitForSaved { it == DockSettings() }
        rotate(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, Configuration.ORIENTATION_PORTRAIT)
        compose.waitUntil(5_000) { compose.onAllNodesWithTag("dock-display").fetchSemanticsNodes().isNotEmpty() }
    }

    @After fun restore() {
        compose.activityRule.scenario.onActivity { activity ->
            val model = ViewModelProvider(activity)[ClockViewModel::class.java]
            model.cancelLockWallpaper()
            model.cancelPhotoPick()
            model.editor.cancel()
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
        originalPhotoBackups.forEach { (name, backup) ->
            backup.copyTo(requireNotNull(container.photos.resolve(name)), overwrite = true)
            backup.delete()
        }
        originalPhotoBackups.clear()
        runBlocking { container.repository.update { originalSettings } }
        waitForSaved { it == originalSettings }
        createdPhotos.forEach(File::delete)
    }

    @Test fun presetSaveAndCancelKeepIndependentModesAndComfortSettings() {
        runBlocking { container.repository.update { it.copy(desktop = it.desktop.copy(nightEnabled = true, nightBrightness = .18f)) } }
        waitForSaved { it.desktop.nightEnabled && it.desktop.nightBrightness == .18f }
        val before = saved()
        openEditor()
        compose.onNodeWithTag("editor-context").performClick()
        compose.onNodeWithTag("Оформление:Midnight").performScrollTo().performClick()
        compose.onNodeWithTag("Оформление:Flip").performScrollTo().performClick()
        compose.onNodeWithTag("editor-context-done").performClick()
        compose.onNodeWithText("Отмена").performClick()
        assertEquals(before, saved())

        openEditor()
        compose.onNodeWithTag("editor-context").performClick()
        compose.onNodeWithTag("Оформление:Studio").performScrollTo().performClick()
        compose.onNodeWithTag("editor-context-done").performClick()
        compose.onNodeWithTag("save-editor").performClick()
        compose.waitUntil(5_000) { saved().desktop.appearance == DockAppearance.STUDIO }
        assertEquals(ClockStyle.ANALOG, saved().desktop.clockStyle)
        assertTrue(saved().desktop.nightEnabled)
        assertEquals(.18f, saved().desktop.nightBrightness)
        assertEquals(before.bedroom, saved().bedroom)
        compose.activityRule.scenario.recreate()
        assertEquals(DockAppearance.STUDIO, saved().desktop.appearance)
        compose.onNodeWithTag("clock").assertIsDisplayed()
    }

    @Test fun editedOrientationRemainsExplicitAcrossPhysicalRotation() {
        openEditor()
        compose.onNodeWithTag("editor-context").performClick()
        compose.onNodeWithTag("Редактируемая раскладка:Ландшафт").performScrollTo().performClick()
        assertEquals(DockOrientation.LANDSCAPE, draft().orientation)
        rotate(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, Configuration.ORIENTATION_LANDSCAPE)
        assertEquals(DockOrientation.LANDSCAPE, draft().orientation)
        compose.onNodeWithTag("Редактируемая раскладка:Портрет").performScrollTo().performClick()
        assertEquals(DockOrientation.PORTRAIT, draft().orientation)
        compose.onNodeWithTag("editor-context-done").performClick()
        rotate(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, Configuration.ORIENTATION_PORTRAIT)
        assertEquals(DockOrientation.PORTRAIT, draft().orientation)
        compose.onNodeWithText("Компоновка").performScrollTo().performClick()
        compose.onNodeWithTag("Компоновка:Одна панель").performScrollTo().performClick()
        compose.onNodeWithTag("save-editor").performClick()
        compose.waitUntil(5_000) { saved().desktop.portrait.kind == LayoutKind.SOLO }
        assertEquals(LayoutKind.DUO, saved().desktop.landscape.kind)
    }

    @Test fun calendarChangesAndCropResetSaveWithoutChangingOtherOrientation() {
        val photo = testPhoto()
        runBlocking {
            container.repository.update {
                it.copy(desktop = it.desktop.copy(background = DockBackground.PHOTO, photoPath = photo.name,
                    portraitPhoto = PhotoFraming(.2f, .8f, 2f), landscapePhoto = PhotoFraming(.8f, .2f, 1.6f)))
            }
        }
        waitForSaved { it.desktop.photoPath == photo.name }
        openEditor()
        compose.onNodeWithTag("editor-tab-1").performClick()
        compose.onNodeWithTag("Первый день недели:Воскресенье").performScrollTo().performClick()
        compose.onNodeWithTag("Цвет выходных:Красный").performScrollTo().performClick()
        compose.onNodeWithTag("Выделять сегодня").performScrollTo().performClick()
        compose.onNodeWithTag("editor-tab-2").performClick()
        compose.onNodeWithText("Сбросить кадрирование").performScrollTo().performClick()
        assertEquals(PhotoFraming(), draft().settings.desktop.portraitPhoto)
        assertEquals(PhotoFraming(.8f, .2f, 1.6f), draft().settings.desktop.landscapePhoto)
        compose.onNodeWithTag("save-editor").performClick()
        compose.waitUntil(5_000) { saved().desktop.portraitPhoto == PhotoFraming() }
        assertEquals(7, saved().desktop.firstDayOfWeek)
        assertEquals(DockAccent.RED, saved().desktop.calendarWeekendAccent)
        assertFalse(saved().desktop.calendarHighlightToday)
        assertEquals(PhotoFraming(.8f, .2f, 1.6f), saved().desktop.landscapePhoto)
    }

    @Test fun cancelledPhotoAndWallpaperRequestsLeaveSettingsUntouched() {
        val photo = testPhoto()
        runBlocking { container.repository.update { it.copy(desktop = it.desktop.copy(photoPath = photo.name, background = DockBackground.PHOTO)) } }
        waitForSaved { it.desktop.photoPath == photo.name }
        val before = saved()
        openEditor()
        val model = viewModel()
        assertTrue(model.beginPhotoPick(gallery = true))
        model.cancelPhotoPick() // Same callback path as a picker returning null.
        model.importPhoto(container, Uri.parse("content://invalid/cancelled"))
        assertEquals(before, draft().settings)

        compose.onNodeWithTag("editor-tab-2").performClick()
        compose.onNodeWithTag("lock-wallpaper").performScrollTo().performClick()
        compose.waitUntil(5_000) { model.lockRequest.value != null }
        model.cancelLockWallpaper() // Dismissal path; no wallpaper adapter is called.
        assertNull(model.lockRequest.value)
        assertFalse(model.busy.value)
        assertEquals(before, draft().settings)
        assertEquals(before, saved())
    }

    @Test fun captureNativeAppearanceAndEditorMatrixWithSyntheticPhoto() {
        val photo = testPhoto()
        for (appearance in listOf(DockAppearance.MIDNIGHT, DockAppearance.STUDIO, DockAppearance.FLIP, DockAppearance.GALLERY)) {
            runBlocking {
                container.repository.update { settings ->
                    settings.copy(desktop = settings.desktop.copy(photoPath = photo.name).withAppearance(appearance))
                }
            }
            waitForSaved { it.desktop.appearance == appearance }
            for ((requested, expected, suffix) in listOf(
                Triple(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, Configuration.ORIENTATION_PORTRAIT, "portrait"),
                Triple(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, Configuration.ORIENTATION_LANDSCAPE, "landscape"),
            )) {
                rotate(requested, expected)
                compose.onNodeWithTag("clock").assertIsDisplayed()
                captureRoot("theme-${appearance.name.lowercase()}-$suffix.png")
            }
        }

        rotate(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, Configuration.ORIENTATION_PORTRAIT)
        openEditor()
        for (tab in 0..2) {
            compose.onNodeWithTag("editor-tab-$tab").performClick()
            captureRoot("editor-$tab-portrait.png")
        }
        rotate(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, Configuration.ORIENTATION_LANDSCAPE)
        compose.onNodeWithTag("editor-context").performClick()
        compose.onNodeWithTag("Редактируемая раскладка:Ландшафт").performScrollTo().performClick()
        compose.onNodeWithTag("editor-context-done").performClick()
        for (tab in 0..2) {
            compose.onNodeWithTag("editor-tab-$tab").performClick()
            captureRoot("editor-$tab-landscape.png")
        }
    }

    @Test fun captureClockEditorThumbnailsAfterFinalRenderingChange() {
        val photo = testPhoto()
        runBlocking {
            container.repository.update { settings ->
                settings.copy(desktop = settings.desktop.copy(photoPath = photo.name).withAppearance(DockAppearance.GALLERY))
            }
        }
        waitForSaved { it.desktop.appearance == DockAppearance.GALLERY }
        openEditor()
        captureRoot("editor-0-portrait.png")
        rotate(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, Configuration.ORIENTATION_LANDSCAPE)
        compose.onNodeWithTag("editor-context").performClick()
        compose.onNodeWithTag("Редактируемая раскладка:Ландшафт").performScrollTo().performClick()
        compose.onNodeWithTag("editor-context-done").performClick()
        captureRoot("editor-0-landscape.png")
    }

    private fun saved() = (container.repository.state.value as SettingsState.Ready).settings
    private fun waitForSaved(predicate: (DockSettings) -> Boolean) {
        compose.waitUntil(5_000) { (container.repository.state.value as? SettingsState.Ready)?.settings?.let(predicate) == true }
    }
    private fun viewModel(): ClockViewModel {
        lateinit var model: ClockViewModel
        compose.activityRule.scenario.onActivity { model = ViewModelProvider(it)[ClockViewModel::class.java] }
        return model
    }
    private fun draft() = requireNotNull(viewModel().editor.state.value)
    private fun openEditor() {
        compose.onNodeWithTag("dock-display").performTouchInput { longClick() }
        compose.onNodeWithText("Мой экран").assertIsDisplayed()
    }
    private fun rotate(requested: Int, expected: Int) {
        compose.activityRule.scenario.onActivity { it.requestedOrientation = requested }
        compose.waitUntil(5_000) { compose.activity.resources.configuration.orientation == expected }
        compose.waitForIdle()
    }
    private fun testPhoto(): File {
        val photo = requireNotNull(container.photos.resolve("appearance-test-${System.nanoTime()}.jpg"))
        photo.parentFile!!.mkdirs()
        val bitmap = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(android.graphics.Color.BLUE)
        photo.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it) }
        bitmap.recycle()
        createdPhotos += photo
        return photo
    }
    private fun captureRoot(name: String) {
        compose.waitForIdle()
        val file = File(requireNotNull(InstrumentationRegistry.getInstrumentation().targetContext.getExternalFilesDir(null)), name)
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }
}
