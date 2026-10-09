package dev.androiddock.prototype

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.androiddock.core.model.*
import java.io.File
import java.nio.file.Files
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Test

class DockRepositoryTest {
    @Test fun committedSettingsSurviveNewStoreAndConcurrentEdits() = runBlocking {
        val folder = Files.createTempDirectory("dock-store").toFile()
        val file = File(folder, "test.preferences_pb")
        var scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        try {
            var repository = DockRepository(PreferenceDataStoreFactory.create(scope = scope) { file }, scope)
            assertEquals(DockSettings(), (repository.state.first { it is SettingsState.Ready } as SettingsState.Ready).settings)
            coroutineScope {
                launch { repository.update { it.copy(mode = DockMode.BEDROOM) } }
                launch { repository.update { it.copy(desktop = it.desktop.copy(firstDayOfWeek = 7)) } }
                launch { repository.update { it.copy(bedroom = it.bedroom.copy(clockStyle = ClockStyle.FLIP)) } }
            }
            scope.coroutineContext[Job]!!.cancelAndJoin()
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
            repository = DockRepository(PreferenceDataStoreFactory.create(scope = scope) { file }, scope)
            val restored = (withTimeout(5_000) { repository.state.first { it is SettingsState.Ready } } as SettingsState.Ready).settings
            assertEquals(DockMode.BEDROOM, restored.mode)
            assertEquals(7, restored.desktop.firstDayOfWeek)
            assertEquals(ClockStyle.FLIP, restored.bedroom.clockStyle)
        } finally {
            scope.coroutineContext[Job]!!.cancelAndJoin()
            folder.deleteRecursively()
        }
    }

    @Test fun invalidSavedSettingsShowErrorAndAreNotOverwritten() = runBlocking {
        val folder = Files.createTempDirectory("dock-invalid").toFile()
        val file = File(folder, "test.preferences_pb")
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        try {
            val store = PreferenceDataStoreFactory.create(scope = scope) { file }
            val key = stringPreferencesKey("dock_settings_v1")
            store.edit { it[key] = "{\"schemaVersion\":99}" }
            val repository = DockRepository(store, scope)
            assertEquals(SettingsState.Error, withTimeout(5_000) { repository.state.first { it != SettingsState.Loading } })
            try { repository.update { DockSettings() }; fail("Invalid config must not be erased") }
            catch (_: IllegalArgumentException) { }
            assertEquals("{\"schemaVersion\":99}", store.data.first()[key])
        } finally {
            scope.coroutineContext[Job]!!.cancelAndJoin()
            folder.deleteRecursively()
        }
    }
}
