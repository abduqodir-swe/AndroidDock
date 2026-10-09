package dev.androiddock.prototype

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import kotlinx.coroutines.*

/** BOOT_COMPLETED occurs after unlock. There is no force-stop escape or power-broadcast FGS start. */
class AutomaticRestoreReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED && intent.action != Intent.ACTION_MY_PACKAGE_REPLACED) return
        val result = goAsync()
        context.appContainer.scope.launch {
            try {
                withTimeout(8_000) {
                    if (context.appContainer.automatic.consent() && Settings.canDrawOverlays(context)) {
                        Log.i(ChargingWatchService.TAG, "Consent-based restore: ${intent.action}")
                        ChargingWatchService.start(context)
                    }
                }
            } catch (error: CancellationException) { throw error }
            catch (error: Exception) { Log.e(ChargingWatchService.TAG, "Restore failed", error) }
            finally { result.finish() }
        }
    }
}
