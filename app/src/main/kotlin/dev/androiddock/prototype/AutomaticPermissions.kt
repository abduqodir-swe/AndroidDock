package dev.androiddock.prototype

import android.Manifest
import android.app.Activity
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.net.toUri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast

internal data class AutomaticPermissions(val overlay: Boolean, val notifications: Boolean, val batteryUnrestricted: Boolean) {
    companion object {
        fun read(context: Context) = AutomaticPermissions(
            Settings.canDrawOverlays(context),
            context.getSystemService(NotificationManager::class.java).areNotificationsEnabled() &&
                (Build.VERSION.SDK_INT < 33 || context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED),
            context.getSystemService(PowerManager::class.java).isIgnoringBatteryOptimizations(context.packageName),
        )
    }
}

internal enum class AutomaticPermissionPage { OVERLAY, NOTIFICATIONS, OEM_WINDOWS, OEM_AUTOSTART, BATTERY, APP_INFO }

/** Public verified Android checks only; HyperOS private grants deliberately remain 'check manually'. */
internal fun Activity.openAutomaticPermission(page: AutomaticPermissionPage) {
    val info = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, "package:$packageName".toUri())
    val preferred = when (page) {
        AutomaticPermissionPage.OVERLAY -> Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, "package:$packageName".toUri())
        AutomaticPermissionPage.NOTIFICATIONS -> Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        AutomaticPermissionPage.OEM_WINDOWS -> Intent().setComponent(ComponentName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity")).putExtra("extra_pkgname", packageName)
        AutomaticPermissionPage.OEM_AUTOSTART -> Intent().setComponent(ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"))
        AutomaticPermissionPage.BATTERY -> Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        AutomaticPermissionPage.APP_INFO -> info
    }
    for (candidate in listOf(preferred, info, Intent(Settings.ACTION_SETTINGS))) {
        try { startActivity(candidate.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)); return }
        catch (_: ActivityNotFoundException) { }
        catch (_: SecurityException) { }
    }
    Toast.makeText(this, "Откройте системные настройки → Приложения → AndroidDock", Toast.LENGTH_LONG).show()
}
