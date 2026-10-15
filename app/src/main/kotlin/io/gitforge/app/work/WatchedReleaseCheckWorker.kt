package io.gitforge.app.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.gitforge.app.MainActivity
import io.gitforge.core.database.GitForgeDatabase
import io.gitforge.core.database.WatchedReleaseDao
import io.gitforge.core.network.GitHubApiService
import io.gitforge.core.network.NetworkClientFactory
import io.gitforge.core.security.EncryptedTokenStorage
import kotlinx.coroutines.flow.firstOrNull

class WatchedReleaseCheckWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val database by lazy {
        Room.databaseBuilder(
            context,
            GitForgeDatabase::class.java,
            "gitforge_local.db"
        ).fallbackToDestructiveMigration(true).build()
    }

    private val watchedReleaseDao by lazy { database.watchedReleaseDao() }

    private val apiService by lazy {
        val tokenStorage = EncryptedTokenStorage(context)
        val okHttpClient = NetworkClientFactory.createOkHttpClient(tokenStorage)
        NetworkClientFactory.createGitHubApiService(okHttpClient)
    }

    companion object {
        const val CHANNEL_ID = "gitforge_watched_releases"
        const val UNIQUE_WORK_NAME = "watched_releases_check_work"
    }

    override suspend fun doWork(): Result {
        return try {
            val watchedList = watchedReleaseDao.getAllWatched().firstOrNull() ?: emptyList()
            for (watched in watchedList) {
                if (!watched.isNotifyEnabled) continue

                try {
                    val releases = apiService.getReleases(
                        owner = watched.ownerLogin,
                        repo = watched.repoName,
                        page = 1,
                        perPage = 1
                    )
                    val latest = releases.firstOrNull() ?: continue

                    if (watched.lastKnownTag == null) {
                        // First time seeing this repo, record baseline
                        watchedReleaseDao.updateReleaseInfo(
                            repoFullName = watched.repoFullName,
                            tag = latest.tagName,
                            name = latest.name,
                            url = latest.htmlUrl,
                            body = latest.body
                        )
                    } else if (watched.lastKnownTag != latest.tagName) {
                        // New release published!
                        showNewReleaseNotification(
                            repoFullName = watched.repoFullName,
                            repoName = watched.repoName,
                            owner = watched.ownerLogin,
                            tagName = latest.tagName,
                            releaseTitle = latest.name ?: latest.tagName
                        )
                        watchedReleaseDao.updateReleaseInfo(
                            repoFullName = watched.repoFullName,
                            tag = latest.tagName,
                            name = latest.name,
                            url = latest.htmlUrl,
                            body = latest.body
                        )
                    }
                } catch (e: Exception) {
                    // Skip single repo errors to check remainder
                    continue
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun showNewReleaseNotification(
        repoFullName: String,
        repoName: String,
        owner: String,
        tagName: String,
        releaseTitle: String
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Watched Repository Releases",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications when repositories you follow publish new releases"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigate_to_repo", true)
            putExtra("repo_owner", owner)
            putExtra("repo_name", repoName)
            putExtra("selected_tab", 3) // Releases tab
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            repoFullName.hashCode(),
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_sync)
            .setContentTitle("🚀 New Release: $repoName ($tagName)")
            .setContentText(releaseTitle)
            .setStyle(NotificationCompat.BigTextStyle().bigText("A new release ($tagName) was published for $repoFullName:\n$releaseTitle\nTap to inspect and download assets."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(repoFullName.hashCode(), notification)
    }
}
