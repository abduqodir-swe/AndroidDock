package dev.androiddock.prototype

import androidx.lifecycle.ViewModel
import dev.androiddock.core.model.ClockStateHolder

class ClockViewModel : ViewModel() {
    private val holder = ClockStateHolder()
    val state = holder.state

    fun start() = holder.start()
    fun stop() = holder.stop()

    override fun onCleared() {
        holder.close()
    }
}
