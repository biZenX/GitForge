package io.gitforge.feature.git

import android.content.Context
import io.gitforge.core.model.LocalFile
import io.gitforge.core.model.Workspace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

class WorkspaceStorageManager(private val customRootDir: File? = null) {

    constructor(context: Context) : this(File(context.filesDir, "workspaces"))

    val workspacesRoot: File
        get() = (customRootDir ?: File(System.getProperty("java.io.tmpdir"), "workspaces")).apply { if (!exists()) mkdirs() }

    suspend fun createWorkspace(name: String, remoteUrl: String? = null): Result<Workspace> = withContext(Dispatchers.IO) {
        runCatching {
            val safeName = name.trim().replace(Regex("[^a-zA-Z0-9._-]"), "_")
            val id = UUID.randomUUID().toString().take(8) + "_" + safeName
            val workspaceDir = File(workspacesRoot, id)
            if (!workspaceDir.exists()) workspaceDir.mkdirs()

            Workspace(
                id = id,
                name = name,
                path = workspaceDir.absolutePath,
                remoteUrl = remoteUrl,
                isGitInitialized = false
            )
        }
    }

    suspend fun listFiles(workspaceDir: File, relativeSubPath: String = ""): Result<List<LocalFile>> = withContext(Dispatchers.IO) {
        runCatching {
            validatePathWithinWorkspace(workspaceDir, relativeSubPath)
            val targetDir = if (relativeSubPath.isBlank()) workspaceDir else File(workspaceDir, relativeSubPath)
            if (!targetDir.exists() || !targetDir.isDirectory) {
                return@runCatching emptyList()
            }

            val files = targetDir.listFiles() ?: emptyArray()
            files.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() })).map { file ->
                val relPath = file.relativeTo(workspaceDir).path
                LocalFile(
                    name = file.name,
                    path = relPath,
                    isDirectory = file.isDirectory,
                    size = if (file.isDirectory) 0L else file.length(),
                    lastModified = file.lastModified(),
                    extension = file.extension
                )
            }
        }
    }

    suspend fun readFileContent(workspaceDir: File, relativePath: String): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            validatePathWithinWorkspace(workspaceDir, relativePath)
            val file = File(workspaceDir, relativePath)
            if (!file.exists()) throw NoSuchFileException(file, reason = "File not found")
            file.readText(Charsets.UTF_8)
        }
    }

    suspend fun saveFileContent(workspaceDir: File, relativePath: String, content: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            validatePathWithinWorkspace(workspaceDir, relativePath)
            val file = File(workspaceDir, relativePath)
            file.parentFile?.let { if (!it.exists()) it.mkdirs() }
            file.writeText(content, Charsets.UTF_8)
        }
    }

    suspend fun deleteFile(workspaceDir: File, relativePath: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            validatePathWithinWorkspace(workspaceDir, relativePath)
            val file = File(workspaceDir, relativePath)
            if (file.exists()) {
                if (file.isDirectory) file.deleteRecursively() else file.delete()
            }
        }
    }

    suspend fun importStreamToFile(workspaceDir: File, relativePath: String, inputStream: InputStream): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            validatePathWithinWorkspace(workspaceDir, relativePath)
            val file = File(workspaceDir, relativePath)
            file.parentFile?.let { if (!it.exists()) it.mkdirs() }
            file.outputStream().use { out ->
                inputStream.use { inp -> inp.copyTo(out) }
            }
            Unit
        }
    }

    suspend fun exportFileToStream(workspaceDir: File, relativePath: String, outputStream: OutputStream): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            validatePathWithinWorkspace(workspaceDir, relativePath)
            val file = File(workspaceDir, relativePath)
            if (!file.exists()) throw NoSuchFileException(file)
            file.inputStream().use { inp ->
                outputStream.use { out -> inp.copyTo(out) }
            }
            Unit
        }
    }

    fun validatePathWithinWorkspace(workspaceDir: File, relativePath: String) {
        val target = File(workspaceDir, relativePath)
        val canonicalWorkspace = workspaceDir.canonicalFile
        val canonicalTarget = target.canonicalFile
        if (!canonicalTarget.path.startsWith(canonicalWorkspace.path)) {
            throw SecurityException("Path traversal attempt detected: '$relativePath' escapes workspace boundary.")
        }
    }
}
