package dev.androiddock.core.model

import org.junit.Assert.*
import org.junit.Test

class DockEditorTest {
    @Test fun draftChangesAreIndependentAndUndoRestoresPriorSettings() {
        val holder = DockEditorStateHolder()
        val original = DockSettings()
        holder.open(original, DockOrientation.PORTRAIT)
        holder.change { it.copy(desktop = it.desktop.withLayout(DockOrientation.PORTRAIT, DockLayout(kind = LayoutKind.SOLO))) }
        holder.selectOrientation(DockOrientation.LANDSCAPE)
        assertEquals(LayoutKind.DUO, holder.state.value!!.settings.desktop.landscape.kind)
        assertEquals(LayoutKind.SOLO, holder.state.value!!.settings.desktop.portrait.kind)
        holder.undo()
        assertEquals(original, holder.state.value!!.settings)
        assertEquals(DockOrientation.LANDSCAPE, holder.state.value!!.orientation)
        holder.cancel()
        assertNull(holder.state.value)
        assertEquals(LayoutKind.DUO, original.desktop.portrait.kind)
    }
}
