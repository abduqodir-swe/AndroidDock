package dev.androiddock.prototype

import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import dev.androiddock.core.model.DockOrientation
import dev.androiddock.core.model.DockProfile
import dev.androiddock.core.model.photoCrop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Shared preview/display crop. Position is normalized across the available crop range. */
@Composable
internal fun DockPhotoPreview(profile: DockProfile, orientation: DockOrientation, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val framing = profile.photoFraming(orientation)
    Box(modifier.clipToBounds().background(Color.Black)) {
        // Recreate state on path changes: never show the previous photograph while decoding.
        key(profile.photoPath) {
            var loaded by remember { mutableStateOf(false) }
            val bitmap by produceState<ImageBitmap?>(null, profile.photoPath) {
                value = withContext(Dispatchers.IO) {
                    context.appContainer.photos.resolve(profile.photoPath)?.let { file ->
                        try {
                            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                            BitmapFactory.decodeFile(file.path, bounds)
                            var sample = 1
                            while (maxOf(bounds.outWidth, bounds.outHeight) / sample > 2048) sample *= 2
                            BitmapFactory.decodeFile(file.path, BitmapFactory.Options().apply { inSampleSize = sample })?.asImageBitmap()
                        } catch (_: Exception) { null }
                    }
                }
                loaded = true
            }
            bitmap?.let { photo ->
                Canvas(Modifier.fillMaxSize().testTag("photo-background")) {
                    if (size.width <= 0f || size.height <= 0f) return@Canvas
                    val target = IntSize(size.width.toInt().coerceAtLeast(1), size.height.toInt().coerceAtLeast(1))
                    val crop = photoCrop(photo.width, photo.height, target.width, target.height, framing)
                    drawImage(photo, srcOffset = IntOffset(crop.left, crop.top), srcSize = IntSize(crop.width, crop.height), dstSize = target)
                }
            }
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = profile.photoDim)))
            if (bitmap == null) Text(if (profile.photoPath == null) "Выберите фото в настройках" else if (!loaded) "Фото загружается…" else "Фото недоступно. Выберите другое в настройках", color = Color.White, modifier = Modifier.padding(24.dp).testTag("photo-placeholder"))
        }
    }
}
