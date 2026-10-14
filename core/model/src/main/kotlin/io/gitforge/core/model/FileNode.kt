package io.gitforge.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class FileNodeType {
    FILE,
    DIR,
    SUBMODULE,
    SYMLINK
}

@Serializable
data class FileNode(
    val name: String,
    val path: String,
    val sha: String = "",
    val size: Long = 0L,
    val url: String = "",
    @SerialName("html_url")
    val htmlUrl: String? = null,
    @SerialName("download_url")
    val downloadUrl: String? = null,
    val type: String = "file"
) {
    val nodeType: FileNodeType
        get() = when (type.lowercase()) {
            "dir" -> FileNodeType.DIR
            "submodule" -> FileNodeType.SUBMODULE
            "symlink" -> FileNodeType.SYMLINK
            else -> FileNodeType.FILE
        }
}

@Serializable
data class RemoteFileContent(
    val name: String,
    val path: String,
    val sha: String,
    val size: Long = 0L,
    val encoding: String = "base64",
    val content: String = "",
    @SerialName("download_url")
    val downloadUrl: String? = null
)

@Serializable
data class CreateOrUpdateFileRequest(
    val message: String,
    val content: String, // base64-encoded
    val sha: String? = null,
    val branch: String? = null
)

@Serializable
data class DeleteFileRequest(
    val message: String,
    val sha: String,
    val branch: String? = null
)

@Serializable
data class FileCommitResponse(
    val content: FileNode? = null,
    val commit: CommitSummary? = null
)

@Serializable
data class CommitSummary(
    val sha: String,
    val message: String
)
