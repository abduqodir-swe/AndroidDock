package dev.androiddock.core.model

import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class ChargingStateHolderTest {
    private val batteryAlways = ChargingEnvironment(false, false, true, true, alwaysOn = true)
    private fun batteryShowing() = ChargingStateHolder(alwaysOn = true).apply {
        update(batteryAlways, 0); tick(700); shown(activeSession!!)
    }
    @Test fun alwaysOnLaunchesOnBatteryAndCableChangesKeepTheSameWindow() {
        val h = batteryShowing(); val id = h.activeSession
        assertTrue(h.displayAllowed)
        h.update(batteryAlways.copy(plugged = true, interactive = true), 1_000)
        assertTrue(h.update(batteryAlways.copy(interactive = true), 2_000).isEmpty())
        assertEquals(id, h.activeSession); assertFalse(h.connected)
    }
    @Test fun alwaysOnUnplugDoesNotCancelPending() {
        val h = ChargingStateHolder(alwaysOn = true)
        h.update(batteryAlways.copy(plugged = true), 0)
        h.update(batteryAlways, 100)
        assertEquals(listOf(ChargingEffect.LAUNCH), h.tick(700))
    }
    @Test fun alwaysOnSupportsRepeatedUseAndLockOnBattery() {
        val h = batteryShowing(); val ids = mutableSetOf(h.activeSession)
        repeat(3) { i ->
            val time = 1_000L + i * 2_000L
            h.update(batteryAlways.copy(interactive = true, keyguardLocked = false), time, ChargingScreenSignal.USER_PRESENT, h.activeSession)
            h.update(batteryAlways, time + 100, ChargingScreenSignal.SCREEN_OFF)
            assertEquals(listOf(ChargingEffect.LAUNCH), h.tick(time + 800))
            assertTrue(ids.add(h.activeSession)); h.shown(h.activeSession!!)
        }
        assertFalse(h.connected)
    }
    @Test fun alwaysOnPowerButtonPauseSurvivesCableChangesAndRearmsAfterUse() {
        val h = batteryShowing()
        h.update(batteryAlways, 1_000, ChargingScreenSignal.SCREEN_OFF, h.activeSession)
        h.update(batteryAlways.copy(plugged = true), 2_000)
        h.update(batteryAlways, 3_000)
        assertTrue(h.tick(10_000).isEmpty())
        assertEquals(ChargingPause.DISPLAY_OFF, h.pause)
        h.update(batteryAlways.copy(interactive = true), 11_000, ChargingScreenSignal.SCREEN_ON)
        assertTrue(h.tick(12_000).isEmpty())
        h.update(batteryAlways.copy(interactive = true, keyguardLocked = false), 13_000, ChargingScreenSignal.USER_PRESENT)
        h.update(batteryAlways, 14_000, ChargingScreenSignal.SCREEN_OFF)
        assertEquals(listOf(ChargingEffect.LAUNCH), h.tick(14_700))
    }
    @Test fun turningAlwaysOnOffOnBatteryClosesWindowAndCancelsPending() {
        val h = batteryShowing()
        assertEquals(listOf(ChargingEffect.CLOSE), h.update(batteryAlways.copy(alwaysOn = false), 1_000))
        assertTrue(h.tick(10_000).isEmpty()); assertFalse(h.displayAllowed)
        val pending = ChargingStateHolder(alwaysOn = true)
        pending.update(batteryAlways, 0)
        pending.update(batteryAlways.copy(alwaysOn = false), 100)
        assertTrue(pending.tick(700).isEmpty())
    }
    @Test fun turningAlwaysOnOffWhilePluggedRetainsChargingPresentation() {
        val h = batteryShowing(); val id = h.activeSession
        h.update(batteryAlways.copy(plugged = true, interactive = true), 1_000)
        assertTrue(h.update(batteryAlways.copy(plugged = true, interactive = true, alwaysOn = false), 2_000).isEmpty())
        assertEquals(id, h.activeSession); assertTrue(h.displayAllowed)
    }
    @Test fun restoredAlwaysOnPresentationDoesNotImmediatelyWakeBatteryPhone() {
        val h = ChargingStateHolder(batteryShowing().session, alwaysOn = true)
        h.update(batteryAlways, 0); assertTrue(h.tick(700).isEmpty())
        h.update(batteryAlways.copy(interactive = true, keyguardLocked = false), 1_000)
        h.update(batteryAlways, 2_000)
        assertEquals(listOf(ChargingEffect.LAUNCH), h.tick(2_700))
    }
    @Test fun explicitAlwaysOnOptInRearmsButCannotBypassDisableOrPermission() {
        val h = ChargingStateHolder(ChargingSession(false, true))
        h.update(batteryAlways, 0)
        assertEquals(listOf(ChargingEffect.LAUNCH), h.tick(700))
        h.disable(); h.update(batteryAlways, 1_000)
        assertTrue(h.tick(2_000).isEmpty())
        val denied = ChargingStateHolder(alwaysOn = true)
        denied.update(batteryAlways.copy(allowed = false), 0)
        assertTrue(denied.tick(700).isEmpty()); assertFalse(denied.displayAllowed)
    }
    private val asleep = ChargingEnvironment(true, false, true, true)
    private val using = asleep.copy(interactive = true, keyguardLocked = false)
    private val awakeLocked = asleep.copy(interactive = true)
    private fun pending() = ChargingStateHolder().apply { update(asleep, 0) }
    private fun showing() = pending().apply { tick(700); shown(activeSession!!) }
    private fun unlock(h: ChargingStateHolder, now: Long) = h.update(using, now, ChargingScreenSignal.USER_PRESENT, h.activeSession)
    private fun lock(h: ChargingStateHolder, now: Long): Long {
        h.update(asleep, now, ChargingScreenSignal.SCREEN_OFF)
        assertEquals(listOf(ChargingEffect.LAUNCH), h.tick(now + 700))
        return h.activeSession!!.also { h.shown(it) }
    }

    @Test fun pluggedAndIdleLaunchesOnceIncludingFullBattery() {
        val h = pending()
        assertTrue(h.tick(699).isEmpty())
        assertEquals(listOf(ChargingEffect.LAUNCH), h.tick(700))
        h.shown(h.activeSession!!)
        h.update(awakeLocked, 800)
        assertTrue(h.tick(9_000).isEmpty())
        assertEquals(ChargingPhase.SHOWING, h.phase)
    }
    @Test fun phoneInUseWaitsUntilScreenOff() {
        val h = ChargingStateHolder()
        h.update(using, 0)
        assertNull(h.deadline)
        lock(h, 10)
    }
    @Test fun unlockAndRelockOnSamePowerLaunchNewSession() {
        val h = showing(); val old = h.activeSession
        assertEquals(listOf(ChargingEffect.CLOSE), unlock(h, 1_000))
        assertEquals(ChargingPhase.WAITING, h.phase)
        assertNotEquals(old, lock(h, 1_100))
        assertTrue(h.connected)
    }
    @Test fun manyUnlockRelockCyclesDoNotRequireUnplugging() {
        val h = showing(); val ids = mutableSetOf(h.activeSession)
        repeat(5) { index ->
            val time = 1_000L + index * 2_000L
            unlock(h, time)
            assertTrue(ids.add(lock(h, time + 100)))
        }
        assertTrue(h.connected); assertNull(h.pause)
    }
    @Test fun backClosesOnlyThisPresentationAndUseRearmsNextLock() {
        val h = showing(); val id = h.activeSession!!
        assertEquals(listOf(ChargingEffect.CLOSE), h.exit(id))
        h.update(asleep, 1_000, ChargingScreenSignal.SCREEN_OFF)
        assertTrue(h.tick(2_000).isEmpty())
        unlock(h, 2_100); lock(h, 2_200)
    }
    @Test fun homeOnUnlockedPhoneRearmsFromActualUse() {
        val h = showing(); val id = h.activeSession!!
        h.userLeaveHint(id)
        h.update(using, 1_000)
        assertEquals(ChargingPhase.WAITING, h.phase)
        lock(h, 1_100)
    }
    @Test fun hyperOsLeaveHintDuringOwnWakeBeforeFocusDoesNotCloseLaunch() {
        val h = pending(); h.tick(700); val id = h.activeSession!!
        assertTrue(h.userLeaveHint(id).isEmpty())
        h.update(awakeLocked, 800, ChargingScreenSignal.SCREEN_ON, id)
        assertTrue(h.userLeaveHint(id).isEmpty())
        h.shown(id)
        h.windowStopped(id)
        assertEquals(id, h.activeSession)
        assertEquals(ChargingPhase.SHOWING, h.phase)
        assertEquals(listOf(ChargingEffect.CLOSE), h.userLeaveHint(id))
        unlock(h, 1_000); lock(h, 1_100)
    }
    @Test fun powerButtonDuringDisplayNeverLoopsDespiteDuplicatesAndWakeOnKeyguard() {
        val h = showing(); val id = h.activeSession!!
        assertEquals(listOf(ChargingEffect.CLOSE), h.update(asleep, 1_000, ChargingScreenSignal.SCREEN_OFF, id))
        assertEquals(ChargingPause.DISPLAY_OFF, h.pause)
        repeat(20) { i ->
            h.update(asleep, 1_001L + i, ChargingScreenSignal.SCREEN_OFF)
            assertTrue(h.tick(10_000L + i).isEmpty())
        }
        h.update(awakeLocked, 11_000, ChargingScreenSignal.SCREEN_ON)
        assertEquals(ChargingPhase.WAIT_FOR_USE, h.phase)
        assertTrue(h.tick(12_000).isEmpty())
    }
    @Test fun usingAfterPowerButtonRearmsWithoutCable() {
        val h = showing()
        h.update(asleep, 1_000, ChargingScreenSignal.SCREEN_OFF, h.activeSession)
        unlock(h, 2_000); lock(h, 2_100)
    }
    @Test fun delayedOldStopExitFocusAndScreenOffDoNotCloseNewWindow() {
        val h = showing(); val old = h.activeSession!!
        unlock(h, 1_000); val next = lock(h, 1_100)
        assertTrue(h.windowStopped(old).isEmpty())
        assertTrue(h.exit(old).isEmpty())
        h.shown(old)
        assertTrue(h.update(asleep, 2_000, ChargingScreenSignal.SCREEN_OFF, old).isEmpty())
        assertEquals(next, h.activeSession); assertEquals(ChargingPhase.SHOWING, h.phase)
    }
    @Test fun stopAndRefocusFromSystemWindowOrRotationDoNotSuppress() {
        val h = showing(); val id = h.activeSession!!
        repeat(3) { h.windowStopped(id); h.shown(id) }
        h.update(awakeLocked, 1_000)
        assertEquals(id, h.activeSession); assertNull(h.pause)
        assertTrue(h.tick(10_000).isEmpty())
    }
    @Test fun coveredLockedWindowGetsOneBoundedRestoreBeforeSystemTimeout() {
        val h = showing(); val id = h.activeSession!!
        h.update(awakeLocked, 800)
        h.windowStopped(id, interactive = true, keyguardLocked = true, now = 1_000)
        assertEquals(1_500L, h.deadline)
        assertTrue(h.tick(1_499).isEmpty())
        assertEquals(listOf(ChargingEffect.RESTORE_WINDOW), h.tick(1_500))
        assertTrue(h.tick(2_000).isEmpty())
        assertEquals(id, h.activeSession)
        assertEquals(ChargingPhase.SHOWING, h.phase)
        h.shown(id)
        h.windowStopped(id, interactive = true, keyguardLocked = true, now = 3_000)
        assertNull(h.deadline)
        assertTrue(h.tick(3_500).isEmpty())
        // A persistent call window can retake focus; do not fight it in a loop.
        h.shown(id)
        h.windowStopped(id, interactive = true, keyguardLocked = true, now = 31_500)
        assertEquals(listOf(ChargingEffect.RESTORE_WINDOW), h.tick(32_000))
    }
    @Test fun restoredFocusOrPowerOffCancelsCoveredWindowRecovery() {
        val focused = showing(); val id = focused.activeSession!!
        focused.update(awakeLocked, 800)
        focused.windowStopped(id, interactive = true, keyguardLocked = true, now = 1_000)
        focused.shown(id)
        assertNull(focused.deadline)
        assertTrue(focused.tick(1_500).isEmpty())

        val power = showing(); val powerId = power.activeSession!!
        power.update(awakeLocked, 800)
        power.windowStopped(powerId, interactive = true, keyguardLocked = true, now = 1_000)
        assertEquals(listOf(ChargingEffect.CLOSE), power.update(asleep, 1_100, ChargingScreenSignal.SCREEN_OFF, powerId))
        assertTrue(power.tick(1_500).isEmpty())
        assertEquals(ChargingPause.DISPLAY_OFF, power.pause)
        assertTrue(power.windowStopped(powerId, interactive = true, keyguardLocked = true, now = 2_000).isEmpty())
        assertNull(power.deadline)
    }
    @Test fun displayTurnScreenOnDoesNotCountAsUseOnPhoneWithoutKeyguard() {
        val h = ChargingStateHolder()
        h.update(asleep.copy(keyguardLocked = false), 0); h.tick(700)
        val id = h.activeSession!!
        h.update(using, 800, ChargingScreenSignal.SCREEN_ON, id)
        h.shown(id)
        h.update(using, 900, ChargingScreenSignal.USER_PRESENT, id)
        assertEquals(id, h.activeSession); assertEquals(ChargingPhase.SHOWING, h.phase)
        h.update(asleep.copy(keyguardLocked = false), 1_000, ChargingScreenSignal.SCREEN_OFF, id)
        assertTrue(h.tick(5_000).isEmpty())
        h.update(using, 6_000, ChargingScreenSignal.SCREEN_ON)
        lock(h, 6_100)
    }
    @Test fun displayScreenOnDoesNotCloseLaunchingWindowWithKeyguard() {
        val h = pending(); h.tick(700); val id = h.activeSession!!
        h.update(awakeLocked, 800, ChargingScreenSignal.SCREEN_ON, id)
        assertEquals(ChargingPhase.LAUNCHING, h.phase)
        h.shown(id); assertEquals(ChargingPhase.SHOWING, h.phase)
    }
    @Test fun lateOwnWakeCannotRearmExitedWindowWithoutKeyguard() {
        val h = ChargingStateHolder()
        h.update(asleep.copy(keyguardLocked = false), 0); h.tick(700); val id = h.activeSession!!
        h.exit(id)
        h.update(using, 800, ChargingScreenSignal.SCREEN_ON, id)
        assertEquals(ChargingPhase.WAIT_FOR_USE, h.phase)
    }
    @Test fun disconnectCancelsPendingAndClosesDisplayImmediately() {
        val h = pending(); h.update(asleep.copy(plugged = false), 100)
        assertTrue(h.tick(700).isEmpty())
        val visible = showing()
        assertEquals(listOf(ChargingEffect.CLOSE), visible.update(asleep.copy(plugged = false), 900))
        assertFalse(visible.displayAllowed)
    }
    @Test fun stableDisconnectAndReconnectBeginsNewCycle() {
        val h = showing(); h.exit(h.activeSession!!)
        h.update(asleep.copy(plugged = false), 1_000)
        h.update(asleep, 3_000)
        assertEquals(listOf(ChargingEffect.LAUNCH), h.tick(3_700))
    }
    @Test fun evenShortPowerDisconnectBeginsNewCycleAfterPowerButtonPause() {
        val h = showing(); h.update(asleep, 1_000, ChargingScreenSignal.SCREEN_OFF, h.activeSession)
        h.update(asleep.copy(plugged = false), 1_100); h.update(asleep, 1_200)
        assertEquals(listOf(ChargingEffect.LAUNCH), h.tick(1_900))
        assertNull(h.pause)
    }
    @Test fun repeatedEventsDoNotPostponeDeadlineOrDuplicateLaunch() {
        val h = pending()
        h.update(asleep, 200); h.update(asleep, 400, ChargingScreenSignal.SCREEN_OFF)
        assertEquals(700L, h.deadline)
        assertEquals(listOf(ChargingEffect.LAUNCH), h.tick(700))
        assertTrue(h.tick(701).isEmpty())
    }
    @Test fun duplicateInitialScreenOffDuringLaunchingDoesNotActAsPowerButton() {
        val h = pending(); h.tick(700); val id = h.activeSession!!
        h.update(asleep, 800, ChargingScreenSignal.SCREEN_OFF, id)
        assertEquals(ChargingPhase.LAUNCHING, h.phase)
        h.shown(id, interactive = false)
        h.update(asleep, 850, ChargingScreenSignal.SCREEN_OFF, id)
        assertEquals(ChargingPhase.SHOWING, h.phase)
        h.update(awakeLocked, 900, ChargingScreenSignal.SCREEN_ON, id)
        assertEquals(listOf(ChargingEffect.CLOSE), h.update(asleep, 1_000, ChargingScreenSignal.SCREEN_OFF, id))
    }
    @Test fun staleOffBroadcastDeliveredWhileScreenIsOnDoesNotCloseWindow() {
        val h = showing(); val id = h.activeSession!!
        h.update(awakeLocked, 1_000, ChargingScreenSignal.SCREEN_OFF, id)
        assertEquals(id, h.activeSession); assertNull(h.pause)
    }
    @Test fun screenUnlockCancelsDelayedLaunch() {
        val h = pending(); unlock(h, 200)
        assertTrue(h.tick(700).isEmpty()); assertNull(h.deadline)
        lock(h, 1_000)
    }
    @Test fun permissionFailureIsSeparateAndRecoveryNeedsUseOrOptIn() {
        val h = showing()
        assertEquals(listOf(ChargingEffect.CLOSE), h.update(asleep.copy(allowed = false), 1_000))
        assertEquals(ChargingPhase.BLOCKED, h.phase); assertEquals(ChargingPause.PERMISSION, h.pause)
        h.update(asleep, 2_000); assertTrue(h.tick(3_000).isEmpty())
        unlock(h, 3_100); lock(h, 3_200)
    }
    @Test fun launchFailureDoesNotLoopAndUsePermitsNewAttempt() {
        val h = pending(); h.tick(700)
        assertEquals(listOf(ChargingEffect.CLOSE, ChargingEffect.LAUNCH_TIMEOUT), h.tick(5_700))
        assertEquals(ChargingPause.LAUNCH_ERROR, h.pause)
        h.update(asleep, 6_000, ChargingScreenSignal.SCREEN_OFF); assertTrue(h.tick(7_000).isEmpty())
        unlock(h, 8_000); lock(h, 8_100)
    }
    @Test fun disabledModeNeverRearmsFromScreenOrPowerEvents() {
        val h = showing(); assertEquals(listOf(ChargingEffect.CLOSE), h.disable())
        unlock(h, 1_000); h.update(asleep, 2_000, ChargingScreenSignal.SCREEN_OFF)
        h.update(asleep.copy(plugged = false), 3_000); h.update(asleep, 5_000)
        assertTrue(h.tick(6_000).isEmpty())
        assertEquals(ChargingPhase.DISABLED, h.phase)
        assertFalse(h.displayAllowed)
        val pending = pending(); pending.disable(); assertTrue(pending.tick(700).isEmpty())
    }
    @Test fun restoringVisibleProcessWaitsForUseThenRelock() {
        val h = ChargingStateHolder(showing().session)
        h.update(asleep, 10_000); assertTrue(h.tick(11_000).isEmpty())
        unlock(h, 12_000); lock(h, 12_100)
    }
    @Test fun legacySuppressedStateRearmsOnUseWithoutUnplugging() {
        val saved = Json.decodeFromString<SavedChargingSession>("""{"boot":42,"session":{"connected":true,"suppressed":true}}""")
        val h = ChargingStateHolder(saved.session)
        assertEquals(ChargingPause.RECOVERY, h.pause)
        h.update(asleep, 0); assertTrue(h.tick(700).isEmpty())
        unlock(h, 1_000); lock(h, 1_100)
    }
    @Test fun restoredPauseAlreadyInUseImmediatelyPermitsNextLock() {
        val h = ChargingStateHolder(ChargingSession(true, true, ChargingPause.DISPLAY_OFF))
        h.update(using, 10_000)
        assertEquals(ChargingPhase.WAITING, h.phase)
        lock(h, 11_000)
    }
    @Test fun restoredUnpluggedPauseDoesNotSuppressTheNextConnection() {
        val h = ChargingStateHolder(ChargingSession(false, true))
        h.update(asleep.copy(plugged = false), 0)
        h.update(asleep, 100)
        assertEquals(listOf(ChargingEffect.LAUNCH), h.tick(800))
        assertNull(h.pause)
    }
}
