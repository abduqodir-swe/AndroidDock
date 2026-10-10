package dev.androiddock.core.model

/** Applies visual choices only; personal photo, orientation crops and comfort settings survive. */
fun DockProfile.withAppearance(appearance: DockAppearance): DockProfile {
    if (appearance == DockAppearance.LEGACY) return copy(appearance = appearance)
    val layout = DockLayout(
        first = PanelConfig(WidgetKind.CLOCK, DockAccent.WHITE),
        second = PanelConfig(WidgetKind.CALENDAR, DockAccent.WHITE),
        spacing = 16,
    )
    return copy(
        appearance = appearance,
        portrait = layout,
        landscape = layout,
        clockStyle = when (appearance) {
            DockAppearance.STUDIO -> ClockStyle.ANALOG
            DockAppearance.FLIP -> ClockStyle.FLIP
            else -> ClockStyle.DIGITAL
        },
        background = when (appearance) {
            DockAppearance.MIDNIGHT -> DockBackground.BLACK
            DockAppearance.GALLERY -> DockBackground.PHOTO
            else -> DockBackground.GRAPHITE
        },
        showSeconds = false,
        calendarHighlightToday = true,
        calendarWeekendAccent = null,
        photoDim = if (appearance == DockAppearance.GALLERY) .32f else .72f,
    )
}

/** Coordinates in source pixels; x/y align the available crop range after fill and zoom. */
data class PhotoCrop(val left: Int, val top: Int, val width: Int, val height: Int)

fun photoCrop(sourceWidth: Int, sourceHeight: Int, targetWidth: Int, targetHeight: Int, framing: PhotoFraming): PhotoCrop {
    require(sourceWidth > 0 && sourceHeight > 0 && targetWidth > 0 && targetHeight > 0)
    require(framing.x.isFinite() && framing.x in 0f..1f && framing.y.isFinite() && framing.y in 0f..1f)
    require(framing.zoom.isFinite() && framing.zoom in 1f..3f)
    val scale = maxOf(targetWidth.toDouble() / sourceWidth, targetHeight.toDouble() / sourceHeight) * framing.zoom
    val width = (targetWidth / scale).toInt().coerceIn(1, sourceWidth)
    val height = (targetHeight / scale).toInt().coerceIn(1, sourceHeight)
    return PhotoCrop(((sourceWidth - width) * framing.x).toInt(), ((sourceHeight - height) * framing.y).toInt(), width, height)
}
