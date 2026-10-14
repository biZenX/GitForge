package io.gitforge.app.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import io.gitforge.core.network.NetworkClientFactory
import io.gitforge.core.security.EncryptedTokenStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class RepoArchiveDownloadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val owner = inputData.getString(KEY_OWNER) ?: return@withContext Result.failure()
        val repo = inputData.getString(KEY_REPO) ?: return@withContext Result.failure()
        val ref = inputData.getString(KEY_REF) ?: "main"

        createNotificationChannel()

        val notificationId = (owner + repo + ref).hashCode()
        val initialNotification = createNotification(
            title = "Downloading $repo ($ref)",
            progress = 0,
            indeterminate = true
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                setForeground(ForegroundInfo(notificationId, initialNotification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC))
            } else {
                setForeground(ForegroundInfo(notificationId, initialNotification))
            }
        } catch (e: Exception) {
            // Foreground not strictly required if run in background
        }

        try {
            val tokenStorage = EncryptedTokenStorage(applicationContext)
            val okHttpClient = NetworkClientFactory.createOkHttpClient(tokenStorage)
            val apiService = NetworkClientFactory.createGitHubApiService(okHttpClient)

            val downloadsDir = File(applicationContext.filesDir, "downloads").apply { if (!exists()) mkdirs() }
            val destinationFile = File(downloadsDir, "$owner-$repo-$ref.zip")

            val responseBody = apiService.downloadArchive(owner, repo, ref)
            val totalBytes = responseBody.contentLength()
            var downloadedBytes = 0L

            responseBody.byteStream().use { input ->
                FileOutputStream(destinationFile).use { output ->
                    val buffer = ByteArray(8 * 1024)
                    var read: Int
                    var lastReportedPercent = 0

                    while (input.read(buffer).also { read = it } != -1) {
                        if (isStopped) {
                            destinationFile.delete()
                            return@withContext Result.failure()
                        }

                        output.write(buffer, 0, read)
                        downloadedBytes += read

                        if (totalBytes > 0) {
                            val progress = ((downloadedBytes * 100) / totalBytes).toInt()
                            if (progress > lastReportedPercent + 4 || progress == 100) {
                                lastReportedPercent = progress
                                setProgress(workDataOf(KEY_PROGRESS to progress))
                                updateNotification(notificationId, "Downloading $repo", progress, false)
                            }
                        }
                    }
                }
            }

            // Completed
            showCompletionNotification(notificationId, "Download complete", "$repo archive saved (${destinationFile.length() / 1024} KB)")

            Result.success(
                workDataOf(
                    KEY_OUTPUT_PATH to destinationFile.absolutePath,
                    KEY_OUTPUT_SIZE to destinationFile.length()
                )
            )
        } catch (e: Exception) {
            showCompletionNotification(notificationId, "Download failed", e.message ?: "Failed to download $repo archive")
            Result.failure(workDataOf("error" to (e.message ?: "Download failed")))
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "GitForge Downloads",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows progress of repository archive downloads"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(title: String, progress: Int, indeterminate: Boolean) =
        NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(if (indeterminate) "Starting download..." else "$progress%")
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setProgress(100, progress, indeterminate)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()

    private fun updateNotification(notificationId: Int, title: String, progress: Int, indeterminate: Boolean) {
        val notification = createNotification(title, progress, indeterminate)
        notificationManager.notify(notificationId, notification)
    }

    private fun showCompletionNotification(notificationId: Int, title: String, text: String) {
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(notificationId, notification)
    }

    companion object {
        const val CHANNEL_ID = "gitforge_downloads_channel"
        const val KEY_OWNER = "owner"
        const val KEY_REPO = "repo"
        const val KEY_REF = "ref"
        const val KEY_PROGRESS = "progress"
        const val KEY_OUTPUT_PATH = "output_path"
        const val KEY_OUTPUT_SIZE = "output_size"
    }
}
