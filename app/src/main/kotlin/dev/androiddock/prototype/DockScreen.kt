package dev.androiddock.prototype

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.foundation.Image
import androidx.compose.material3.TextButton
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.graphics.BitmapFactory
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
    DockBackdrop(profile)
    Box(Modifier.fillMaxSize().then(if (preview) Modifier else Modifier.safeDrawingPadding()).padding(if (preview || orientation == DockOrientation.LANDSCAPE) 8.dp else 24.dp).offset { IntOffset(shift.first, shift.second) }) {
        if (layout.kind == LayoutKind.SOLO) {
            DockPanel(layout.first, profile, time, battery, Modifier.fillMaxSize(), monthOffset, onMonthChange)
        } else if (orientation == DockOrientation.PORTRAIT) {
            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(layout.spacing.dp)) {
                DockPanel(layout.first, profile, time, battery, Modifier.weight(layout.firstWeight).fillMaxWidth(), monthOffset, onMonthChange)
                DockPanel(layout.second, profile, time, battery, Modifier.weight(1f - layout.firstWeight).fillMaxWidth(), monthOffset, onMonthChange)
            }
        } else {
            Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(layout.spacing.dp)) {
                DockPanel(layout.first, profile, time, battery, Modifier.weight(layout.firstWeight).fillMaxHeight(), monthOffset, onMonthChange)
                DockPanel(layout.second, profile, time, battery, Modifier.weight(1f - layout.firstWeight).fillMaxHeight(), monthOffset, onMonthChange)
            }
        }
    }
    }
}

@Composable
private fun DockBackdrop(profile: DockProfile) {
    val color = when (profile.background) {
        DockBackground.BLACK, DockBackground.PHOTO -> Color.Black
        DockBackground.GRAPHITE -> Color(0xFF15191D)
        DockBackground.BLUE -> Color(0xFF0C1A28)
    }
    Box(Modifier.fillMaxSize().background(color))
    if (profile.background == DockBackground.PHOTO) {
        val context = LocalContext.current
        val bitmap by produceState<android.graphics.Bitmap?>(null, profile.photoPath) {
            value = withContext(Dispatchers.IO) {
                context.appContainer.photos.resolve(profile.photoPath)?.let { file ->
                    try { BitmapFactory.decodeFile(file.path) } catch (_: Exception) { null }
                }
            }
        }
        bitmap?.let { Image(it.asImageBitmap(), contentDescription = null, Modifier.fillMaxSize().testTag("photo-background"), contentScale = ContentScale.Crop) }
        // Fixed scrim keeps light numerals readable on arbitrary photographs.
        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = .72f)))
        if (bitmap == null) Text("Фото недоступно", color = Color.White, modifier = Modifier.padding(24.dp))
    }
}

@Composable
private fun DockPanel(
    config: PanelConfig, profile: DockProfile, time: java.time.ZonedDateTime,
    battery: Availability<BatteryState>, modifier: Modifier, monthOffset: Int, onMonthChange: ((Int) -> Unit)?,
) {
    val color = config.accent.color()
    Box(modifier.background(
        if (config.surface) Color(0xFF17191C) else Color.Transparent,
        RoundedCornerShape(if (config.rounded) 16.dp else 0.dp),
    ).padding(8.dp), contentAlignment = Alignment.Center) {
        when (config.widget) {
            WidgetKind.CLOCK -> Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ClockFace(time, profile, color, config.textScale, Modifier.weight(1f).fillMaxWidth())
                if (!profile.use24Hour) Text(time.format(DateTimeFormatter.ofPattern("a", Locale.getDefault())), color = color, fontSize = 18.sp)
                if (profile.showDate) Text(time.format(DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.getDefault())), color = color.copy(alpha = .8f), fontSize = 16.sp, textAlign = TextAlign.Center)
                if (profile.showBattery) BatteryLabel(battery, color)
            }
            WidgetKind.CALENDAR -> MonthCalendar(YearMonth.from(time).plusMonths(monthOffset.toLong()), time.toLocalDate(), DayOfWeek.of(profile.firstDayOfWeek), color, config.textScale, monthOffset, onMonthChange)
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
private fun MonthCalendar(month: YearMonth, today: java.time.LocalDate, firstDay: DayOfWeek, color: Color, scale: Float, monthOffset: Int, onMonthChange: ((Int) -> Unit)?) {
    val grid = monthGrid(month, firstDay)
    BoxWithConstraints(Modifier.fillMaxSize().testTag("calendar")) {
        val daySize = minOf(maxWidth.value / 7f, (maxHeight.value - 60f).coerceAtLeast(84f) / (grid.cells.size / 7 + 1))
        Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onMonthChange != null) TextButton(onClick = { onMonthChange(monthOffset - 1) }, contentPadding = PaddingValues(0.dp), modifier = Modifier.size(48.dp).semantics { contentDescription = "Предыдущий месяц" }) { Text("−", color = color) }
                BasicText(month.format(DateTimeFormatter.ofPattern("LLLL yyyy", Locale.getDefault())).replaceFirstChar { it.titlecase(Locale.getDefault()) }, Modifier.weight(1f).padding(bottom = 8.dp), style = TextStyle(color = color, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center), maxLines = 1, autoSize = TextAutoSize.StepBased(8.sp, (20 * scale).sp))
                if (onMonthChange != null) TextButton(onClick = { onMonthChange(monthOffset + 1) }, contentPadding = PaddingValues(0.dp), modifier = Modifier.size(48.dp).semantics { contentDescription = "Следующий месяц" }) { Text("+", color = color) }
            }
            Row {
                grid.weekdays.forEach { weekday ->
                    Box(Modifier.size(daySize.dp), contentAlignment = Alignment.Center) {
                        BasicText(weekday.getDisplayName(DateTextStyle.SHORT_STANDALONE, Locale.getDefault()), Modifier.fillMaxWidth(), style = TextStyle(color = color.copy(alpha = .7f), textAlign = TextAlign.Center), maxLines = 1, autoSize = TextAutoSize.StepBased(6.sp, (12 * scale).sp))
                    }
                }
            }
            grid.cells.chunked(7).forEach { week ->
                Row {
                    week.forEach { date ->
                        Box(Modifier.size(daySize.dp).padding(2.dp).background(if (date == today) color else Color.Transparent, CircleShape)
                            .then(if (date == today) Modifier.testTag("today").semantics { contentDescription = "Сегодня, $date" } else Modifier), contentAlignment = Alignment.Center) {
                            if (date != null) BasicText(date.dayOfMonth.toString(), Modifier.testTag("day-${date.dayOfMonth}"), style = TextStyle(color = if (date == today) Color.Black else color, textAlign = TextAlign.Center), maxLines = 1, autoSize = TextAutoSize.StepBased(6.sp, (16 * scale).sp))
                        }
                    }
                }
            }
        }
    }
}
