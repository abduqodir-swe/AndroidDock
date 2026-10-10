package dev.androiddock.prototype

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import dev.androiddock.core.model.*
import java.time.Instant
import java.time.ZoneId
import kotlin.math.cos
import kotlin.math.sin

internal fun DockMode.label() = if (this == DockMode.BEDROOM) "Спальня" else "Стол"
internal fun WidgetKind.label() = when (this) { WidgetKind.CLOCK -> "Часы"; WidgetKind.CALENDAR -> "Календарь"; WidgetKind.BATTERY -> "Батарея" }
internal fun ClockStyle.label() = when (this) { ClockStyle.DIGITAL -> "Цифровые"; ClockStyle.ANALOG -> "Аналоговые"; ClockStyle.FLIP -> "Flip" }
internal fun DockBackground.label() = when (this) { DockBackground.BLACK -> "Чёрный"; DockBackground.GRAPHITE -> "Графит"; DockBackground.BLUE -> "Тёмно-синий"; DockBackground.PHOTO -> "Моё фото" }
internal fun DockAccent.label() = when (this) { DockAccent.WHITE -> "Светлый"; DockAccent.AMBER -> "Янтарный"; DockAccent.RED -> "Красный" }
private val LocalEditingEnabled = staticCompositionLocalOf { true }
private val EditorColors = darkColorScheme(
    primary = Color(0xFFF2BD68), onPrimary = Color(0xFF251A09),
    surface = Color(0xFF101214), onSurface = Color(0xFFF2F1EF),
    surfaceVariant = Color(0xFF292C30), onSurfaceVariant = Color(0xFFC8C8C8),
    secondaryContainer = Color(0xFF4B381C), onSecondaryContainer = Color(0xFFFFDDA3),
    outline = Color(0xFF8E9196),
)

