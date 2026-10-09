package dev.androiddock.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class ChargingPause { USER_EXIT, DISPLAY_OFF, PERMISSION, LAUNCH_ERROR, RECOVERY, MODE_DISABLED }

@Serializable
data class ChargingSession(
    val connected: Boolean = false,
    // Kept for reading 0.2.0 snapshots. It no longer means 'until unplugged'.
    val suppressed: Boolean = false,
    val pause: ChargingPause? = null,
)

@Serializable
data class SavedChargingSession(val boot: Int, val session: ChargingSession)

enum class ChargingPhase { WAITING, PENDING, LAUNCHING, SHOWING, WAIT_FOR_USE, BLOCKED, DISABLED }
enum class ChargingEffect { LAUNCH, CLOSE, LAUNCH_TIMEOUT, RESTORE_WINDOW }
enum class ChargingScreenSignal { NONE, SCREEN_ON, SCREEN_OFF, USER_PRESENT }
data class ChargingEnvironment(val plugged: Boolean, val interactive: Boolean, val keyguardLocked: Boolean, val allowed: Boolean, val alwaysOn: Boolean = false)

/** Event-driven presentation policy. Clock and unique window identifiers are supplied by the host. */
class ChargingStateHolder(
    restored: ChargingSession = ChargingSession(),
    private val nextSessionId: () -> Long = run { var id = 0L; { ++id } },
    alwaysOn: Boolean = false,
) {
    var connected = restored.connected; private set
    var pause = restored.pause ?: if (restored.suppressed) ChargingPause.RECOVERY else null; private set
    var phase = when (pause) {
        ChargingPause.MODE_DISABLED -> ChargingPhase.DISABLED
        ChargingPause.PERMISSION -> ChargingPhase.BLOCKED
        null -> ChargingPhase.WAITING
        else -> ChargingPhase.WAIT_FOR_USE
    }; private set
    var activeSession: Long? = null; private set
    private var environment = ChargingEnvironment(false, false, true, false, alwaysOn)
    private var presentationSawKeyguard = false
    private var presentationWasInteractive = false
    private var lastClosedSession: Long? = null
    private var launchAt: Long? = null
    private var timeoutAt: Long? = null
    private var restoreAt: Long? = null
    private var lastRestoreAt: Long? = null
    private val powerEligible get() = connected || environment.alwaysOn
    val observing get() = powerEligible
    val displayAllowed get() = powerEligible && environment.allowed && activeSession != null && phase in setOf(ChargingPhase.LAUNCHING, ChargingPhase.SHOWING)
    // A lost visible window never immediately wakes the phone again after a process restart.
    val session get() = ChargingSession(connected, pause != null || activeSession != null, pause ?: if (activeSession != null) ChargingPause.RECOVERY else null)
    val deadline get() = listOfNotNull(launchAt, timeoutAt, restoreAt).minOrNull()

    fun update(
        current: ChargingEnvironment,
        now: Long,
        signal: ChargingScreenSignal = ChargingScreenSignal.NONE,
        signalSession: Long? = null,
    ): List<ChargingEffect> {
        // A delayed SCREEN_OFF from an old window cannot dismiss a new presentation.
        if (signal == ChargingScreenSignal.SCREEN_OFF && signalSession != null && activeSession != null && signalSession != activeSession) return emptyList()
        val effects = mutableListOf<ChargingEffect>()
        val alwaysOnChanged = current.alwaysOn != environment.alwaysOn
        environment = current
        if (!current.plugged && connected) {
            connected = false
            if (!current.alwaysOn) {
                effects += closeWindow()
                cancelLaunch()
                if (phase !in setOf(ChargingPhase.DISABLED, ChargingPhase.BLOCKED)) {
                    pause = null
                    phase = ChargingPhase.WAITING
                }
            }
        } else if (current.plugged && !connected) {
            connected = true
            // Includes a process restored from a snapshot taken while unplugged.
            if (!current.alwaysOn && phase == ChargingPhase.WAIT_FOR_USE) {
                pause = null
                phase = ChargingPhase.WAITING
            }
        }
        if (phase == ChargingPhase.DISABLED) return effects
        if (alwaysOnChanged && (current.alwaysOn || !connected)) {
            if (!current.alwaysOn) { effects += closeWindow(); cancelLaunch() }
            if (phase == ChargingPhase.WAIT_FOR_USE || (!current.alwaysOn && phase != ChargingPhase.BLOCKED)) {
                pause = null
                phase = ChargingPhase.WAITING
            }
        }
        if (!current.allowed) {
            effects += waitForUse(ChargingPause.PERMISSION)
            phase = ChargingPhase.BLOCKED
            return effects
        }
        if (phase == ChargingPhase.BLOCKED) phase = ChargingPhase.WAIT_FOR_USE

        val ownWake = signal in setOf(ChargingScreenSignal.SCREEN_ON, ChargingScreenSignal.USER_PRESENT) &&
            signalSession != null && (signalSession == activeSession || signalSession == lastClosedSession)
        if (signal == ChargingScreenSignal.SCREEN_OFF && !current.interactive && activeSession != null &&
            signalSession == activeSession && presentationWasInteractive) {
            effects += waitForUse(ChargingPause.DISPLAY_OFF)
        } else if (activeSession != null) {
            // turnScreenOn is interactive too. It cannot count as user use on an unprotected phone.
            val unlocked = presentationSawKeyguard && current.interactive &&
                (!current.keyguardLocked || signal == ChargingScreenSignal.USER_PRESENT)
            if (unlocked) {
                effects += closeWindow()
                cancelLaunch()
                pause = null
                phase = ChargingPhase.WAITING
            } else {
                presentationSawKeyguard = presentationSawKeyguard || current.keyguardLocked
                presentationWasInteractive = presentationWasInteractive || current.interactive
            }
        } else if (phase == ChargingPhase.WAIT_FOR_USE && current.interactive && !current.keyguardLocked && !ownWake) {
            pause = null
            phase = ChargingPhase.WAITING
        }
        if (phase == ChargingPhase.PENDING && !eligible()) {
            cancelLaunch(); phase = ChargingPhase.WAITING
        }
        // USER_PRESENT starts use, not another presentation, even if OEM keyguard state lags.
        if (signal != ChargingScreenSignal.USER_PRESENT) schedule(now)
        return effects
    }

    fun tick(now: Long): List<ChargingEffect> {
        if (timeoutAt?.let { now >= it } == true) {
            val effects = waitForUse(ChargingPause.LAUNCH_ERROR)
            return effects + ChargingEffect.LAUNCH_TIMEOUT
        }
        if (launchAt?.let { now >= it } == true) {
            launchAt = null
            if (phase == ChargingPhase.PENDING && eligible()) {
                phase = ChargingPhase.LAUNCHING
                activeSession = nextSessionId()
                presentationSawKeyguard = environment.keyguardLocked
                presentationWasInteractive = false
                timeoutAt = now + LAUNCH_ACK_MS
                return listOf(ChargingEffect.LAUNCH)
            }
        }
        if (restoreAt?.let { now >= it } == true) {
            restoreAt = null
            if (phase == ChargingPhase.SHOWING && activeSession != null &&
                environment.interactive && environment.keyguardLocked && displayAllowed) {
                lastRestoreAt = now
                return listOf(ChargingEffect.RESTORE_WINDOW)
            }
        }
        return emptyList()
    }

    fun shown(token: Long, interactive: Boolean = true) {
        if (token == activeSession && displayAllowed) {
            phase = ChargingPhase.SHOWING
            timeoutAt = null
            restoreAt = null
            presentationWasInteractive = presentationWasInteractive || interactive
        }
    }
    fun exit(token: Long): List<ChargingEffect> =
        if (token == activeSession) waitForUse(ChargingPause.USER_EXIT) else emptyList()

    // HyperOS can send this hint during our own wake, before the window ever gains focus.
    fun userLeaveHint(token: Long): List<ChargingEffect> =
        if (phase == ChargingPhase.SHOWING) exit(token) else emptyList()

    /** Reclaim a covered clock, but yield to a persistent system window instead of fighting it. */
    fun windowStopped(token: Long, interactive: Boolean = false, keyguardLocked: Boolean = false, now: Long = 0): List<ChargingEffect> {
        if (token == activeSession && phase == ChargingPhase.SHOWING && interactive &&
            keyguardLocked && restoreAt == null &&
            (lastRestoreAt?.let { now - it >= RESTORE_COOLDOWN_MS } ?: true)) {
            restoreAt = now + RESTORE_DELAY_MS
        }
        return emptyList()
    }

    fun launchFailed(token: Long): List<ChargingEffect> =
        if (token == activeSession) waitForUse(ChargingPause.LAUNCH_ERROR) else emptyList()

    fun disable(): List<ChargingEffect> {
        val effects = waitForUse(ChargingPause.MODE_DISABLED)
        phase = ChargingPhase.DISABLED
        return effects
    }

    private fun eligible() = powerEligible && environment.allowed && (!environment.interactive || environment.keyguardLocked)
    private fun schedule(now: Long) {
        if (phase == ChargingPhase.WAITING && eligible()) {
            phase = ChargingPhase.PENDING
            launchAt = now + LAUNCH_DELAY_MS
        }
    }
    private fun closeWindow(): List<ChargingEffect> {
        if (activeSession == null) return emptyList()
        lastClosedSession = activeSession
        activeSession = null
        restoreAt = null
        lastRestoreAt = null
        return listOf(ChargingEffect.CLOSE)
    }
    private fun waitForUse(reason: ChargingPause): List<ChargingEffect> {
        val effects = closeWindow()
        cancelLaunch()
        pause = reason
        phase = ChargingPhase.WAIT_FOR_USE
        return effects
    }
    private fun cancelLaunch() { launchAt = null; timeoutAt = null; restoreAt = null }
    companion object {
        const val LAUNCH_DELAY_MS = 700L
        const val LAUNCH_ACK_MS = 5_000L
        const val RESTORE_DELAY_MS = 500L
        const val RESTORE_COOLDOWN_MS = 30_000L
    }
}
