package dev.androiddock.prototype

import android.view.Window
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect

/** Overrides only this app window; never writes the user's system brightness setting. */
@Composable
internal fun WindowBrightness(window: Window?, brightness: Float) {
    DisposableEffect(window) {
        val original = window?.attributes?.screenBrightness ?: -1f
        onDispose { window?.let { it.attributes = it.attributes.apply { screenBrightness = original } } }
    }
    SideEffect { window?.let { it.attributes = it.attributes.apply { screenBrightness = brightness } } }
}
