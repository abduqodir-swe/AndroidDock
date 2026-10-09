package dev.androiddock.prototype

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import dev.androiddock.core.model.ChargingSession
import dev.androiddock.core.model.SavedChargingSession
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

data class AutomaticModes(val onPower: Boolean = false, val alwaysOn: Boolean = false) {
    val active get() = onPower || alwaysOn
}

class AutomaticStore(private val store: DataStore<Preferences>, scope: CoroutineScope) {
    private val key = booleanPreferencesKey("automatic_on_power")
    private val alwaysKey = booleanPreferencesKey("automatic_always_on")
    private val sessionKey = stringPreferencesKey("automatic_session_v1")
    val modeChanges = store.data.map { AutomaticModes(it[key] ?: false, it[alwaysKey] ?: false) }.distinctUntilChanged()
    val consentChanges = modeChanges.map { it.active }.distinctUntilChanged()
    val enabled = modeChanges.map { it.onPower }.catch { emit(false) }.stateIn(scope, SharingStarted.Eagerly, false)
    val alwaysOn = modeChanges.map { it.alwaysOn }.catch { emit(false) }.stateIn(scope, SharingStarted.Eagerly, false)
    suspend fun modes() = store.data.first().let { AutomaticModes(it[key] ?: false, it[alwaysKey] ?: false) }
    suspend fun consent() = modes().active
    suspend fun setEnabled(value: Boolean) {
        store.edit {
            // A new explicit opt-in starts fresh, independently of an old presentation pause.
            if (value && it[key] != true && it[alwaysKey] != true) it.remove(sessionKey)
            it[key] = value
        }
    }
    suspend fun setAlwaysOn(value: Boolean) {
        store.edit {
            if (value && it[alwaysKey] != true) it.remove(sessionKey)
            it[alwaysKey] = value
        }
    }
    suspend fun disableAll() { store.edit { it[key] = false; it[alwaysKey] = false } }
    suspend fun restoreSession(boot: Int): ChargingSession {
        val raw = store.data.first()[sessionKey] ?: return ChargingSession()
        val saved = runCatching { Json.decodeFromString<SavedChargingSession>(raw) }.getOrNull()
        return if (saved?.boot == boot) saved.session else ChargingSession()
    }
    suspend fun saveSession(boot: Int, session: ChargingSession) {
        store.edit { it[sessionKey] = Json.encodeToString(SavedChargingSession(boot, session)) }
    }
}
