package dev.androiddock.prototype

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import java.io.File
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Copies only the explicitly selected image; no gallery permission or persistent URI grant. */
class PhotoStore(private val context: Context) {
    val directory = File(context.filesDir, "backgrounds")

    suspend fun import(uri: Uri): String = withContext(Dispatchers.IO) {
        directory.mkdirs()
        val source = File(directory, "${UUID.randomUUID()}.source")
        val result = File(directory, "${UUID.randomUUID()}.jpg")
        try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                source.outputStream().use { output ->
                    val buffer = ByteArray(32 * 1024)
                    var total = 0L
                    while (true) {
                        val count = input.read(buffer)
                        if (count == -1) break
                        total += count
                        require(total <= 32 * 1024 * 1024) { "Фотография больше 32 МБ. Выберите меньший файл." }
                        output.write(buffer, 0, count)
                    }
                }
            } ?: error("Фотография недоступна. Выберите её ещё раз.")
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(source.path, bounds)
            require(bounds.outWidth > 0 && bounds.outHeight > 0) { "Не удалось открыть изображение. Выберите другое фото." }
            val options = BitmapFactory.Options().apply {
                var sample = 1
                while (maxOf(bounds.outWidth, bounds.outHeight) / sample > 2048) sample *= 2
                inSampleSize = sample
            }
            val bitmap = BitmapFactory.decodeFile(source.path, options) ?: error("Не удалось прочитать фотографию.")
            try {
                val matrix = Matrix()
                when (ExifInterface(source.path).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                    ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
                    ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.setScale(1f, -1f)
                    ExifInterface.ORIENTATION_TRANSPOSE -> { matrix.setRotate(90f); matrix.postScale(-1f, 1f) }
                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
                    ExifInterface.ORIENTATION_TRANSVERSE -> { matrix.setRotate(270f); matrix.postScale(-1f, 1f) }
                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(270f)
                }
                val oriented = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                try { result.outputStream().use { require(oriented.compress(Bitmap.CompressFormat.JPEG, 90, it)) } }
                finally { if (oriented !== bitmap) oriented.recycle() }
            } finally { bitmap.recycle() }
            result.name
        } catch (error: Exception) {
            result.delete()
            throw error
        } finally { source.delete() }
    }

    fun resolve(name: String?): File? = name?.takeIf { File(it).name == it && it.endsWith(".jpg") }?.let { name ->
        // Reject traversal and symlinks outside the app's private imported-photo directory.
        runCatching { File(directory, name).takeIf { it.canonicalFile.parentFile == directory.canonicalFile } }.getOrNull()
    }

    // Snapshot before closing the editor so cleanup cannot delete a later draft's new photo.
    fun unusedFiles(keep: Set<String>): List<String> =
        directory.listFiles()?.filter { it.name.endsWith(".jpg") && it.name !in keep }?.map { it.name }.orEmpty()

    suspend fun removeFiles(names: List<String>) = withContext(Dispatchers.IO) {
        names.forEach { resolve(it)?.delete() }
    }
}
