package dev.androiddock.prototype

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.IconButton
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
    orientationOverride: DockOrientation? = null,
    preview: Boolean = false,
    monthOffset: Int = 0,
    onMonthChange: ((Int) -> Unit)? = null,
) {
    val profile = settings.profile()
    val orientation = orientationOverride ?: currentDockOrientation()
    val layout = profile.layout(orientation)
    val time = Instant.ofEpochMilli(state.epochMillis).atZone(ZoneId.systemDefault())
    val shift = if (profile.pixelShift && !preview) pixelShiftOffset(state.epochMillis) else 0 to 0
    Box(modifier.fillMaxSize()) {
    DockBackdrop(profile, orientation)
    Box(Modifier.fillMaxSize().then(if (preview) Modifier else Modifier.safeDrawingPadding()).padding(if (preview || orientation == DockOrientation.LANDSCAPE) 8.dp else 24.dp).offset { IntOffset(shift.first, shift.second) }) {
        if (layout.kind == LayoutKind.SOLO) {
            DockPanel(layout.first, profile, time, battery, Modifier.fillMaxSize(), monthOffset, onMonthChange)
        } else if (orientation == DockOrientation.PORTRAIT) {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(layout.spacing.dp)) {
                DockPanel(layout.first, profile, time, battery, Modifier.weight(layout.firstWeight).fillMaxWidth(), monthOffset, onMonthChange)
                if (profile.appearance == DockAppearance.STUDIO) Box(Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = .18f)))
                DockPanel(layout.second, profile, time, battery, Modifier.weight(1f - layout.firstWeight).fillMaxWidth(), monthOffset, onMonthChange)
            }
        } else {
            Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(layout.spacing.dp)) {
                DockPanel(layout.first, profile, time, battery, Modifier.weight(layout.firstWeight).fillMaxHeight(), monthOffset, onMonthChange)
                if (profile.appearance == DockAppearance.STUDIO) Box(Modifier.fillMaxHeight().width(1.dp).background(Color.White.copy(alpha = .18f)))
                DockPanel(layout.second, profile, time, battery, Modifier.weight(1f - layout.firstWeight).fillMaxHeight(), monthOffset, onMonthChange)
            }
        }
    }
    }
}

@Composable
private fun DockBackdrop(profile: DockProfile, orientation: DockOrientation) {
    val color = when (profile.background) {
        DockBackground.BLACK, DockBackground.PHOTO -> Color.Black
        DockBackground.GRAPHITE -> Color(0xFF15191D)
        DockBackground.BLUE -> Color(0xFF0C1A28)
    }
    Box(Modifier.fillMaxSize().background(color))
    if (profile.background == DockBackground.PHOTO) DockPhotoPreview(profile, orientation, Modifier.fillMaxSize())
}

