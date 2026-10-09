package dev.androiddock.prototype

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import dev.androiddock.core.model.Availability
import dev.androiddock.core.model.BatteryState
import dev.androiddock.core.model.batteryPercent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn

class BatteryProvider(context: Context, scope: CoroutineScope) {
    val state = callbackFlow<Availability<BatteryState>> {
        fun publish(intent: Intent) {
            val percent = batteryPercent(intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1), intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1))
            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            trySend(if (percent == null) Availability.Unavailable else Availability.Data(
                BatteryState(percent, status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL),
            ))
        }
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) = publish(intent)
        }
        // ACTION_BATTERY_CHANGED is a protected, system-only sticky broadcast.
        context.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))?.let(::publish)
        awaitClose { context.unregisterReceiver(receiver) }
    }.distinctUntilChanged().stateIn(scope, SharingStarted.WhileSubscribed(5_000), Availability.Unavailable)
}
