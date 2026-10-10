package dev.androiddock.prototype

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.androiddock.core.model.DockAppearance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import dev.androiddock.core.model.ClockStyle
import dev.androiddock.core.model.DockProfile
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

@Composable
internal fun ClockFace(time: ZonedDateTime, profile: DockProfile, color: Color, scale: Float, modifier: Modifier = Modifier) {
    val format = (if (profile.use24Hour) "HH:mm" else "hh:mm") + (if (profile.showSeconds) ":ss" else "")
    val formatted = time.format(DateTimeFormatter.ofPattern(format, Locale.getDefault()))
    when (profile.clockStyle) {
        ClockStyle.DIGITAL -> BoxWithConstraints(modifier, contentAlignment = Alignment.Center) {
            val maximum = (maxWidth.value / (formatted.length * .62f) * .95f * scale).coerceIn(18f, if (profile.appearance == DockAppearance.LEGACY) 144f else 320f)
            BasicText(formatted, Modifier.fillMaxWidth().testTag("clock"),
                style = TextStyle(color = color, fontFamily = when (profile.appearance) { DockAppearance.GALLERY -> FontFamily.Serif; DockAppearance.LEGACY -> FontFamily.Monospace; else -> FontFamily.SansSerif }, fontWeight = if (profile.appearance == DockAppearance.MIDNIGHT) FontWeight.Bold else FontWeight.Normal, textAlign = TextAlign.Center),
                maxLines = 1, autoSize = TextAutoSize.StepBased(18.sp, maximum.sp))
        }
        ClockStyle.ANALOG -> Column(modifier.testTag("clock").semantics(mergeDescendants = true) { contentDescription = "Аналоговые часы, $formatted" }, horizontalAlignment = Alignment.CenterHorizontally) {
            AnalogClock(time, profile.showSeconds, color, Modifier.weight(1f).fillMaxWidth(), profile.appearance == DockAppearance.STUDIO, scale)
            if (profile.appearance == DockAppearance.STUDIO) BasicText(formatted, Modifier.fillMaxWidth(), style = TextStyle(color = color, textAlign = TextAlign.Center), maxLines = 1, autoSize = TextAutoSize.StepBased(14.sp, (44 * scale).sp))
        }
        ClockStyle.FLIP -> FlipClock(formatted, color, scale, profile.animations && android.animation.ValueAnimator.areAnimatorsEnabled(), modifier.testTag("clock").clearAndSetSemantics { contentDescription = "Перекидные часы, $formatted" }, profile.appearance == DockAppearance.FLIP)
    }
}

@Composable
private fun AnalogClock(time: ZonedDateTime, seconds: Boolean, color: Color, modifier: Modifier, numbered: Boolean, scale: Float) {
    BoxWithConstraints(modifier, contentAlignment = Alignment.Center) {
        val available = minOf(maxWidth, maxHeight)
        // Studio reserves enlargement headroom; legacy keeps its existing size at scale 1.
        val side = minOf(available, available * if (numbered) scale / 1.3f else scale)
        Box(Modifier.size(side), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize().padding(12.dp)) {
            val radius = size.minDimension / 2
            val center = center
            fun point(angle: Double, length: Float): Offset {
                val radians = Math.toRadians(angle - 90)
                return center + Offset((cos(radians) * length).toFloat(), (sin(radians) * length).toFloat())
            }
            for (tick in 0..59) {
                val hour = tick % 5 == 0
                drawLine(color.copy(alpha = if (hour) .9f else .35f), point(tick * 6.0, radius * if (hour) .84f else .91f), point(tick * 6.0, radius), strokeWidth = if (hour) 3.dp.toPx() else 1.dp.toPx(), cap = StrokeCap.Round)
            }
            drawLine(color, center, point((time.hour % 12 + time.minute / 60.0) * 30, radius * .53f), 6.dp.toPx(), StrokeCap.Round)
            drawLine(color, center, point((time.minute + time.second / 60.0) * 6, radius * .77f), 4.dp.toPx(), StrokeCap.Round)
            if (seconds) drawLine(color.copy(alpha = .65f), center, point(time.second * 6.0, radius * .86f), 1.5.dp.toPx(), StrokeCap.Round)
            drawCircle(if (numbered && color == Color(0xFFF5F3ED)) Color(0xFFD9C29B) else color, radius = 5.dp.toPx(), center = center)
        }
        if (numbered) listOf("12" to Alignment.TopCenter, "3" to Alignment.CenterEnd, "6" to Alignment.BottomCenter, "9" to Alignment.CenterStart).forEach { (label, alignment) ->
            BasicText(label, Modifier.align(alignment).padding(side * .14f), style = TextStyle(color = color, fontSize = (side.value * .075f).coerceIn(12f, 28f).sp))
        }
        }
    }
}

@Composable
private fun FlipClock(value: String, color: Color, scale: Float, animate: Boolean, modifier: Modifier, warm: Boolean) {
    BoxWithConstraints(modifier, contentAlignment = Alignment.Center) {
        val parts = value.split(":")
        val width = ((maxWidth.value - (parts.size - 1) * 8) / parts.size).dp
        val height = minOf(maxHeight, width * 1.1f)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            parts.forEach { FlipCard(it, color, scale, animate, Modifier.width(width).height(height), warm) }
        }
    }
}

@Composable
private fun FlipCard(value: String, color: Color, scale: Float, animate: Boolean, modifier: Modifier, warm: Boolean) {
    val progress = remember { Animatable(1f) }
    var previous by remember { mutableStateOf(value) }
    var shown by remember { mutableStateOf(value) }
    LaunchedEffect(value, animate) {
        previous = shown
        shown = value
        if (animate && previous != value) {
            progress.snapTo(0f)
            progress.animateTo(1f, tween(350))
        } else progress.snapTo(1f)
    }
    val fraction = progress.value
    BoxWithConstraints(modifier.clip(RoundedCornerShape(12.dp)).background(if (warm) Color(0xFF242422) else Color(0xFF1B1D20)).then(if (warm) Modifier.border(1.dp, Color(0xFF494943), RoundedCornerShape(12.dp)) else Modifier), contentAlignment = Alignment.Center) {
        val maximum = (maxWidth.value / 1.24f * .9f * scale).coerceIn(14f, if (warm) 280f else 144f)
        Box(Modifier.fillMaxSize().graphicsLayer {
            rotationX = if (fraction < .5f) -fraction * 180 else (1 - fraction) * 180
            cameraDistance = 12 * density
        }, contentAlignment = Alignment.Center) {
            BasicText(if (fraction < .5f) previous else value, Modifier.fillMaxWidth().padding(4.dp),
                style = TextStyle(color = color, fontFamily = if (warm) FontFamily.SansSerif else FontFamily.Monospace, fontWeight = if (warm) FontWeight.Bold else FontWeight.Normal, textAlign = TextAlign.Center),
                maxLines = 1, autoSize = TextAutoSize.StepBased(14.sp, maximum.sp))
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(Color.Black.copy(alpha = .65f)))
    }
}