@Composable
private fun DockPanel(
    config: PanelConfig, profile: DockProfile, time: java.time.ZonedDateTime,
    battery: Availability<BatteryState>, modifier: Modifier, monthOffset: Int, onMonthChange: ((Int) -> Unit)?,
) {
    val color = if (config.accent == DockAccent.WHITE && profile.appearance == DockAppearance.FLIP) Color(0xFFF3E6CB) else config.accent.color()
    Box(modifier.background(
        if (config.surface) Color(0xFF17191C) else Color.Transparent,
        RoundedCornerShape(if (config.rounded) 16.dp else 0.dp),
    ).then(if (profile.appearance == DockAppearance.GALLERY && profile.background == DockBackground.PHOTO && !config.surface) Modifier.background(Brush.verticalGradient(listOf(Color.Black.copy(alpha = .25f), Color.Black.copy(alpha = .6f), Color.Black.copy(alpha = .25f))), RoundedCornerShape(if (config.rounded) 16.dp else 0.dp)) else Modifier).padding(8.dp), contentAlignment = Alignment.Center) {
        when (config.widget) {
            WidgetKind.CLOCK -> Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ClockFace(time, profile, color, config.textScale, Modifier.weight(1f).fillMaxWidth())
                if (!profile.use24Hour) Text(time.format(DateTimeFormatter.ofPattern("a", Locale.getDefault())), color = color, fontSize = 18.sp)
                if (profile.showDate) Text(time.format(DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.getDefault())), color = color.copy(alpha = .8f), fontSize = 16.sp, textAlign = TextAlign.Center)
                if (profile.showBattery) BatteryLabel(battery, color)
            }
            WidgetKind.CALENDAR -> MonthCalendar(YearMonth.from(time).plusMonths(monthOffset.toLong()), time.toLocalDate(), DayOfWeek.of(profile.firstDayOfWeek), color, config.textScale, monthOffset, onMonthChange, profile)
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
private fun MonthCalendar(month: YearMonth, today: java.time.LocalDate, firstDay: DayOfWeek, color: Color, scale: Float, monthOffset: Int, onMonthChange: ((Int) -> Unit)?, profile: DockProfile) {
    val grid = monthGrid(month, firstDay)
    BoxWithConstraints(Modifier.fillMaxSize().testTag("calendar")) {
        val headerHeight = if (onMonthChange != null) 48f else 32f
        val daySize = minOf(maxWidth.value / 7f, (maxHeight.value - headerHeight - if (profile.appearance == DockAppearance.STUDIO) 8f else 0f).coerceAtLeast(0f) / (grid.cells.size / 7 + 1))
        Column(Modifier.fillMaxWidth().align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(Modifier.fillMaxWidth().height(headerHeight.dp), verticalAlignment = Alignment.CenterVertically) {
                if (onMonthChange != null) MonthButton(false, color) { onMonthChange(monthOffset - 1) }
                BasicText(month.format(DateTimeFormatter.ofPattern("LLLL yyyy", Locale.getDefault())).replaceFirstChar { it.titlecase(Locale.getDefault()) }, Modifier.weight(1f), style = TextStyle(color = color, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center), maxLines = 1, autoSize = TextAutoSize.StepBased(8.sp, (20 * scale).sp))
                if (onMonthChange != null) MonthButton(true, color) { onMonthChange(monthOffset + 1) }
            }
            if (profile.appearance == DockAppearance.STUDIO) Box(Modifier.fillMaxWidth().padding(vertical = 3.dp).height(1.dp).background(color.copy(alpha = .28f)))
            Row {
                grid.weekdays.forEach { weekday ->
                    Box(Modifier.size(daySize.dp), contentAlignment = Alignment.Center) {
                        BasicText(weekday.getDisplayName(DateTextStyle.SHORT_STANDALONE, Locale.getDefault()), Modifier.fillMaxWidth(), style = TextStyle(color = if (weekday == DayOfWeek.SATURDAY || weekday == DayOfWeek.SUNDAY) profile.calendarWeekendAccent?.color() ?: color.copy(alpha = .7f) else color.copy(alpha = .7f), textAlign = TextAlign.Center), maxLines = 1, autoSize = TextAutoSize.StepBased(6.sp, (12 * scale).sp))
                    }
                }
            }
            grid.cells.chunked(7).forEach { week ->
                Row {
                    week.forEach { date ->
                        val highlighted = date == today && profile.calendarHighlightToday
                        val highlightColor = if (profile.appearance == DockAppearance.LEGACY || configAccentIsColored(color)) color else when (profile.appearance) {
                            DockAppearance.STUDIO -> Color(0xFFD9C29B)
                            DockAppearance.FLIP -> Color(0xFFCBA16A)
                            else -> Color(0xFFFFCA80)
                        }
                        val dateColor = if (date?.dayOfWeek == DayOfWeek.SATURDAY || date?.dayOfWeek == DayOfWeek.SUNDAY) profile.calendarWeekendAccent?.color() ?: color else color
                        Box(Modifier.size(daySize.dp).padding(2.dp).background(if (highlighted) highlightColor else Color.Transparent, if (profile.appearance == DockAppearance.FLIP) RoundedCornerShape(8.dp) else CircleShape)
                            .then(if (date == today) Modifier.testTag("today").semantics { contentDescription = "Сегодня, $date" } else Modifier), contentAlignment = Alignment.Center) {
                            if (date != null) BasicText(date.dayOfMonth.toString(), Modifier.testTag("day-${date.dayOfMonth}"), style = TextStyle(color = if (highlighted) Color.Black else dateColor, textAlign = TextAlign.Center), maxLines = 1, autoSize = TextAutoSize.StepBased(8.sp, ((if (profile.appearance == DockAppearance.LEGACY) 16 else 20) * scale).sp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthButton(next: Boolean, color: Color, onClick: () -> Unit) {
    IconButton(onClick, Modifier.size(48.dp).semantics {
        contentDescription = if (next) "Следующий месяц" else "Предыдущий месяц"
    }) {
        Canvas(Modifier.size(24.dp)) {
            val edge = size.width * if (next) .4f else .6f
            val tip = size.width * if (next) .65f else .35f
            val middle = Offset(tip, size.height * .5f)
            drawLine(color, Offset(edge, size.height * .25f), middle, 2.dp.toPx(), StrokeCap.Round)
            drawLine(color, middle, Offset(edge, size.height * .75f), 2.dp.toPx(), StrokeCap.Round)
        }
    }
}

private fun configAccentIsColored(color: Color) = color == DockAccent.AMBER.color() || color == DockAccent.RED.color()
