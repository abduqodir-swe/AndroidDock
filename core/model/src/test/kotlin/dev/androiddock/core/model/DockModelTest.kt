package dev.androiddock.core.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import org.junit.Assert.*
import org.junit.Test

class DockModelTest {
    @Test fun leapFebruaryStartsInCorrectColumnAndContainsEveryDay() {
        val grid = monthGrid(YearMonth.of(2024, 2), DayOfWeek.MONDAY)
        assertEquals(3, grid.cells.indexOf(LocalDate.of(2024, 2, 1)))
        assertEquals(29, grid.cells.count { it != null })
        assertEquals(0, grid.cells.size % 7)
    }

    @Test fun sundayFirstShiftsGridAndWeekdayLabelsTogether() {
        val grid = monthGrid(YearMonth.of(2026, 11), DayOfWeek.SUNDAY)
        assertEquals(DayOfWeek.SUNDAY, grid.weekdays.first())
        assertEquals(LocalDate.of(2026, 11, 1), grid.cells.first())
        assertEquals(30, grid.cells.count { it != null })
    }

    @Test fun sixWeekMonthIsNotTruncated() {
        val grid = monthGrid(YearMonth.of(2026, 3), DayOfWeek.MONDAY)
        assertEquals(42, grid.cells.size)
        assertTrue(grid.cells.contains(LocalDate.of(2026, 3, 31)))
    }

    @Test fun profilesAndOrientationLayoutsSurviveSerializationIndependently() {
        val original = DockSettings().let { settings ->
            settings.withProfile(DockMode.DESKTOP, settings.desktop.withLayout(DockOrientation.PORTRAIT,
                DockLayout(kind = LayoutKind.SOLO, first = PanelConfig(WidgetKind.BATTERY), firstWeight = .65f)))
                .copy(mode = DockMode.BEDROOM)
        }
        val restored = SettingsCodec.decode(SettingsCodec.encode(original))
        assertEquals(original, restored)
        assertEquals(LayoutKind.DUO, restored.desktop.landscape.kind)
        assertEquals(LayoutKind.DUO, restored.bedroom.portrait.kind)
    }

    @Test fun oldSettingsMissingNewFieldsGetDefaults() {
        assertEquals(DockSettings(), SettingsCodec.decode("{}"))
    }

    @Test(expected = IllegalArgumentException::class) fun unsupportedVersionIsNotSilentlyOverwritten() {
        SettingsCodec.decode("{\"schemaVersion\":2}")
    }

    @Test(expected = IllegalArgumentException::class) fun invalidPanelProportionIsRejected() {
        SettingsCodec.encode(DockSettings(desktop = DockProfile(portrait = DockLayout(firstWeight = .99f))))
    }

    @Test fun batteryScalesAndUnavailableValuesAreHandled() {
        assertEquals(75, batteryPercent(150, 200))
        assertEquals(100, batteryPercent(Int.MAX_VALUE, Int.MAX_VALUE))
        assertNull(batteryPercent(-1, 100))
        assertNull(batteryPercent(20, 0))
        assertNull(batteryPercent(101, 100))
    }
}
