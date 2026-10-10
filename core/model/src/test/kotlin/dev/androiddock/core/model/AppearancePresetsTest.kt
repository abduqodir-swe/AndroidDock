package dev.androiddock.core.model

import org.junit.Assert.*
import org.junit.Test

class AppearancePresetsTest {
    @Test fun oldProfileDefaultsKeepLegacyAppearance() {
        val restored = SettingsCodec.decode("""{"desktop":{"photoPath":"custom.jpg","clockStyle":"ANALOG"}}""")
        assertEquals(DockAppearance.LEGACY, restored.desktop.appearance)
        assertEquals(ClockStyle.ANALOG, restored.desktop.clockStyle)
        assertEquals(.72f, restored.desktop.photoDim)
        assertEquals(PhotoFraming(), restored.desktop.portraitPhoto)
        assertEquals(DockProfile(photoPath = "custom.jpg", clockStyle = ClockStyle.ANALOG), restored.desktop)
    }

    @Test fun presetsReplaceBothVisualLayoutsAndPreservePersonalChoices() {
        val original = DockProfile(
            portrait = DockLayout(kind = LayoutKind.SOLO), landscape = DockLayout(first = PanelConfig(WidgetKind.BATTERY)),
            photoPath = "custom.jpg", firstDayOfWeek = 7, brightness = .31f, nightBrightness = .04f,
            nightEnabled = true, animations = false, pixelShift = false, use24Hour = false,
            showSeconds = false, showDate = false, showBattery = false,
            portraitPhoto = PhotoFraming(.1f, .9f, 2f), landscapePhoto = PhotoFraming(.8f, .2f, 1.5f),
        )
        DockAppearance.entries.filter { it != DockAppearance.LEGACY }.forEach { appearance ->
            val applied = original.withAppearance(appearance)
            assertEquals(LayoutKind.DUO, applied.portrait.kind)
            assertEquals(applied.portrait, applied.landscape)
            assertEquals(WidgetKind.CLOCK, applied.portrait.first.widget)
            assertEquals(WidgetKind.CALENDAR, applied.portrait.second.widget)
            assertEquals(DockAccent.WHITE, applied.portrait.second.accent)
            assertEquals(original, applied.copy(
                appearance = original.appearance, portrait = original.portrait, landscape = original.landscape,
                clockStyle = original.clockStyle, background = original.background, photoDim = original.photoDim,
            ))
        }
        assertEquals(ClockStyle.ANALOG, original.withAppearance(DockAppearance.STUDIO).clockStyle)
        assertEquals(ClockStyle.FLIP, original.withAppearance(DockAppearance.FLIP).clockStyle)
        assertEquals(DockBackground.PHOTO, original.withAppearance(DockAppearance.GALLERY).background)
        assertEquals(original, original.withAppearance(DockAppearance.LEGACY))
    }

    @Test fun appearanceAndCropsRoundTripIndependentlyPerModeAndOrientation() {
        val desktop = DockProfile().withAppearance(DockAppearance.GALLERY)
            .withPhotoFraming(DockOrientation.PORTRAIT, PhotoFraming(.2f, .7f, 2f))
            .copy(calendarHighlightToday = false, calendarWeekendAccent = DockAccent.RED)
        val settings = DockSettings(desktop = desktop, bedroom = DockProfile().withAppearance(DockAppearance.STUDIO))
        assertEquals(settings, SettingsCodec.decode(SettingsCodec.encode(settings)))
        assertEquals(PhotoFraming(), desktop.photoFraming(DockOrientation.LANDSCAPE))
    }

    @Test fun invalidPhotoSettingsAreRejected() {
        listOf(Float.NaN, Float.POSITIVE_INFINITY, -.1f, 1.1f).forEach { invalid ->
            rejected(DockProfile(portraitPhoto = PhotoFraming(x = invalid)))
            rejected(DockProfile(landscapePhoto = PhotoFraming(y = invalid)))
        }
        listOf(Float.NaN, Float.POSITIVE_INFINITY, .9f, 3.1f).forEach { rejected(DockProfile(portraitPhoto = PhotoFraming(zoom = it))) }
        listOf(Float.NaN, Float.POSITIVE_INFINITY, -.1f, .91f).forEach { rejected(DockProfile(photoDim = it)) }
    }

    @Test fun cropEdgesAndZoomStayWithinSourceWithMatchingAspect() {
        val centered = photoCrop(1600, 900, 900, 1600, PhotoFraming())
        assertEquals(PhotoCrop(547, 0, 506, 900), centered)
        val left = photoCrop(1600, 900, 900, 1600, PhotoFraming(0f, 0f, 2f))
        val right = photoCrop(1600, 900, 900, 1600, PhotoFraming(1f, 1f, 2f))
        assertEquals(0, left.left)
        assertEquals(0, left.top)
        assertEquals(1600, right.left + right.width)
        assertEquals(900, right.top + right.height)
        assertEquals(PhotoCrop(0, 0, 900, 1600), photoCrop(900, 1600, 900, 1600, PhotoFraming()))
    }

    private fun rejected(profile: DockProfile) {
        try { DockSettings(desktop = profile).validated(); fail("Invalid photo settings accepted") }
        catch (_: IllegalArgumentException) { }
    }
}
