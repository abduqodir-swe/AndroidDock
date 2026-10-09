package dev.androiddock.prototype

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.res.Configuration
import dev.androiddock.core.model.*
import java.time.DayOfWeek
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle as DateTextStyle
import java.util.Locale

internal fun DockAccent.color() = when (this) {
    DockAccent.WHITE -> Color(0xFFF5F3ED)
    DockAccent.AMBER -> Color(0xFFFFCA80)
    DockAccent.RED -> Color(0xFFFF8980)
}

@Composable
internal fun currentDockOrientation(): DockOrientation =
    if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) DockOrientation.LANDSCAPE else DockOrientation.PORTRAIT

@Composable
fun DockScreen(
    state: ClockState,
    modifier: Modifier = Modifier,
    settings: DockSettings = DockSettings(),
    battery: Availability<BatteryState> = Availability.Unavailable,
) {
    val profile = settings.profile()
    val orientation = currentDockOrientation()
    val layout = profile.layout(orientation)
    val time = Instant.ofEpochMilli(state.epochMillis).atZone(ZoneId.systemDefault())
    Box(modifier.fillMaxSize().background(Color.Black).safeDrawingPadding().padding(24.dp)) {
        if (layout.kind == LayoutKind.SOLO) {
            DockPanel(layout.first, profile, time, battery, Modifier.fillMaxSize())
        } else if (orientation == DockOrientation.PORTRAIT) {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(layout.spacing.dp)) {
                DockPanel(layout.first, profile, time, battery, Modifier.weight(layout.firstWeight).fillMaxWidth())
                DockPanel(layout.second, profile, time, battery, Modifier.weight(1f - layout.firstWeight).fillMaxWidth())
            }
        } else {
            Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(layout.spacing.dp)) {
                DockPanel(layout.first, profile, time, battery, Modifier.weight(layout.firstWeight).fillMaxHeight())
                DockPanel(layout.second, profile, time, battery, Modifier.weight(1f - layout.firstWeight).fillMaxHeight())
            }
        }
    }
}

@Composable
private fun DockPanel(
    config: PanelConfig, profile: DockProfile, time: java.time.ZonedDateTime,
    battery: Availability<BatteryState>, modifier: Modifier,
) {
    val color = config.accent.color()
    Box(modifier.background(
        if (config.surface) Color(0xFF17191C) else Color.Transparent,
        RoundedCornerShape(if (config.rounded) 16.dp else 0.dp),
    ).padding(12.dp), contentAlignment = Alignment.Center) {
        when (config.widget) {
            WidgetKind.CLOCK -> Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                val format = (if (profile.use24Hour) "HH:mm" else "hh:mm") + (if (profile.showSeconds) ":ss" else "")
                BasicText(time.format(DateTimeFormatter.ofPattern(format, Locale.getDefault())),
                    Modifier.fillMaxWidth().testTag("clock"),
                    style = TextStyle(color = color, fontFamily = FontFamily.Monospace, textAlign = TextAlign.Center),
                    maxLines = 1, autoSize = TextAutoSize.StepBased(24.sp, (128 * config.textScale).sp))
                if (!profile.use24Hour) Text(time.format(DateTimeFormatter.ofPattern("a", Locale.getDefault())), color = color, fontSize = 18.sp)
                if (profile.showDate) Text(time.format(DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.getDefault())), color = color.copy(alpha = .8f), fontSize = 16.sp, textAlign = TextAlign.Center)
                if (profile.showBattery) BatteryLabel(battery, color)
            }
            WidgetKind.CALENDAR -> MonthCalendar(YearMonth.from(time), time.toLocalDate(), DayOfWeek.of(profile.firstDayOfWeek), color, config.textScale)
            WidgetKind.BATTERY -> BatteryLabel(battery, color, large = true)
        }
    }
}

@Composable
internal fun BatteryLabel(state: Availability<BatteryState>, color: Color, large: Boolean = false) {
    val label = when (state) {
        is Availability.Data -> "${state.value.percent}% · ${if (state.value.charging) "Заряжается" else "От батареи"}"
        Availability.NoPermission, Availability.Unavailable -> "Батарея недоступна"
    }
    Text(label, Modifier.testTag("battery"), color = color.copy(alpha = .8f), fontSize = if (large) 28.sp else 14.sp, textAlign = TextAlign.Center)
}

@Composable
private fun MonthCalendar(month: YearMonth, today: java.time.LocalDate, firstDay: DayOfWeek, color: Color, scale: Float) {
    val grid = monthGrid(month, firstDay)
    BoxWithConstraints(Modifier.fillMaxSize().testTag("calendar")) {
        val daySize = minOf(maxWidth.value / 7f, (maxHeight.value - 52f).coerceAtLeast(84f) / (grid.cells.size / 7 + 1))
        Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(month.format(DateTimeFormatter.ofPattern("LLLL yyyy", Locale.getDefault())).replaceFirstChar { it.titlecase(Locale.getDefault()) }, color = color, fontWeight = FontWeight.Medium, fontSize = (22 * scale).sp, modifier = Modifier.padding(bottom = 16.dp))
            Row {
                grid.weekdays.forEach { weekday ->
                    Box(Modifier.size(daySize.dp), contentAlignment = Alignment.Center) {
                        Text(weekday.getDisplayName(DateTextStyle.SHORT_STANDALONE, Locale.getDefault()), color = color.copy(alpha = .7f), fontSize = (12 * scale).sp)
                    }
                }
            }
            grid.cells.chunked(7).forEach { week ->
                Row {
                    week.forEach { date ->
                        Box(Modifier.size(daySize.dp).padding(2.dp).background(if (date == today) color else Color.Transparent, CircleShape)
                            .then(if (date == today) Modifier.testTag("today").semantics { contentDescription = "Сегодня, $date" } else Modifier), contentAlignment = Alignment.Center) {
                            if (date != null) Text(date.dayOfMonth.toString(), color = if (date == today) Color.Black else color, fontSize = (16 * scale).sp)
                        }
                    }
                }
            }
        }
    }
}
