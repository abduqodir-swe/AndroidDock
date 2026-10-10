package dev.androiddock.prototype

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import dev.androiddock.core.model.photoCrop
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

class AndroidLockWallpaper(context: Context) : LockWallpaperAdapter {
    private val manager = WallpaperManager.getInstance(context.applicationContext)
    override fun isSupported() = manager.isWallpaperSupported
    override fun isAllowed() = manager.isSetWallpaperAllowed

    override suspend fun setLockPhoto(request: LockWallpaperRequest): Int {
        val bitmap = BitmapFactory.decodeFile(request.photo.path)
            ?: error("Не удалось прочитать фотографию. Выберите её ещё раз.")
        try {
            val crop = photoCrop(bitmap.width, bitmap.height, request.width, request.height, request.framing)
            val cropped = Bitmap.createBitmap(bitmap, crop.left, crop.top, crop.width, crop.height)
            try {
                currentCoroutineContext().ensureActive()
                // This overload targets only the lock screen. Never retry with FLAG_SYSTEM or without flags.
                return manager.setBitmap(cropped, null, false, WallpaperManager.FLAG_LOCK)
            } finally { if (cropped !== bitmap) cropped.recycle() }
        } finally { bitmap.recycle() }
    }
}
