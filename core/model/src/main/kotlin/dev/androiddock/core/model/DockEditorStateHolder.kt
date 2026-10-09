package dev.androiddock.core.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** A reversible draft. Persisting is a separate repository action after Save. */
data class DockEditorState(val settings: DockSettings, val orientation: DockOrientation, val canUndo: Boolean = false)

class DockEditorStateHolder {
    private val mutableState = MutableStateFlow<DockEditorState?>(null)
    val state = mutableState.asStateFlow()
    private val history = ArrayDeque<DockSettings>()

    fun open(settings: DockSettings, orientation: DockOrientation) {
        history.clear()
        mutableState.value = DockEditorState(settings, orientation)
    }

    fun selectOrientation(orientation: DockOrientation) {
        mutableState.value = mutableState.value?.copy(orientation = orientation)
    }

    fun change(transform: (DockSettings) -> DockSettings) {
        val current = mutableState.value ?: return
        val next = transform(current.settings).validated()
        if (next == current.settings) return
        history.addLast(current.settings)
        if (history.size > 50) history.removeFirst()
        mutableState.value = current.copy(settings = next, canUndo = true)
    }

    fun undo() {
        val current = mutableState.value ?: return
        val previous = history.removeLastOrNull() ?: return
        mutableState.value = current.copy(settings = previous, canUndo = history.isNotEmpty())
    }

    fun cancel() {
        history.clear()
        mutableState.value = null
    }
}
