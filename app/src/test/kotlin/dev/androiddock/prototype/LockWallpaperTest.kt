package dev.androiddock.prototype

import dev.androiddock.core.model.*
import java.io.File
import java.nio.file.Files
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LockWallpaperTest {
    private class FakeAdapter(var supported: Boolean = true, var allowed: Boolean = true, var id: Int = 1) : LockWallpaperAdapter {
        var received: LockWallpaperRequest? = null
        var failure: Exception? = null
        override fun isSupported() = supported
        override fun isAllowed() = allowed
        override suspend fun setLockPhoto(request: LockWallpaperRequest): Int {
            failure?.let { throw it }
            received = request
            return id
        }
    }

    @Test fun explicitOperationPassesExactPhotoAndCropToLockOnlyAdapter() = runBlocking {
        withPhoto { photo ->
            val request = LockWallpaperRequest(photo, PhotoFraming(.2f, .8f, 2f), 1080, 2400)
            val adapter = FakeAdapter()
            LockWallpaperOperation(adapter).apply(request)
            assertSame(request, adapter.received)
        }
    }

    @Test fun supportPolicyAndMissingFileFailBeforeSetting() = runBlocking {
        withPhoto { photo ->
            listOf(FakeAdapter(supported = false), FakeAdapter(allowed = false)).forEach { adapter ->
                expectFailure { LockWallpaperOperation(adapter).apply(LockWallpaperRequest(photo, PhotoFraming(), 1080, 2400)) }
                assertNull(adapter.received)
            }
            val adapter = FakeAdapter()
            expectFailure { LockWallpaperOperation(adapter).apply(LockWallpaperRequest(File(photo.parentFile, "missing.jpg"), PhotoFraming(), 1080, 2400)) }
            assertNull(adapter.received)
        }
    }

    @Test fun zeroIdAndAdapterErrorsAreNeverSuccess() = runBlocking {
        withPhoto { photo ->
            val request = LockWallpaperRequest(photo, PhotoFraming(), 1080, 2400)
            expectFailure { LockWallpaperOperation(FakeAdapter(id = 0)).apply(request) }
            expectFailure { LockWallpaperOperation(FakeAdapter().apply { failure = SecurityException("Denied") }).apply(request) }
        }
    }

    @Test fun confirmationKeepsSnapshotAcrossDraftChangesAndCancelDoesNotSet() = runBlocking {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        try {
            withPhoto { photo ->
                val vm = ClockViewModel()
                val profile = DockProfile(portraitPhoto = PhotoFraming(.1f, .9f, 2f), landscapePhoto = PhotoFraming(zoom = 3f))
                vm.editor.open(DockSettings(desktop = profile), DockOrientation.LANDSCAPE)
                vm.requestLockWallpaper(profile, photo, 2400, 1080)
                val snapshot = vm.lockRequest.value!!
                assertEquals(profile.portraitPhoto, snapshot.framing)
                assertEquals(1080, snapshot.width)
                assertEquals(2400, snapshot.height)
                assertTrue(vm.busy.value)
                vm.cancelLockWallpaper()
                assertFalse(vm.busy.value)
                assertNull(vm.lockRequest.value)
                val adapter = FakeAdapter()
                vm.confirmLockWallpaper(adapter)
                assertNull(adapter.received)
                vm.requestLockWallpaper(profile, photo, 1080, 2400)
                vm.editor.change { it.copy(mode = DockMode.BEDROOM) }
                vm.confirmLockWallpaper(adapter)
                withTimeout(5000) { vm.busy.first { !it } }
                assertEquals(snapshot, adapter.received)
                assertNotNull(vm.notice.value)
                assertNull(vm.error.value)
            }
        } finally { Dispatchers.resetMain() }
    }

    @Test fun cancelingGalleryPickerLeavesDraftAndAppearanceUntouched() {
        val vm = ClockViewModel()
        val settings = DockSettings()
        vm.editor.open(settings, DockOrientation.PORTRAIT)
        assertTrue(vm.beginPhotoPick(gallery = true))
        vm.cancelPhotoPick()
        assertEquals(settings, vm.editor.state.value!!.settings)
        assertEquals(DockAppearance.LEGACY, vm.editor.state.value!!.settings.profile().appearance)
    }

    @Test fun viewModelReportsFailedOperationAndReleasesBusy() = runBlocking {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        try {
            withPhoto { photo ->
                val vm = ClockViewModel()
                vm.requestLockWallpaper(DockProfile(), photo, 1080, 2400)
                vm.confirmLockWallpaper(FakeAdapter(id = 0))
                withTimeout(5000) { vm.busy.first { !it } }
                assertNull(vm.notice.value)
                assertNotNull(vm.error.value)
            }
        } finally { Dispatchers.resetMain() }
    }

    private suspend fun withPhoto(action: suspend (File) -> Unit) {
        val photo = Files.createTempFile("lock-photo", ".jpg").toFile()
        try { action(photo) } finally { photo.delete() }
    }

    private suspend fun expectFailure(action: suspend () -> Unit) {
        try { action(); fail("Failure was reported as success") }
        catch (_: IllegalArgumentException) { }
        catch (_: SecurityException) { }
    }
}
