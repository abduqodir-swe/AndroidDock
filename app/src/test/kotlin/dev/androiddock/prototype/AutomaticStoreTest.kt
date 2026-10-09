package dev.androiddock.prototype

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import dev.androiddock.core.model.ChargingSession
import dev.androiddock.core.model.ChargingPause
import dev.androiddock.core.model.ChargingStateHolder
import dev.androiddock.core.model.ChargingEnvironment
import dev.androiddock.core.model.ChargingEffect
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import java.io.File
import java.nio.file.Files
import kotlinx.coroutines.*
import org.junit.Assert.*
import org.junit.Test

class AutomaticStoreTest {
    @Test fun alwaysOnConsentIsIndependentPersistsAndDisableAllClearsBoth() = runBlocking {
        val folder = Files.createTempDirectory("always-on-store").toFile()
        val file = File(folder, "test.preferences_pb")
        var scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        try {
            var store = AutomaticStore(PreferenceDataStoreFactory.create(scope = scope) { file }, scope)
            assertEquals(AutomaticModes(), store.modes())
            store.setAlwaysOn(true)
            assertTrue(store.consent()); assertEquals(AutomaticModes(false, true), store.modes())
            scope.coroutineContext[Job]!!.cancelAndJoin()
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
            store = AutomaticStore(PreferenceDataStoreFactory.create(scope = scope) { file }, scope)
            assertEquals(AutomaticModes(false, true), store.modes())
            store.setEnabled(true); store.setAlwaysOn(false)
            assertEquals(AutomaticModes(true, false), store.modes()); assertTrue(store.consent())
            store.setAlwaysOn(true); store.disableAll()
            assertEquals(AutomaticModes(), store.modes()); assertFalse(store.consent())
        } finally { scope.coroutineContext[Job]!!.cancelAndJoin(); folder.deleteRecursively() }
    }
    @Test fun oldSuppressedSnapshotRearmsAfterUseOnSameCharge() = runBlocking {
        val folder = Files.createTempDirectory("automatic-migrate").toFile()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        try {
            val prefs = PreferenceDataStoreFactory.create(scope = scope) { File(folder, "test.preferences_pb") }
            val store = AutomaticStore(prefs, scope)
            store.setEnabled(true)
            prefs.edit { it[stringPreferencesKey("automatic_session_v1")] = """{"boot":42,"session":{"connected":true,"suppressed":true}}""" }
            val holder = ChargingStateHolder(store.restoreSession(42))
            holder.update(ChargingEnvironment(true, true, false, true), 0)
            holder.update(ChargingEnvironment(true, false, true, true), 100)
            assertEquals(listOf(ChargingEffect.LAUNCH), holder.tick(800))
            assertTrue(store.consent())
        } finally { scope.coroutineContext[Job]!!.cancelAndJoin(); folder.deleteRecursively() }
    }
    @Test fun explicitNewOptInClearsOldPauseAndKeepsDisplaySettings() = runBlocking {
        val folder = Files.createTempDirectory("automatic-opt-in").toFile()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        try {
            val prefs = PreferenceDataStoreFactory.create(scope = scope) { File(folder, "test.preferences_pb") }
            val store = AutomaticStore(prefs, scope)
            val repository = DockRepository(prefs, scope)
            repository.update { it.copy(mode = dev.androiddock.core.model.DockMode.BEDROOM) }
            val original = (repository.state.first { it is SettingsState.Ready } as SettingsState.Ready).settings
            store.setEnabled(true)
            store.saveSession(42, ChargingSession(true, true, ChargingPause.DISPLAY_OFF))
            store.setEnabled(false); store.setEnabled(true)
            assertEquals(ChargingSession(), store.restoreSession(42))
            assertTrue(store.consent())
            assertEquals(original, (repository.state.first { it is SettingsState.Ready } as SettingsState.Ready).settings)
        } finally { scope.coroutineContext[Job]!!.cancelAndJoin(); folder.deleteRecursively() }
    }
    @Test fun explicitConsentAndSuppressedCycleSurviveProcessButNotBoot() = runBlocking {
        val folder = Files.createTempDirectory("automatic-store").toFile()
        val file = File(folder, "test.preferences_pb")
        var scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        try {
            var store = AutomaticStore(PreferenceDataStoreFactory.create(scope = scope) { file }, scope)
            assertFalse(store.consent())
            store.setEnabled(true)
            store.saveSession(42, ChargingSession(connected = true, suppressed = true))
            scope.coroutineContext[Job]!!.cancelAndJoin()
            scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
            store = AutomaticStore(PreferenceDataStoreFactory.create(scope = scope) { file }, scope)
            assertTrue(store.consent())
            assertEquals(ChargingSession(true, true), store.restoreSession(42))
            assertEquals(ChargingSession(), store.restoreSession(43))
            store.setEnabled(false)
            assertFalse(store.consent())
        } finally {
            scope.coroutineContext[Job]!!.cancelAndJoin()
            folder.deleteRecursively()
        }
    }
}
