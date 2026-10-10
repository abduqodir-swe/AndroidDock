package dev.androiddock.prototype

import dev.androiddock.core.model.PhotoFraming
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Immutable editor snapshot: wallpaper always uses the portrait crop of the selected private photo. */
data class LockWallpaperRequest(val photo: File, val framing: PhotoFraming, val width: Int, val height: Int)

/** No home-screen or system-crop entry point is exposed. The implementation sets FLAG_LOCK exclusively. */
interface LockWallpaperAdapter {
    fun isSupported(): Boolean
    fun isAllowed(): Boolean
    suspend fun setLockPhoto(request: LockWallpaperRequest): Int
}

class LockWallpaperOperation(private val adapter: LockWallpaperAdapter) {
    suspend fun apply(request: LockWallpaperRequest) = withContext(Dispatchers.IO) {
        require(adapter.isSupported()) { "Устройство не поддерживает установку обоев." }
        require(adapter.isAllowed()) { "Установка обоев запрещена настройками устройства." }
        require(request.photo.isFile) { "Фотография недоступна. Выберите её ещё раз." }
        require(adapter.setLockPhoto(request) > 0) { "Android не подтвердил установку обоев. Попробуйте ещё раз." }
    }
}
