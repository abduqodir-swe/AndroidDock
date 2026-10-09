package dev.androiddock.prototype

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

private val Context.dockStore by preferencesDataStore(name = "androiddock")

class DockApplication : Application() {
    val container by lazy { AppContainer(this) }
}

class AppContainer(context: Context) {
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    val repository = DockRepository(context.dockStore, scope)
    val battery = BatteryProvider(context.applicationContext, scope)
    val photos = PhotoStore(context.applicationContext)
}

val Context.appContainer: AppContainer
    get() = (applicationContext as DockApplication).container
