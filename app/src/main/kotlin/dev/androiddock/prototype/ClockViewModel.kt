package dev.androiddock.prototype

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.androiddock.core.model.ClockStateHolder
import dev.androiddock.core.model.DockEditorStateHolder
import dev.androiddock.core.model.DockBackground
import dev.androiddock.core.model.DockAppearance
import dev.androiddock.core.model.DockMode
import dev.androiddock.core.model.DockProfile
import java.io.File
import dev.androiddock.core.model.withAppearance
import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.CancellationException

class ClockViewModel : ViewModel() {
    private val holder = ClockStateHolder()
    val state = holder.state
    val editor = DockEditorStateHolder()
    private val mutableBusy = MutableStateFlow(false)
    val busy = mutableBusy.asStateFlow()
    private val mutableError = MutableStateFlow<String?>(null)
    val error = mutableError.asStateFlow()

    private val mutableLockRequest = MutableStateFlow<LockWallpaperRequest?>(null)
    val lockRequest = mutableLockRequest.asStateFlow()
    private val mutableNotice = MutableStateFlow<String?>(null)
    val notice = mutableNotice.asStateFlow()
    private data class PhotoPick(val mode: DockMode, val gallery: Boolean)
    private var photoPick: PhotoPick? = null

    fun clearNotice() { mutableNotice.value = null }

    fun beginPhotoPick(gallery: Boolean = false): Boolean {
        val draft = editor.state.value ?: return false
        if (mutableBusy.value) return false
        photoPick = PhotoPick(draft.settings.mode, gallery)
        return true
    }

    fun cancelPhotoPick() { photoPick = null }

    fun requestLockWallpaper(container: AppContainer, width: Int, height: Int) {
        val draft = editor.state.value ?: return
        if (mutableBusy.value) return
        val profile = draft.settings.profile()
        requestLockWallpaper(profile, container.photos.resolve(profile.photoPath), width, height)
    }

    internal fun requestLockWallpaper(profile: DockProfile, photo: File?, width: Int, height: Int) {
        if (mutableBusy.value) return
        if (photo == null || !photo.isFile) {
            mutableError.value = "Сначала выберите фотографию для фона."
            return
        }
        mutableError.value = null
        mutableBusy.value = true
        mutableLockRequest.value = LockWallpaperRequest(photo, profile.portraitPhoto, minOf(width, height), maxOf(width, height))
    }

    fun cancelLockWallpaper() {
        if (mutableLockRequest.value == null) return
        mutableLockRequest.value = null
        mutableBusy.value = false
    }

    fun confirmLockWallpaper(adapter: LockWallpaperAdapter) {
        val request = mutableLockRequest.value ?: return
        mutableLockRequest.value = null
        viewModelScope.launch {
            try {
                LockWallpaperOperation(adapter).apply(request)
                mutableNotice.value = "Фото установлено на экран блокировки."
            } catch (error: CancellationException) { throw error }
            catch (error: Exception) {
                mutableError.value = error.message ?: "Не удалось установить обои экрана блокировки."
            } finally { mutableBusy.value = false }
        }
    }

    fun clearError() { mutableError.value = null }

    fun saveEditor(container: AppContainer) {
        val draft = editor.state.value ?: return
        if (mutableBusy.value) return
        cancelPhotoPick()
        viewModelScope.launch {
            mutableBusy.value = true
            mutableError.value = null
            try {
                container.repository.update { draft.settings }
                val unused = container.photos.unusedFiles(setOfNotNull(draft.settings.bedroom.photoPath, draft.settings.desktop.photoPath))
                editor.cancel()
                container.photos.removeFiles(unused)
            } catch (error: CancellationException) { throw error }
            catch (_: Exception) { mutableError.value = "Не удалось сохранить. Попробуйте ещё раз; изменения остаются в редакторе." }
            finally { mutableBusy.value = false }
        }
    }

    fun importPhoto(container: AppContainer, uri: Uri) {
        val pick = photoPick ?: return
        if (mutableBusy.value || editor.state.value == null) { photoPick = null; return }
        photoPick = null
        val mode = pick.mode
        viewModelScope.launch {
            mutableBusy.value = true
            mutableError.value = null
            try {
                val name = container.photos.import(uri)
                editor.change {
                    val profile = it.profile(mode).copy(background = DockBackground.PHOTO, photoPath = name)
                    it.withProfile(mode, if (pick.gallery) profile.withAppearance(DockAppearance.GALLERY) else profile)
                }
            } catch (error: CancellationException) { throw error }
            catch (error: Exception) { mutableError.value = error.message ?: "Не удалось открыть фотографию. Выберите другое фото." }
            finally { mutableBusy.value = false }
        }
    }

    fun start() = holder.start()
    fun stop() = holder.stop()

    override fun onCleared() {
        holder.close()
    }
}
