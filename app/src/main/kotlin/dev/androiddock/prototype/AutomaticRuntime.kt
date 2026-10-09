package dev.androiddock.prototype

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import dev.androiddock.core.model.ChargingEnvironment
import dev.androiddock.core.model.ChargingScreenSignal

sealed interface ChargingEvent {
    data object Refresh : ChargingEvent
    data class ScreenChanged(val signal: ChargingScreenSignal, val session: Long?, val environment: ChargingEnvironment) : ChargingEvent
    data object Tick : ChargingEvent
    data object Disable : ChargingEvent
    data class ModesChanged(val modes: AutomaticModes) : ChargingEvent
    data class Shown(val session: Long) : ChargingEvent
    data class Exit(val session: Long) : ChargingEvent
    data class UserLeaveHint(val session: Long) : ChargingEvent
    data class WindowStopped(val session: Long) : ChargingEvent
}

/** Process-local bridge; persisted power-cycle policy lives in AutomaticStore. */
class AutomaticRuntime {
    internal val display = MutableStateFlow<Long?>(null)
    val displaySession = display.asStateFlow()
    internal val mutableStatus = MutableStateFlow("Наблюдение не запущено")
    val status = mutableStatus.asStateFlow()
    internal var send: ((ChargingEvent) -> Unit)? = null
    fun shown(session: Long) { send?.invoke(ChargingEvent.Shown(session)) }
    fun exit(session: Long) { send?.invoke(ChargingEvent.Exit(session)) }
    fun userLeaveHint(session: Long) { send?.invoke(ChargingEvent.UserLeaveHint(session)) }
    fun windowStopped(session: Long) { send?.invoke(ChargingEvent.WindowStopped(session)) }
}
