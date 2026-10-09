package dev.androiddock.prototype

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.androiddock.core.model.ClockStateHolder
import dev.androiddock.core.model.DockEditorStateHolder
import dev.androiddock.core.model.DockBackground
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

    fun clearError() { mutableError.value = null }

    fun saveEditor(container: AppContainer) {
        val draft = editor.state.value ?: return
        if (mutableBusy.value) return
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
        if (mutableBusy.value || editor.state.value == null) return
        val mode = editor.state.value!!.settings.mode
        viewModelScope.launch {
            mutableBusy.value = true
            mutableError.value = null
            try {
                val name = container.photos.import(uri)
                editor.change { it.withProfile(mode, it.profile(mode).copy(background = DockBackground.PHOTO, photoPath = name)) }
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
