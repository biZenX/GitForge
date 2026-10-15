package io.gitforge.app.data.updater

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.gitforge.app.MainActivity
import io.gitforge.core.network.NetworkClientFactory
import io.gitforge.core.security.EncryptedTokenStorage

class AppUpdateCheckWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val updateManager by lazy {
        val tokenStorage = EncryptedTokenStorage(context)
        val okHttpClient = NetworkClientFactory.createOkHttpClient(tokenStorage)
        AppUpdateManager(context, okHttpClient)
    }

    companion object {
        const val CHANNEL_ID = "gitforge_updates"
        const val NOTIFICATION_ID = 4040
    }

    override suspend fun doWork(): Result {
        val updateResult = updateManager.checkForUpdates()
        val updateInfo = updateResult.getOrNull()

        if (updateInfo != null) {
            val prefs = context.getSharedPreferences(AppUpdateManager.UPDATE_PREFS, Context.MODE_PRIVATE)
            val lastNotified = prefs.getInt(AppUpdateManager.PREF_LAST_NOTIFIED_VERSION, 0)

            if (updateInfo.versionCode > lastNotified) {
                showUpdateNotification(updateInfo)
                prefs.edit().putInt(AppUpdateManager.PREF_LAST_NOTIFIED_VERSION, updateInfo.versionCode).apply()
            }
        }

        return Result.success()
    }

    private fun showUpdateNotification(info: AppUpdateInfo) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "App Updates",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications when new GitForge releases are published"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("show_update_dialog", true)
            putExtra("update_version", info.versionName)
            putExtra("update_url", info.downloadUrl)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle("🚀 GitForge Update Available (v${info.versionName})")
            .setContentText("A new release is ready for installation. Tap to update.")
            .setStyle(NotificationCompat.BigTextStyle().bigText(
                "Version v${info.versionName} is now available!\n" +
                info.mainFeatures.joinToString("\n") { "• $it" }
            ))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
