package dev.androiddock.prototype

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.androiddock.core.model.ClockState
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val clockFormat = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.ROOT)

@Composable
fun DockScreen(state: ClockState, modifier: Modifier = Modifier) {
    val time = Instant.ofEpochMilli(state.epochMillis)
        .atZone(ZoneId.systemDefault())
        .format(clockFormat)

    Box(
        modifier = modifier.fillMaxSize().background(Color.Black)
            .safeDrawingPadding().padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = time,
            style = TextStyle(
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
            ),
            maxLines = 1,
            autoSize = TextAutoSize.StepBased(minFontSize = 32.sp, maxFontSize = 128.sp),
        )
    }
}
