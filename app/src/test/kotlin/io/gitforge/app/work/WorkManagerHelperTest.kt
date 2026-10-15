package io.gitforge.app.work

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.UUID

@RunWith(JUnit4::class)
class WorkManagerHelperTest {

    private lateinit var mockWorkManager: WorkManager
    private lateinit var helper: WorkManagerHelper

    @Before
    fun setup() {
        mockWorkManager = mockk(relaxed = true)
        helper = WorkManagerHelper(mockWorkManager)
    }

    @Test
    fun `enqueueArchiveDownload schedules unique work with correct name and returns UUID`() {
        val uniqueNameSlot = slot<String>()
        val policySlot = slot<ExistingWorkPolicy>()
        val requestSlot = slot<OneTimeWorkRequest>()

        every {
            mockWorkManager.enqueueUniqueWork(
                capture(uniqueNameSlot),
                capture(policySlot),
                capture(requestSlot)
            )
        } returns mockk(relaxed = true)

        val workId = helper.enqueueArchiveDownload("torvalds", "linux", "master")

        assertThat(workId).isNotNull()
        assertThat(uniqueNameSlot.captured).isEqualTo("download_torvalds_linux_master")
        assertThat(policySlot.captured).isEqualTo(ExistingWorkPolicy.REPLACE)
        assertThat(requestSlot.captured.id).isEqualTo(workId)
        assertThat(requestSlot.captured.tags).contains("archive_download_torvalds_linux")
    }

    @Test
    fun `cancelWork invokes cancelWorkById on WorkManager`() {
        val testId = UUID.randomUUID()
        helper.cancelWork(testId)

        verify { mockWorkManager.cancelWorkById(testId) }
    }

    @Test
    fun `observeWorkInfo returns flow from WorkManager`() = runBlocking {
        val testId = UUID.randomUUID()
        val mockWorkInfo: WorkInfo = mockk(relaxed = true)

        every { mockWorkManager.getWorkInfoByIdFlow(testId) } returns flowOf(mockWorkInfo)

        val result = helper.observeWorkInfo(testId).first()
        assertThat(result).isEqualTo(mockWorkInfo)
    }

    @Test
    fun `schedulePeriodicRepoSync enqueues unique periodic work`() {
        val uniqueNameSlot = slot<String>()
        val policySlot = slot<ExistingPeriodicWorkPolicy>()
        val requestSlot = slot<PeriodicWorkRequest>()

        every {
            mockWorkManager.enqueueUniquePeriodicWork(
                capture(uniqueNameSlot),
                capture(policySlot),
                capture(requestSlot)
            )
        } returns mockk(relaxed = true)

        helper.schedulePeriodicRepoSync()

        assertThat(uniqueNameSlot.captured).isEqualTo(RepoSyncWorker.UNIQUE_WORK_NAME)
        assertThat(policySlot.captured).isEqualTo(ExistingPeriodicWorkPolicy.KEEP)
        assertThat(requestSlot.captured).isNotNull()
    }

    @Test
    fun `schedulePeriodicAppUpdateCheck enqueues periodic work`() {
        val uniqueNameSlot = slot<String>()
        val policySlot = slot<ExistingPeriodicWorkPolicy>()
        val requestSlot = slot<PeriodicWorkRequest>()

        every {
            mockWorkManager.enqueueUniquePeriodicWork(
                capture(uniqueNameSlot),
                capture(policySlot),
                capture(requestSlot)
            )
        } returns mockk(relaxed = true)

        helper.schedulePeriodicAppUpdateCheck()

        assertThat(uniqueNameSlot.captured).isEqualTo("gitforge_periodic_app_update_check")
        assertThat(policySlot.captured).isEqualTo(ExistingPeriodicWorkPolicy.KEEP)
        assertThat(requestSlot.captured).isNotNull()
    }

    @Test
    fun `schedulePeriodicWatchedReleaseCheck enqueues periodic work`() {
        val uniqueNameSlot = slot<String>()
        val policySlot = slot<ExistingPeriodicWorkPolicy>()
        val requestSlot = slot<PeriodicWorkRequest>()

        every {
            mockWorkManager.enqueueUniquePeriodicWork(
                capture(uniqueNameSlot),
                capture(policySlot),
                capture(requestSlot)
            )
        } returns mockk(relaxed = true)

        helper.schedulePeriodicWatchedReleaseCheck()

        assertThat(uniqueNameSlot.captured).isEqualTo(WatchedReleaseCheckWorker.UNIQUE_WORK_NAME)
        assertThat(policySlot.captured).isEqualTo(ExistingPeriodicWorkPolicy.KEEP)
        assertThat(requestSlot.captured).isNotNull()
    }
}
