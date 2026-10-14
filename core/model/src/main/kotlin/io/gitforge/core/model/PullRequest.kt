package io.gitforge.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitRef(
    val label: String = "",
    val ref: String = "",
    val sha: String = "",
    val user: User? = null,
    val repo: Repository? = null
)

@Serializable
data class PullRequest(
    val id: Long = 0L,
    val number: Int,
    val title: String,
    val body: String? = null,
    val state: String = "open",
    val draft: Boolean = false,
    val merged: Boolean = false,
    val user: User? = null,
    val head: GitRef = GitRef(),
    val base: GitRef = GitRef(),
    val additions: Int = 0,
    val deletions: Int = 0,
    @SerialName("changed_files")
    val changedFiles: Int = 0,
    @SerialName("created_at")
    val createdAt: String = "",
    @SerialName("updated_at")
    val updatedAt: String = "",
    @SerialName("closed_at")
    val closedAt: String? = null,
    @SerialName("merged_at")
    val mergedAt: String? = null,
    @SerialName("html_url")
    val htmlUrl: String = "",
    @SerialName("diff_url")
    val diffUrl: String = ""
)

@Serializable
data class PullRequestFile(
    val sha: String = "",
    val filename: String,
    val status: String = "modified",
    val additions: Int = 0,
    val deletions: Int = 0,
    val changes: Int = 0,
    val patch: String? = null
)

@Serializable
data class MergePullRequestRequest(
    @SerialName("commit_title")
    val commitTitle: String? = null,
    @SerialName("commit_message")
    val commitMessage: String? = null,
    @SerialName("merge_method")
    val mergeMethod: String = "merge" // merge, squash, or rebase
)

@Serializable
data class MergeResult(
    val sha: String = "",
    val merged: Boolean = false,
    val message: String = ""
)
