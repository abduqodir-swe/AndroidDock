package dev.androiddock.prototype

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class AutomaticSettingsActivity : ComponentActivity() {
    private var permissions by mutableStateOf<AutomaticPermissions?>(null)
    private var busy by mutableStateOf(false)
    private var error by mutableStateOf<String?>(null)

    override fun onResume() {
        super.onResume()
        permissions = AutomaticPermissions.read(this)
        lifecycleScope.launch {
            try {
                if (permissions?.overlay == true && appContainer.automatic.consent()) ChargingWatchService.start(this@AutomaticSettingsActivity)
            } catch (e: CancellationException) { throw e }
            catch (_: Exception) { error = "Не удалось прочитать согласие. Попробуйте открыть приложение снова." }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val enabled by appContainer.automatic.enabled.collectAsStateWithLifecycle()
            val alwaysOn by appContainer.automatic.alwaysOn.collectAsStateWithLifecycle()
            val status by appContainer.automaticRuntime.status.collectAsStateWithLifecycle()
            val notifications = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
                permissions = AutomaticPermissions.read(this)
            }
            DockTheme {
                Column(Modifier.fillMaxSize().safeDrawingPadding().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Автопоказ StandBy", style = MaterialTheme.typography.headlineSmall)
                    Text("StandBy появится при подключении питания к погашенному или заблокированному телефону. Во время использования телефона он ждёт выключения экрана. Работают обе ориентации.")
                    Text("После разблокировки и новой блокировки StandBy возвращается без переподключения зарядки. Back/Home закрывают текущий показ. Кнопка питания оставляет экран выключенным до следующего использования телефона. Редактор доступен после разблокирования.")
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Автоматически на зарядке", Modifier.weight(1f))
                        Switch(checked = enabled, enabled = !busy && permissions != null,
                            onCheckedChange = { changeMode(it) }, modifier = Modifier.testTag("automatic-switch").semantics { contentDescription = "Автоматически на зарядке" })
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Always On — без зарядки", Modifier.weight(1f))
                        Switch(checked = alwaysOn, enabled = !busy && permissions != null,
                            onCheckedChange = { changeMode(it, always = true) },
                            modifier = Modifier.testTag("always-on-switch").semantics { contentDescription = "Always On — без зарядки" })
                    }
                    Text("Always On показывает StandBy при блокировке даже без питания. При отключении кабеля показ продолжается. Это обычный экран приложения: на батарее он расходует заряд, а не использует системный энергосберегающий AOD.")
                    Text(status, Modifier.testTag("automatic-status"))
                    error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.testTag("automatic-error")) }
                    HorizontalDivider()
                    Text(if (permissions?.overlay == true) "Поверх других приложений: разрешено" else "Поверх других приложений: требуется", Modifier.testTag("overlay-status"))
                    OutlinedButton(onClick = { openAutomaticPermission(AutomaticPermissionPage.OVERLAY) }) { Text("Настроить показ поверх приложений") }
                    Text(if (permissions?.notifications == true) "Уведомления: разрешены" else "Уведомления: выключены; статус службы может быть скрыт")
                    OutlinedButton(onClick = {
                        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) notifications.launch(Manifest.permission.POST_NOTIFICATIONS)
                        else openAutomaticPermission(AutomaticPermissionPage.NOTIFICATIONS)
                    }) { Text("Настроить уведомления") }
                    Text("HyperOS: в «Других разрешениях» проверьте «Открывать окна в фоновом режиме» и «Показывать на экране блокировки». Их состояние приложение достоверно проверить не может.")
                    OutlinedButton(onClick = { openAutomaticPermission(AutomaticPermissionPage.OEM_WINDOWS) }) { Text("Другие разрешения HyperOS") }
                    Text("Фоновый автозапуск помогает восстановлению после перезагрузки. Состояние: проверьте вручную.")
                    OutlinedButton(onClick = { openAutomaticPermission(AutomaticPermissionPage.OEM_AUTOSTART) }) { Text("Фоновый автозапуск HyperOS") }
                    Text(if (permissions?.batteryUnrestricted == true) "Оптимизация батареи Android: исключение выдано" else "Оптимизация батареи Android: исключение не выдано")
                    Text("Дополнительно в карточке AndroidDock на HyperOS выберите контроль активности «Нет ограничений», если прошивка останавливает наблюдение. Этот OEM-режим приложение проверить не может.")
                    OutlinedButton(onClick = { openAutomaticPermission(AutomaticPermissionPage.BATTERY) }) { Text("Оптимизация батареи Android") }
                    OutlinedButton(onClick = { openAutomaticPermission(AutomaticPermissionPage.APP_INFO) }) { Text("Карточка приложения / батарея HyperOS") }
                    Text("После перезагрузки наблюдение восстанавливается по сохранённому согласию, после первого разблокирования. Если система остановила службу, открытие AndroidDock восстановит её. Принудительную остановку приложение не обходит.")
                    TextButton(onClick = { finish() }) { Text("Назад") }
                }
            }
        }
    }

    private fun changeMode(enable: Boolean, always: Boolean = false) {
        if (busy) return
        permissions = AutomaticPermissions.read(this)
        if (enable && permissions?.overlay != true) {
            error = "Сначала разрешите показ поверх других приложений. Затем включите режим снова."
            return
        }
        busy = true
        error = null
        lifecycleScope.launch {
            try {
                if (always) appContainer.automatic.setAlwaysOn(enable) else appContainer.automatic.setEnabled(enable)
                if (appContainer.automatic.consent()) {
                    if (!ChargingWatchService.start(this@AutomaticSettingsActivity)) error = "Система запретила старт службы; проверьте настройки HyperOS. Согласие сохранено."
                } else {
                    // Consent is durably off before stopping the observer or a racing boot/update callback.
                    stopService(Intent(this@AutomaticSettingsActivity, ChargingWatchService::class.java))
                    appContainer.automaticRuntime.display.value = null
                    appContainer.automaticRuntime.mutableStatus.value = "Наблюдение отключено"
                }
            } catch (e: CancellationException) { throw e }
            catch (_: Exception) {
                error = "Не удалось сохранить режим; попробуйте ещё раз."
                Toast.makeText(this@AutomaticSettingsActivity, error, Toast.LENGTH_LONG).show()
            } finally { busy = false }
        }
    }
}
