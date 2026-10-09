package dev.androiddock.prototype

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import dev.androiddock.core.model.*

internal fun DockMode.label() = if (this == DockMode.BEDROOM) "Спальня" else "Рабочий стол"
internal fun WidgetKind.label() = when (this) { WidgetKind.CLOCK -> "Часы"; WidgetKind.CALENDAR -> "Календарь"; WidgetKind.BATTERY -> "Батарея" }
internal fun ClockStyle.label() = when (this) { ClockStyle.DIGITAL -> "Цифровые"; ClockStyle.ANALOG -> "Аналоговые"; ClockStyle.FLIP -> "Перекидные" }
internal fun DockBackground.label() = when (this) { DockBackground.BLACK -> "Чёрный"; DockBackground.GRAPHITE -> "Графит"; DockBackground.BLUE -> "Тёмно-синий"; DockBackground.PHOTO -> "Фото" }
internal fun DockAccent.label() = when (this) { DockAccent.WHITE -> "Светлый"; DockAccent.AMBER -> "Янтарный"; DockAccent.RED -> "Красный" }
private val LocalEditingEnabled = staticCompositionLocalOf { true }

@Composable
fun DockEditor(
    draft: DockEditorState, clock: ClockState, battery: Availability<BatteryState>,
    busy: Boolean, error: String?, onChange: ((DockSettings) -> DockSettings) -> Unit,
    onOrientation: (DockOrientation) -> Unit, onUndo: () -> Unit,
    onCancel: () -> Unit, onSave: () -> Unit, onPhoto: () -> Unit,
) {
    val mode = draft.settings.mode
    val profile = draft.settings.profile()
    val layout = profile.layout(draft.orientation)
    fun profileChange(transform: (DockProfile) -> DockProfile) = onChange { it.withProfile(mode, transform(it.profile(mode))) }
    fun layoutChange(transform: (DockLayout) -> DockLayout) = profileChange { it.withLayout(draft.orientation, transform(it.layout(draft.orientation))) }

    Column(Modifier.fillMaxSize().safeDrawingPadding()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(onCancel, enabled = !busy) { Text("Отмена") }
            Text("Мой экран", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            TextButton(onSave, enabled = !busy, modifier = Modifier.testTag("save-editor")) { Text(if (busy) "Подождите…" else "Сохранить") }
        }
        if (error != null) Text(error, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
        BoxWithConstraints(Modifier.weight(1f)) {
            val landscape = maxWidth > maxHeight
            val preview: @Composable (Modifier) -> Unit = { modifier ->
                BoxWithConstraints(modifier.padding(8.dp)) {
                    val aspect = if (draft.orientation == DockOrientation.PORTRAIT) 9f / 16f else 16f / 9f
                    val frameWidth = minOf(maxWidth, maxHeight * aspect)
                    val baseDensity = LocalDensity.current
                    val scale = frameWidth.value / if (draft.orientation == DockOrientation.PORTRAIT) 400f else 711f
                    CompositionLocalProvider(LocalDensity provides Density(baseDensity.density * scale, baseDensity.fontScale)) {
                    DockScreen(clock, Modifier.align(Alignment.Center).aspectRatio(aspect).fillMaxSize(), draft.settings, battery,
                        orientationOverride = draft.orientation, preview = true)
                    }
                }
            }
            val controls: @Composable (Modifier) -> Unit = { modifier ->
                CompositionLocalProvider(LocalEditingEnabled provides !busy) {
                Column(modifier.verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Choice("Режим", DockMode.entries, mode, { it.label() }) { selected -> onChange { it.copy(mode = selected) } }
                    Choice("Редактируемая раскладка", DockOrientation.entries, draft.orientation, { if (it == DockOrientation.PORTRAIT) "Портрет" else "Ландшафт" }, onOrientation)
                    Text("Состав и пропорции", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 12.dp))
                    Choice("Компоновка", LayoutKind.entries, layout.kind, { it.name }) { selected -> layoutChange { it.copy(kind = selected) } }
                    PanelEditor("Панель 1", layout.first) { panel -> layoutChange { it.copy(first = panel) } }
                    if (layout.kind == LayoutKind.DUO) {
                        PanelEditor("Панель 2", layout.second) { panel -> layoutChange { it.copy(second = panel) } }
                        TextButton(onClick = { layoutChange { it.copy(first = it.second, second = it.first) } }, enabled = !busy) { Text("Поменять панели местами") }
                        SettingSlider("Доля первой панели", layout.firstWeight, .3f.. .7f, "${(layout.firstWeight * 100).toInt()}%", steps = 7) { value -> layoutChange { it.copy(firstWeight = value) } }
                    }
                    SettingSlider("Отступы", layout.spacing.toFloat(), 0f..32f, "${layout.spacing} dp", steps = 7) { value -> layoutChange { it.copy(spacing = value.toInt()) } }
                    Text("Часы и календарь", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 12.dp))
                    Choice("Циферблат", ClockStyle.entries, profile.clockStyle, { it.label() }) { selected -> profileChange { it.copy(clockStyle = selected) } }
                    SettingSwitch("24 часа", profile.use24Hour) { value -> profileChange { it.copy(use24Hour = value) } }
                    SettingSwitch("Секунды", profile.showSeconds) { value -> profileChange { it.copy(showSeconds = value) } }
                    SettingSwitch("Дата в часах", profile.showDate) { value -> profileChange { it.copy(showDate = value) } }
                    SettingSwitch("Батарея в часах", profile.showBattery) { value -> profileChange { it.copy(showBattery = value) } }
                    Choice("Первый день недели", listOf(1, 7), profile.firstDayOfWeek, { if (it == 1) "Понедельник" else "Воскресенье" }) { selected -> profileChange { it.copy(firstDayOfWeek = selected) } }
                    Text("Фон и комфорт", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 12.dp))
                    Choice("Фон", DockBackground.entries, profile.background, { it.label() }) { selected ->
                        if (selected == DockBackground.PHOTO && profile.photoPath == null) onPhoto()
                        else profileChange { it.copy(background = selected) }
                    }
                    TextButton(onPhoto, enabled = !busy) { Text(if (profile.photoPath == null) "Выбрать фотографию" else "Заменить фотографию") }
                    Text("Сохраняется только выбранное фото; оно остаётся внутри приложения.", style = MaterialTheme.typography.bodySmall)
                    SettingSwitch("Ночная яркость", profile.nightEnabled) { value -> profileChange { it.copy(nightEnabled = value) } }
                    if (profile.nightEnabled) {
                        SettingSlider("Яркость ночью", profile.nightBrightness, .01f.. .3f, "${(profile.nightBrightness * 100).toInt()}%") { value -> profileChange { it.copy(nightBrightness = value) } }
                    } else {
                        SettingSwitch("Системная яркость", profile.brightness == -1f) { value -> profileChange { it.copy(brightness = if (value) -1f else .5f) } }
                        if (profile.brightness != -1f) SettingSlider("Яркость экрана", profile.brightness, .01f..1f, "${(profile.brightness * 100).toInt()}%") { value -> profileChange { it.copy(brightness = value) } }
                    }
                    SettingSwitch("Анимация часов", profile.animations) { value -> profileChange { it.copy(animations = value) } }
                    SettingSwitch("Сдвиг пикселей", profile.pixelShift) { value -> profileChange { it.copy(pixelShift = value) } }
                    Text("Медленный сдвиг содержимого снижает риск выгорания, но не исключает его.", style = MaterialTheme.typography.bodySmall)
                    TextButton(onUndo, enabled = draft.canUndo && !busy) { Text("Отменить последнее изменение") }
                    Text("Настройки режима общие для обеих ориентаций; состав, пропорции и оформление панелей сохраняются отдельно.", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom = 24.dp))
                }
                }
            }
            if (landscape) Row(Modifier.fillMaxSize()) {
                preview(Modifier.weight(.42f).fillMaxHeight())
                controls(Modifier.weight(.58f).fillMaxHeight())
            } else Column(Modifier.fillMaxSize()) {
                preview(Modifier.fillMaxWidth().height(220.dp))
                controls(Modifier.weight(1f).fillMaxWidth())
            }
        }
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
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            values.forEach { value -> FilterChip(selected = value == selected, onClick = { onChange(value) }, enabled = LocalEditingEnabled.current, label = { Text(label(value)) }) }
        }
    }
}

@Composable
private fun SettingSwitch(label: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, Modifier.weight(1f).padding(end = 12.dp))
        Switch(checked, onChange, enabled = LocalEditingEnabled.current, modifier = Modifier.testTag(label))
    }
}

@Composable
private fun SettingSlider(label: String, value: Float, range: ClosedFloatingPointRange<Float>, display: String, steps: Int = 0, onChange: (Float) -> Unit) {
    Column {
        Row { Text(label, Modifier.weight(1f)); Text(display) }
        Slider(value, onChange, valueRange = range, steps = steps, enabled = LocalEditingEnabled.current, modifier = Modifier.testTag(label))
    }
}
