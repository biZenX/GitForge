package io.gitforge.app.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.gitforge.core.database.GitForgeDatabase
import io.gitforge.core.database.RepoEntity
import io.gitforge.core.network.NetworkClientFactory
import io.gitforge.core.security.EncryptedTokenStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.room.Room

class RepoSyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val tokenStorage = EncryptedTokenStorage(applicationContext)
            if (tokenStorage.getToken().isNullOrBlank()) {
                return@withContext Result.success() // Nothing to sync for guest
            }

            val okHttpClient = NetworkClientFactory.createOkHttpClient(tokenStorage)
            val apiService = NetworkClientFactory.createGitHubApiService(okHttpClient)

            val database = Room.databaseBuilder(
                applicationContext,
                GitForgeDatabase::class.java,
                "gitforge_local.db"
            ).fallbackToDestructiveMigration(true).build()

            val repos = apiService.getAuthenticatedUserRepositories(page = 1, perPage = 30)
            database.repoDao().insertRepos(repos.map { RepoEntity.fromDomain(it) })

            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    companion object {
        const val UNIQUE_WORK_NAME = "gitforge_repo_sync_periodic"
    }
}
