package io.gitforge.app.data.updater

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.Locale
import java.util.regex.Pattern
import javax.inject.Inject
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Singleton
class AppUpdateManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val okHttpClient: OkHttpClient
) {
    companion object {
        const val GITHUB_REPO_OWNER = "biZenX"
        const val GITHUB_REPO_NAME = "GitForge"
        const val UPDATE_PREFS = "gitforge_update_prefs"
        const val PREF_LAST_NOTIFIED_VERSION = "last_notified_version_code"
    }

    /**
     * Checks GitHub for the latest release and returns update info if a newer version exists.
     */
    suspend fun checkForUpdates(
        currentVersionName: String = "1.0.5",
        currentVersionCode: Int = 6
    ): Result<AppUpdateInfo?> = withContext(Dispatchers.IO) {
        try {
            val url = "https://api.github.com/repos/$GITHUB_REPO_OWNER/$GITHUB_REPO_NAME/releases/latest"
            val request = Request.Builder()
                .url(url)
                .header("Accept", "application/vnd.github.v3+json")
                .header("User-Agent", "GitForge-Android-Client")
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("GitHub API error: ${response.code}"))
            }

            val body = response.body?.string() ?: return@withContext Result.failure(Exception("Empty response"))
            val json = JSONObject(body)

            val rawTagName = json.optString("tag_name", "").trim()
            val remoteVersion = rawTagName.removePrefix("v").trim()
            val publishedAt = json.optString("published_at", "").take(10)
            val releaseNotes = json.optString("body", "")

            val remoteVersionCode = parseVersionToCode(remoteVersion)

            // Check if remote version is strictly newer
            val isNewer = isVersionNewer(remoteVersion, currentVersionName) || (remoteVersionCode > currentVersionCode)
            if (!isNewer) {
                return@withContext Result.success(null) // App is up to date
            }

            // Find APK asset
            var apkDownloadUrl = ""
            var apkSize = 0L
            val assets = json.optJSONArray("assets")
            if (assets != null) {
                for (i in 0 until assets.length()) {
                    val asset = assets.getJSONObject(i)
                    val name = asset.optString("name", "")
                    if (name.endsWith(".apk", ignoreCase = true)) {
                        apkDownloadUrl = asset.optString("browser_download_url", "")
                        apkSize = asset.optLong("size", 0L)
                        break
                    }
                }
            }

            if (apkDownloadUrl.isBlank()) {
                apkDownloadUrl = json.optString("html_url", "")
            }

            val (features, improvements) = parseReleaseNotes(releaseNotes)

            val updateInfo = AppUpdateInfo(
                versionName = remoteVersion,
                versionCode = remoteVersionCode,
                releaseDate = publishedAt,
                mainFeatures = features,
                improvements = improvements,
                downloadUrl = apkDownloadUrl,
                fileSize = apkSize
            )

            Result.success(updateInfo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Downloads the APK file and triggers the native Android package installer.
     */
    suspend fun downloadAndInstallApk(
        updateInfo: AppUpdateInfo,
        onProgress: (Int) -> Unit = {}
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val downloadUrl = updateInfo.downloadUrl
            val request = Request.Builder().url(downloadUrl).build()
            val response = okHttpClient.newCall(request).execute()

            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Download failed: HTTP ${response.code}"))
            }

            val body = response.body ?: return@withContext Result.failure(Exception("Empty download body"))
            val contentLength = body.contentLength()
            val inputStream: InputStream = body.byteStream()

            val updateDir = File(context.cacheDir, "updates")
            if (!updateDir.exists()) updateDir.mkdirs()

            val targetFile = File(updateDir, "GitForge-v${updateInfo.versionName}.apk")
            if (targetFile.exists()) targetFile.delete()

            val outputStream = FileOutputStream(targetFile)
            val buffer = ByteArray(8192)
            var bytesRead: Int
            var totalBytesRead = 0L

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
                totalBytesRead += bytesRead
                if (contentLength > 0) {
                    val progress = ((totalBytesRead * 100) / contentLength).toInt()
                    onProgress(progress)
                }
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()

            // Launch package installer
            installApk(targetFile)

            Result.success(targetFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Downloads any APK from a direct URL and triggers the native Android package installer.
     */
    suspend fun downloadAndInstallApkUrl(
        apkUrl: String,
        fileName: String,
        onProgress: (Int) -> Unit = {}
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(apkUrl).build()
            val response = okHttpClient.newCall(request).execute()

            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Download failed: HTTP ${response.code}"))
            }

            val body = response.body ?: return@withContext Result.failure(Exception("Empty download body"))
            val contentLength = body.contentLength()
            val inputStream: InputStream = body.byteStream()

            val updateDir = File(context.cacheDir, "updates")
            if (!updateDir.exists()) updateDir.mkdirs()

            val safeName = if (fileName.endsWith(".apk", ignoreCase = true)) fileName else "$fileName.apk"
            val targetFile = File(updateDir, safeName)
            if (targetFile.exists()) targetFile.delete()

            val outputStream = FileOutputStream(targetFile)
            val buffer = ByteArray(8192)
            var bytesRead: Int
            var totalBytesRead = 0L

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
                totalBytesRead += bytesRead
                if (contentLength > 0) {
                    val progress = ((totalBytesRead * 100) / contentLength).toInt()
                    onProgress(progress)
                }
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()

            // Launch package installer
            installApk(targetFile)

            Result.success(targetFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Launches Android Package Installer using FileProvider.
     */
    fun installApk(file: File) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!context.packageManager.canRequestPackageInstalls()) {
                    val permissionIntent = Intent(android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                        data = Uri.parse("package:${context.packageName}")
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(permissionIntent)
                    return
                }
            }

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(installIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isVersionNewer(remoteVersion: String, currentVersion: String): Boolean {
        val rParts = remoteVersion.split(".").mapNotNull { it.filter { char -> char.isDigit() }.toIntOrNull() }
        val cParts = currentVersion.split(".").mapNotNull { it.filter { char -> char.isDigit() }.toIntOrNull() }
        val maxLen = maxOf(rParts.size, cParts.size)

        for (i in 0 until maxLen) {
            val r = rParts.getOrElse(i) { 0 }
            val c = cParts.getOrElse(i) { 0 }
            if (r > c) return true
            if (r < c) return false
        }
        return false
    }

    private fun parseVersionToCode(version: String): Int {
        val parts = version.split(".").mapNotNull { it.filter { char -> char.isDigit() }.toIntOrNull() }
        return if (parts.size >= 3) {
            parts[0] * 10000 + parts[1] * 100 + parts[2]
        } else if (parts.size == 2) {
            parts[0] * 10000 + parts[1] * 100
        } else {
            parts.firstOrNull() ?: 1
        }
    }

    private fun parseReleaseNotes(notes: String): Pair<List<String>, List<String>> {
        val features = mutableListOf<String>()
        val improvements = mutableListOf<String>()

        var currentCategory: String? = null
        for (line in notes.lines()) {
            val trimmed = line.trim()
            if (trimmed.startsWith("#")) {
                val lower = trimmed.lowercase(Locale.ROOT)
                currentCategory = when {
                    lower.contains("feature") || lower.contains("مميزات") || lower.contains("ميزات") -> "FEATURE"
                    lower.contains("fix") || lower.contains("improve") || lower.contains("إصلاح") || lower.contains("تحسين") -> "IMPROVEMENT"
                    else -> currentCategory
                }
            } else if (trimmed.startsWith("- ") || trimmed.startsWith("* ") || trimmed.startsWith("• ")) {
                val text = trimmed.removePrefix("- ").removePrefix("* ").removePrefix("• ").trim()
                if (text.isNotBlank()) {
                    if (currentCategory == "FEATURE" || features.size < 3) {
                        features.add(text)
                    } else {
                        improvements.add(text)
                    }
                }
            }
        }

        return Pair(features.take(4), improvements.take(4))
    }
}
