package dev.androiddock.core.model

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/** Owns its scope. Hosts start/stop observation and close it when finally destroyed. */
class ClockStateHolder(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val nowMillis: () -> Long = System::currentTimeMillis,
) : AutoCloseable {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val mutableState = MutableStateFlow(ClockState(nowMillis()))
    val state: StateFlow<ClockState> = mutableState.asStateFlow()
    private var ticker: Job? = null

    // Host lifecycle methods are called on one thread (the Android main thread).
    fun start() {
        if (ticker?.isActive == true || !scope.isActive) return
        mutableState.value = ClockState(nowMillis())
        ticker = scope.launch {
            while (isActive) {
                delay(1_000)
                // Read wall time each time: changes to the system clock must be reflected.
                mutableState.value = ClockState(nowMillis())
            }
        }
    }

    fun stop() {
        ticker?.cancel()
        ticker = null
    }

    override fun close() {
        scope.cancel()
        ticker = null
    }
}