@Composable
fun DockEditor(
    draft: DockEditorState, clock: ClockState, battery: Availability<BatteryState>,
    busy: Boolean, error: String?, onChange: ((DockSettings) -> DockSettings) -> Unit,
    onOrientation: (DockOrientation) -> Unit, onUndo: () -> Unit,
    onCancel: () -> Unit, onSave: () -> Unit, onPhoto: () -> Unit,
    onLockWallpaper: () -> Unit = {}, onGalleryPhoto: () -> Unit = onPhoto,
) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var compositionOpen by rememberSaveable { mutableStateOf(false) }
    var comfortOpen by rememberSaveable { mutableStateOf(false) }
    var contextOpen by rememberSaveable { mutableStateOf(false) }
    val contextScroll = rememberScrollState()
    val clockScroll = rememberScrollState()
    val calendarScroll = rememberScrollState()
    val backgroundScroll = rememberScrollState()
    val presetScroll = rememberScrollState()
    val mode = draft.settings.mode
    val profile = draft.settings.profile()
    val layout = profile.layout(draft.orientation)
    fun profileChange(transform: (DockProfile) -> DockProfile) = onChange { it.withProfile(mode, transform(it.profile(mode))) }
    fun layoutChange(transform: (DockLayout) -> DockLayout) = profileChange { it.withLayout(draft.orientation, transform(it.layout(draft.orientation))) }
    fun widgetChange(kind: WidgetKind, transform: (PanelConfig) -> PanelConfig) = layoutChange {
        it.copy(first = if (it.first.widget == kind) transform(it.first) else it.first,
            second = if (it.second.widget == kind) transform(it.second) else it.second)
    }

    MaterialTheme(colorScheme = EditorColors) {
        CompositionLocalProvider(LocalEditingEnabled provides !busy) {
            if (contextOpen) AlertDialog(
                onDismissRequest = { if (!busy) contextOpen = false },
                title = { Text("Раскладка и оформление") },
                text = {
                    Column(Modifier.verticalScroll(contextScroll), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Режим и ориентация", style = MaterialTheme.typography.labelLarge)
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            DockMode.entries.forEach { value ->
                                EditorChoiceChip("Режим", value.label(), value == mode) { onChange { it.copy(mode = value) } }
                            }
                            DockOrientation.entries.forEach { value ->
                                EditorChoiceChip("Редактируемая раскладка", if (value == DockOrientation.PORTRAIT) "Портрет" else "Ландшафт", value == draft.orientation) { onOrientation(value) }
                            }
                        }
                        Row(Modifier.fillMaxWidth().horizontalScroll(presetScroll), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            DockAppearance.entries.filter { it != DockAppearance.LEGACY }.forEach { value ->
                                val selected = profile.appearance == value
                                val customized = selected && profile != profile.withAppearance(value)
                                EditorChoiceChip("Оформление", value.name.lowercase().replaceFirstChar(Char::titlecase), selected,
                                    state = if (selected) { if (customized) "Выбрано, ваши изменения" else "Выбрано, исходный стиль" } else "Не выбрано") {
                                    if (value == DockAppearance.GALLERY && profile.photoPath == null) { contextOpen = false; onGalleryPhoto() }
                                    else profileChange { it.withAppearance(value) }
                                }
                            }
                        }
                        Text("Оформление применяется к обеим ориентациям выбранного режима. Затем можно изменить отдельные настройки.", style = MaterialTheme.typography.bodySmall)
                    }
                },
                confirmButton = { TextButton(onClick = { contextOpen = false }, enabled = !busy, modifier = Modifier.heightIn(min = 48.dp).testTag("editor-context-done")) { Text("Готово") } },
            )
            Surface(Modifier.fillMaxSize()) {
                Column(Modifier.fillMaxSize().safeDrawingPadding()) {
                    Row(Modifier.fillMaxWidth().heightIn(min = 48.dp).padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Мой экран", style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f).semantics { heading() })
                        TextButton(onClick = { contextOpen = true }, enabled = !busy,
                            modifier = Modifier.heightIn(min = 48.dp).testTag("editor-context")) { Text("Раскладка") }
                    }
                    TabRow(selectedTabIndex = tab) {
                        listOf("Часы", "Календарь", "Фон").forEachIndexed { index, label ->
                            Tab(selected = tab == index, onClick = { tab = index }, enabled = !busy,
                                text = { Text(label) }, modifier = Modifier.heightIn(min = 48.dp).testTag("editor-tab-$index"))
                        }
                    }
                    if (error != null) Text(error, color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)
                            .semantics { liveRegion = LiveRegionMode.Polite })
                    BoxWithConstraints(Modifier.weight(1f)) {
                        val landscape = maxWidth > maxHeight
                        val portraitPreviewHeight = minOf(220.dp, maxHeight * .38f)
                        val preview: @Composable (Modifier) -> Unit = { modifier ->
                            Column(modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${mode.label()} · ${if (draft.orientation == DockOrientation.PORTRAIT) "Портрет" else "Ландшафт"}",
                                    style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                BoxWithConstraints(Modifier.weight(1f).fillMaxWidth().padding(top = 8.dp), contentAlignment = Alignment.Center) {
                                    val aspect = if (draft.orientation == DockOrientation.PORTRAIT) 9f / 16f else 16f / 9f
                                    val width = minOf(maxWidth, maxHeight * aspect)
                                    Surface(Modifier.width(width).height(width / aspect), shape = MaterialTheme.shapes.medium,
                                        color = Color.Black, border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) {
                                        val density = LocalDensity.current
                                        val scale = (width.value / if (draft.orientation == DockOrientation.PORTRAIT) 400f else 711f).coerceAtLeast(.05f)
                                        CompositionLocalProvider(LocalDensity provides Density(density.density * scale, density.fontScale)) {
                                            DockScreen(clock, Modifier.fillMaxSize(), draft.settings, battery,
                                                orientationOverride = draft.orientation, preview = true)
                                        }
                                    }
                                }
                            }
                        }
                        val controls: @Composable (Modifier) -> Unit = { modifier ->
                            Column(modifier.verticalScroll(when (tab) { 0 -> clockScroll; 1 -> calendarScroll; else -> backgroundScroll })
                                .padding(horizontal = 20.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                when (tab) {
                                    0 -> {
                                        ClockStyleChoices(clock, profile) { selected -> profileChange { it.copy(clockStyle = selected) } }
                                        WidgetControls(WidgetKind.CLOCK, layout) { transform -> widgetChange(WidgetKind.CLOCK, transform) }
                                        SettingSwitch("24 часа", profile.use24Hour) { value -> profileChange { it.copy(use24Hour = value) } }
                                        SettingSwitch("Секунды", profile.showSeconds) { value -> profileChange { it.copy(showSeconds = value) } }
                                        SettingSwitch("Дата в часах", profile.showDate) { value -> profileChange { it.copy(showDate = value) } }
                                        SettingSwitch("Батарея в часах", profile.showBattery) { value -> profileChange { it.copy(showBattery = value) } }
                                    }
                                    1 -> {
                                        Choice("Первый день недели", listOf(1, 7), profile.firstDayOfWeek,
                                            { if (it == 1) "Понедельник" else "Воскресенье" }) { selected -> profileChange { it.copy(firstDayOfWeek = selected) } }
                                        SettingSwitch("Выделять сегодня", profile.calendarHighlightToday) { value -> profileChange { it.copy(calendarHighlightToday = value) } }
                                        WidgetControls(WidgetKind.CALENDAR, layout) { transform -> widgetChange(WidgetKind.CALENDAR, transform) }
                                        Choice("Цвет выходных", listOf<DockAccent?>(null) + DockAccent.entries, profile.calendarWeekendAccent,
                                            { it?.label() ?: "Как будни" }) { selected -> profileChange { it.copy(calendarWeekendAccent = selected) } }
                                    }
                                    else -> {
                                        Choice("Фон AndroidDock", DockBackground.entries, profile.background, { it.label() }) { selected ->
                                            if (selected == DockBackground.PHOTO && profile.photoPath == null) onPhoto()
                                            else profileChange { it.copy(background = selected) }
                                        }
                                        OutlinedButton(onPhoto, enabled = !busy, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                                            Text(if (profile.photoPath == null) "Выбрать фотографию" else "Заменить фотографию")
                                        }
                                        Text("Фото хранится внутри приложения. Отмена выбора сохраняет текущий фон.", style = MaterialTheme.typography.bodySmall)
                                        if (profile.photoPath != null && profile.background == DockBackground.PHOTO) {
                                            val framing = profile.photoFraming(draft.orientation)
                                            Text("Кадрирование · ${if (draft.orientation == DockOrientation.PORTRAIT) "портрет" else "ландшафт"}", style = MaterialTheme.typography.titleMedium)
                                            SettingSlider("Масштаб фото", framing.zoom, 1f..3f, "${(framing.zoom * 100).toInt()}%") { value -> profileChange { it.withPhotoFraming(draft.orientation, it.photoFraming(draft.orientation).copy(zoom = value)) } }
                                            SettingSlider("По горизонтали", framing.x, 0f..1f, "${(framing.x * 100).toInt()}%") { value -> profileChange { it.withPhotoFraming(draft.orientation, it.photoFraming(draft.orientation).copy(x = value)) } }
                                            SettingSlider("По вертикали", framing.y, 0f..1f, "${(framing.y * 100).toInt()}%") { value -> profileChange { it.withPhotoFraming(draft.orientation, it.photoFraming(draft.orientation).copy(y = value)) } }
                                            TextButton(onClick = { profileChange { it.withPhotoFraming(draft.orientation, PhotoFraming()) } }, enabled = !busy,
                                                modifier = Modifier.heightIn(min = 48.dp)) { Text("Сбросить кадрирование") }
                                            SettingSlider("Затемнение", profile.photoDim, 0f.. .9f, "${(profile.photoDim * 100).toInt()}%") { value -> profileChange { it.copy(photoDim = value) } }
                                        }
                                        HorizontalDivider()
                                        Text("Обои экрана блокировки", style = MaterialTheme.typography.titleMedium)
                                        OutlinedButton(onLockWallpaper, enabled = !busy && profile.photoPath != null,
                                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp).testTag("lock-wallpaper")) { Text("На экран блокировки…") }
                                        Text("Только фото, без виджетов. Потребуется подтверждение.", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                                HorizontalDivider()
                                Expansion("Компоновка", compositionOpen) { compositionOpen = !compositionOpen }
                                if (compositionOpen) {
                                    Choice("Компоновка", LayoutKind.entries, layout.kind, { if (it == LayoutKind.DUO) "Две панели" else "Одна панель" }) { selected -> layoutChange { it.copy(kind = selected) } }
                                    PanelEditor("Панель 1", layout.first) { panel -> layoutChange { it.copy(first = panel) } }
                                    if (layout.kind == LayoutKind.DUO) {
                                        PanelEditor("Панель 2", layout.second) { panel -> layoutChange { it.copy(second = panel) } }
                                        TextButton(onClick = { layoutChange { it.copy(first = it.second, second = it.first) } }, enabled = !busy,
                                            modifier = Modifier.heightIn(min = 48.dp)) { Text("Поменять панели местами") }
                                        SettingSlider("Доля первой панели", layout.firstWeight, .3f.. .7f, "${(layout.firstWeight * 100).toInt()}%", steps = 7) { value -> layoutChange { it.copy(firstWeight = value) } }
                                    }
                                    SettingSlider("Отступы", layout.spacing.toFloat(), 0f..32f, "${layout.spacing} dp", steps = 7) { value -> layoutChange { it.copy(spacing = value.toInt()) } }
                                }
                                Expansion("Комфорт", comfortOpen) { comfortOpen = !comfortOpen }
                                if (comfortOpen) {
                                    SettingSwitch("Ночная яркость", profile.nightEnabled) { value -> profileChange { it.copy(nightEnabled = value) } }
                                    if (profile.nightEnabled) SettingSlider("Яркость ночью", profile.nightBrightness, .01f..1f, "${(profile.nightBrightness * 100).toInt()}%") { value -> profileChange { it.copy(nightBrightness = value) } }
                                    else {
                                        SettingSwitch("Системная яркость", profile.brightness == -1f) { value -> profileChange { it.copy(brightness = if (value) -1f else .5f) } }
                                        if (profile.brightness != -1f) SettingSlider("Яркость экрана", profile.brightness, .01f..1f, "${(profile.brightness * 100).toInt()}%") { value -> profileChange { it.copy(brightness = value) } }
                                    }
                                    SettingSwitch("Анимация часов", profile.animations) { value -> profileChange { it.copy(animations = value) } }
                                    SettingSwitch("Сдвиг пикселей", profile.pixelShift) { value -> profileChange { it.copy(pixelShift = value) } }
                                    Text("Медленный сдвиг содержимого снижает риск выгорания, но не исключает его.", style = MaterialTheme.typography.bodySmall)
                                }
                                TextButton(onUndo, enabled = draft.canUndo && !busy, modifier = Modifier.heightIn(min = 48.dp)) { Text("Отменить последнее изменение") }
                                Text("Настройки режима общие для обеих ориентаций. Панели и кадрирование сохраняются отдельно.", style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        if (landscape) Row(Modifier.fillMaxSize()) {
                            preview(Modifier.weight(.42f).fillMaxHeight())
                            controls(Modifier.weight(.58f).fillMaxHeight())
                        } else Column(Modifier.fillMaxSize()) {
                            preview(Modifier.fillMaxWidth().height(portraitPreviewHeight))
                            controls(Modifier.weight(1f).fillMaxWidth())
                        }
                    }
                    HorizontalDivider()
                    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onCancel, enabled = !busy, modifier = Modifier.weight(1f).heightIn(min = 48.dp)) { Text("Отмена") }
                        Button(onSave, enabled = !busy, modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("save-editor")) {
                            Text(if (busy) "Подождите…" else "Сохранить")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EditorChoiceChip(title: String, label: String, selected: Boolean, state: String? = null, onClick: () -> Unit) {
    FilterChip(selected = selected, onClick = onClick, enabled = LocalEditingEnabled.current,
        label = { Text(label) }, modifier = Modifier.heightIn(min = 48.dp).testTag("$title:$label")
            .semantics { contentDescription = "$title: $label"; if (state != null) stateDescription = state })
}

@Composable
private fun ClockStyleChoices(clock: ClockState, profile: DockProfile, onChange: (ClockStyle) -> Unit) {
    val time = Instant.ofEpochMilli(clock.epochMillis).atZone(ZoneId.systemDefault())
    Column {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ClockStyle.entries.forEach { style ->
                val selected = profile.clockStyle == style
                Surface(onClick = { onChange(style) }, enabled = LocalEditingEnabled.current,
                    shape = MaterialTheme.shapes.medium,
                    color = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(if (selected) 2.dp else 1.dp,
                        if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
                    modifier = Modifier.weight(1f).heightIn(min = 48.dp).testTag("Стиль часов:${style.label()}")
                        .semantics { this.selected = selected; role = Role.RadioButton }) {
                    Column(Modifier.padding(6.dp), horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(Modifier.fillMaxWidth().height(40.dp).clearAndSetSemantics {}) {
                            if (style == ClockStyle.ANALOG) {
                                AnalogStyleThumbnail(time.hour, time.minute, MaterialTheme.colorScheme.primary, Modifier.fillMaxSize())
                            } else {
                                ClockFace(time, profile.copy(clockStyle = style, showSeconds = false, animations = false,
                                    appearance = if (style == ClockStyle.FLIP) DockAppearance.FLIP else DockAppearance.MIDNIGHT),
                                    MaterialTheme.colorScheme.primary, 1f, Modifier.fillMaxSize())
                            }
                        }
                        Text(style.label(), style = MaterialTheme.typography.labelMedium,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    }
                }
            }
        }
    }
}

/** The small style selector uses proportional geometry rather than the full dial's padding. */
@Composable
private fun AnalogStyleThumbnail(hour: Int, minute: Int, color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val radius = minOf(size.width, size.height) * .46f
        if (radius <= 0f) return@Canvas
        val center = Offset(size.width / 2f, size.height / 2f)
        fun point(degrees: Float, distance: Float): Offset {
            val radians = Math.toRadians((degrees - 90f).toDouble())
            return Offset(center.x + cos(radians).toFloat() * distance,
                center.y + sin(radians).toFloat() * distance)
        }
        drawCircle(color.copy(alpha = .65f), radius, center, style = Stroke(radius * .035f))
        repeat(12) { tick ->
            val major = tick % 3 == 0
            drawLine(color.copy(alpha = if (major) 1f else .65f),
                point(tick * 30f, radius * if (major) .72f else .82f),
                point(tick * 30f, radius * .91f),
                strokeWidth = radius * if (major) .065f else .04f, cap = StrokeCap.Round)
        }
        drawLine(color, center, point((hour % 12 + minute / 60f) * 30f, radius * .48f),
            strokeWidth = radius * .095f, cap = StrokeCap.Round)
        drawLine(color, center, point(minute * 6f, radius * .68f),
            strokeWidth = radius * .065f, cap = StrokeCap.Round)
        drawCircle(color, radius * .075f, center)
    }
}

@Composable
private fun WidgetControls(kind: WidgetKind, layout: DockLayout, onChange: ((PanelConfig) -> PanelConfig) -> Unit) {
    val panels = listOf(layout.first) + if (layout.kind == LayoutKind.DUO) listOf(layout.second) else emptyList()
    val config = panels.firstOrNull { it.widget == kind }
    if (config == null) {
        Text("${kind.label()}: виджет отсутствует в этой раскладке. Добавьте его в разделе «Компоновка».", style = MaterialTheme.typography.bodySmall)
    } else {
        SettingSlider("Размер · ${kind.label()}", config.textScale, .7f..1.3f, "${(config.textScale * 100).toInt()}%", steps = 5) { value -> onChange { it.copy(textScale = value) } }
        Choice("Цвет · ${kind.label()}", DockAccent.entries, config.accent, { it.label() }) { value -> onChange { it.copy(accent = value) } }
    }
}

@Composable
private fun Expansion(title: String, expanded: Boolean, onClick: () -> Unit) {
    TextButton(onClick, enabled = LocalEditingEnabled.current, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
        .semantics { stateDescription = if (expanded) "Развёрнуто" else "Свёрнуто" }) {
        Text(title, Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
        Text(if (expanded) "Свернуть" else "Настроить", style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun PanelEditor(title: String, config: PanelConfig, onChange: (PanelConfig) -> Unit) {
    Text(title, style = MaterialTheme.typography.titleMedium)
    Choice("Виджет · $title", WidgetKind.entries, config.widget, { it.label() }) { onChange(config.copy(widget = it)) }
    Choice("Цвет · $title", DockAccent.entries, config.accent, { it.label() }) { onChange(config.copy(accent = it)) }
    SettingSlider("Размер текста · $title", config.textScale, .7f..1.3f, "${(config.textScale * 100).toInt()}%", steps = 5) { onChange(config.copy(textScale = it)) }
    SettingSwitch("Подложка · $title", config.surface) { onChange(config.copy(surface = it)) }
    if (config.surface) SettingSwitch("Скругления · $title", config.rounded) { onChange(config.copy(rounded = it)) }
}

@Composable
private fun <T> Choice(title: String, values: List<T>, selected: T, label: (T) -> String, onChange: (T) -> Unit) {
    Column {
        Text(title, style = MaterialTheme.typography.labelLarge)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            values.forEach { value -> FilterChip(selected = value == selected, onClick = { onChange(value) }, enabled = LocalEditingEnabled.current,
                label = { Text(label(value)) }, modifier = Modifier.heightIn(min = 48.dp).testTag("$title:${label(value)}")) }
        }
    }
}

@Composable
private fun SettingSwitch(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().heightIn(min = 48.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, Modifier.weight(1f).padding(end = 12.dp))
        Switch(checked, onChange, enabled = LocalEditingEnabled.current,
            modifier = Modifier.testTag(label).semantics { contentDescription = label })
    }
}

@Composable
private fun SettingSlider(label: String, value: Float, range: ClosedFloatingPointRange<Float>, display: String, steps: Int = 0, onChange: (Float) -> Unit) {
    Column {
        Row { Text(label, Modifier.weight(1f).padding(end = 8.dp)); Text(display) }
        Slider(value, onChange, valueRange = range, steps = steps, enabled = LocalEditingEnabled.current,
            modifier = Modifier.heightIn(min = 48.dp).testTag(label).semantics { contentDescription = label; stateDescription = display })
    }
}
