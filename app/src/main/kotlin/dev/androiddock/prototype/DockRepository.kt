package dev.androiddock.prototype

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.androiddock.core.model.DockSettings
import dev.androiddock.core.model.SettingsCodec
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed interface SettingsState {
    data object Loading : SettingsState
    data class Ready(val settings: DockSettings) : SettingsState
    data object Error : SettingsState
}

class DockRepository(private val store: DataStore<Preferences>, scope: CoroutineScope) {
    private val key = stringPreferencesKey("dock_settings_v1")
    val state = store.data.map<Preferences, SettingsState> { prefs ->
        SettingsState.Ready(prefs[key]?.let(SettingsCodec::decode) ?: DockSettings())
    }.catch { emit(SettingsState.Error) }
        .stateIn(scope, SharingStarted.WhileSubscribed(5_000), SettingsState.Loading)

    // DataStore serializes edits; rapid changes cannot overwrite each other's fields.
    suspend fun update(transform: (DockSettings) -> DockSettings) {
        store.edit { prefs ->
            val current = prefs[key]?.let(SettingsCodec::decode) ?: DockSettings()
            prefs[key] = SettingsCodec.encode(transform(current))
        }
    }
}
