package dev.androiddock.core.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable enum class DockMode { BEDROOM, DESKTOP }
@Serializable enum class DockOrientation { PORTRAIT, LANDSCAPE }
@Serializable enum class LayoutKind { DUO, SOLO }
@Serializable enum class WidgetKind { CLOCK, CALENDAR, BATTERY }
@Serializable enum class ClockStyle { DIGITAL, ANALOG, FLIP }
@Serializable enum class DockBackground { BLACK, GRAPHITE, BLUE, PHOTO }
@Serializable enum class DockAccent { WHITE, AMBER, RED }

@Serializable data class PanelConfig(
    val widget: WidgetKind = WidgetKind.CLOCK,
    val accent: DockAccent = DockAccent.WHITE,
    val textScale: Float = 1f,
    val surface: Boolean = false,
    val rounded: Boolean = true,
)

@Serializable data class DockLayout(
    val kind: LayoutKind = LayoutKind.DUO,
    val first: PanelConfig = PanelConfig(),
    val second: PanelConfig = PanelConfig(widget = WidgetKind.CALENDAR),
    val firstWeight: Float = .5f,
    val spacing: Int = 16,
)

@Serializable data class DockProfile(
    val portrait: DockLayout = DockLayout(),
    val landscape: DockLayout = DockLayout(),
    val clockStyle: ClockStyle = ClockStyle.DIGITAL,
    val use24Hour: Boolean = true,
    val showSeconds: Boolean = true,
    val showDate: Boolean = true,
    val showBattery: Boolean = true,
    // ISO weekday number: Monday = 1, Sunday = 7.
    val firstDayOfWeek: Int = 1,
    val background: DockBackground = DockBackground.BLACK,
    val photoPath: String? = null,
    val brightness: Float = -1f,
    val nightBrightness: Float = .08f,
    val nightEnabled: Boolean = false,
    val animations: Boolean = true,
    val pixelShift: Boolean = true,
) {
    fun layout(orientation: DockOrientation) = if (orientation == DockOrientation.PORTRAIT) portrait else landscape
    fun withLayout(orientation: DockOrientation, layout: DockLayout) =
        if (orientation == DockOrientation.PORTRAIT) copy(portrait = layout) else copy(landscape = layout)
    fun windowBrightness(): Float = if (nightEnabled) nightBrightness else brightness
}

@Serializable data class DockSettings(
    val schemaVersion: Int = 1,
    val mode: DockMode = DockMode.DESKTOP,
    val bedroom: DockProfile = DockProfile(
        showSeconds = false, animations = false, nightEnabled = true,
        portrait = DockLayout(first = PanelConfig(accent = DockAccent.AMBER), second = PanelConfig(WidgetKind.CALENDAR, DockAccent.AMBER)),
        landscape = DockLayout(first = PanelConfig(accent = DockAccent.AMBER), second = PanelConfig(WidgetKind.CALENDAR, DockAccent.AMBER)),
    ),
    val desktop: DockProfile = DockProfile(),
) {
    fun profile(mode: DockMode = this.mode) = if (mode == DockMode.BEDROOM) bedroom else desktop
    fun withProfile(mode: DockMode, profile: DockProfile) =
        if (mode == DockMode.BEDROOM) copy(bedroom = profile) else copy(desktop = profile)

    fun validated(): DockSettings {
        require(schemaVersion == 1) { "Unsupported settings version: $schemaVersion" }
        listOf(bedroom, desktop).forEach { p ->
            require(p.firstDayOfWeek in 1..7)
            require(p.brightness == -1f || p.brightness in .01f..1f)
            require(p.nightBrightness in .01f..1f)
            listOf(p.portrait, p.landscape).forEach { l ->
                require(l.firstWeight in .3f.. .7f && l.spacing in 0..32)
                listOf(l.first, l.second).forEach { require(it.textScale in .7f..1.3f) }
            }
        }
        return this
    }
}

/** Local settings only; this is deliberately not a Dock Pack format. */
object SettingsCodec {
    private val json = Json { encodeDefaults = true; ignoreUnknownKeys = true }
    fun encode(settings: DockSettings): String = json.encodeToString(settings.validated())
    fun decode(value: String): DockSettings = json.decodeFromString<DockSettings>(value).validated()
}
