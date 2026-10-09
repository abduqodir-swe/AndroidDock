package dev.androiddock.core.model

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ClockStateHolderTest {
    @Test fun initialStateUsesCurrentWallTime() = runTest {
        ClockStateHolder(StandardTestDispatcher(testScheduler)) { 123_456L }.use { holder ->
            assertEquals(123_456L, holder.state.value.epochMillis)
        }
    }

    @Test fun runningClockReadsWallTimeEverySecond() = runTest {
        ClockStateHolder(StandardTestDispatcher(testScheduler)) {
            123_000L + testScheduler.currentTime
        }.use { holder ->
            holder.start()
            runCurrent()
            advanceTimeBy(1_000)
            runCurrent()
            assertEquals(124_000L, holder.state.value.epochMillis)
            advanceTimeBy(2_000)
            runCurrent()
            assertEquals(126_000L, holder.state.value.epochMillis)
        }
    }

    @Test fun systemTimeChangeIsReflectedWithoutCountingTicks() = runTest {
        var now = 100_000L
        ClockStateHolder(StandardTestDispatcher(testScheduler)) { now }.use { holder ->
            holder.start()
            runCurrent()
            now = 50_000L
            advanceTimeBy(1_000)
            runCurrent()
            assertEquals(now, holder.state.value.epochMillis)
        }
    }

    @Test fun stoppingPausesUpdatesAndStartingResynchronizes() = runTest {
        var now = 100_000L
        ClockStateHolder(StandardTestDispatcher(testScheduler)) { now }.use { holder ->
            holder.start()
            runCurrent()
            holder.stop()
            now = 200_000L
            advanceTimeBy(10_000)
            runCurrent()
            assertEquals(100_000L, holder.state.value.epochMillis)
            holder.start()
            assertEquals(now, holder.state.value.epochMillis)
        }
    }

    @Test fun repeatedStartDoesNotCreateExtraTickers() = runTest {
        var reads = 0
        ClockStateHolder(StandardTestDispatcher(testScheduler)) { (++reads).toLong() }.use { holder ->
            holder.start()
            holder.start()
            holder.start()
            runCurrent()
            val beforeTick = reads
            advanceTimeBy(1_000)
            runCurrent()
            assertEquals(beforeTick + 1, reads)
        }
    }

    @Test fun closingCancelsScopeAndPreventsRestart() = runTest {
        var now = 100_000L
        val holder = ClockStateHolder(StandardTestDispatcher(testScheduler)) { now }
        holder.start()
        runCurrent()
        holder.close()
        now = 200_000L
        holder.start()
        advanceTimeBy(10_000)
        runCurrent()
        assertEquals(100_000L, holder.state.value.epochMillis)
    }
}
