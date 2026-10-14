package io.gitforge.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Workspace(
    val id: String,
    val name: String,
    val path: String,
    val remoteUrl: String? = null,
    val currentBranch: String = "main",
    val lastAccessed: Long = System.currentTimeMillis(),
    val isGitInitialized: Boolean = false
)

@Serializable
data class LocalFile(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val size: Long = 0L,
    val lastModified: Long = 0L,
    val extension: String = ""
)

@Serializable
data class GitStatusResult(
    val branch: String = "main",
    val added: List<String> = emptyList(),
    val changed: List<String> = emptyList(),
    val removed: List<String> = emptyList(),
    val missing: List<String> = emptyList(),
    val modified: List<String> = emptyList(),
    val untracked: List<String> = emptyList(),
    val conflicting: List<String> = emptyList(),
    val isClean: Boolean = true
)

@Serializable
data class GitCommitResult(
    val commitId: String,
    val shortMessage: String,
    val author: String
)

@Serializable
data class CommandResult(
    val command: String,
    val stdout: String,
    val stderr: String,
    val exitCode: Int,
    val durationMs: Long,
    val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class AuthToken(
    val token: String,
    val type: String = "Bearer",
    val scope: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class SearchResponse<T>(
    val total_count: Int = 0,
    val incomplete_results: Boolean = false,
    val items: List<T> = emptyList()
)
