package io.gitforge.app.work

import androidx.work.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkManagerHelper @Inject constructor(
    private val workManager: WorkManager
) {

    fun enqueueArchiveDownload(owner: String, repo: String, ref: String): UUID {
        val inputData = workDataOf(
            RepoArchiveDownloadWorker.KEY_OWNER to owner,
            RepoArchiveDownloadWorker.KEY_REPO to repo,
            RepoArchiveDownloadWorker.KEY_REF to ref
        )

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequestBuilder<RepoArchiveDownloadWorker>()
            .setInputData(inputData)
            .setConstraints(constraints)
            .addTag("archive_download_${owner}_${repo}")
            .build()

        workManager.enqueueUniqueWork(
            "download_${owner}_${repo}_${ref}",
            ExistingWorkPolicy.REPLACE,
            request
        )

        return request.id
    }

    fun observeWorkInfo(workId: UUID): Flow<WorkInfo?> {
        return workManager.getWorkInfoByIdFlow(workId)
    }

    fun cancelWork(workId: UUID) {
        workManager.cancelWorkById(workId)
    }

    fun schedulePeriodicRepoSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val periodicSyncRequest = PeriodicWorkRequestBuilder<RepoSyncWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            RepoSyncWorker.UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicSyncRequest
        )
    }

    fun schedulePeriodicAppUpdateCheck() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val updateRequest = PeriodicWorkRequestBuilder<io.gitforge.app.data.updater.AppUpdateCheckWorker>(12, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "gitforge_periodic_app_update_check",
            ExistingPeriodicWorkPolicy.KEEP,
            updateRequest
        )
    }

    fun schedulePeriodicWatchedReleaseCheck() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val watchedRequest = PeriodicWorkRequestBuilder<WatchedReleaseCheckWorker>(6, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            WatchedReleaseCheckWorker.UNIQUE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            watchedRequest
        )
    }
}
