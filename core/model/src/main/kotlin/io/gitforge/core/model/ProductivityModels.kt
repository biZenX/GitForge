package io.gitforge.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Workflow(
    val id: Long = 0L,
    val name: String,
    val path: String = "",
    val state: String = "active"
)

@Serializable
data class WorkflowResponse(
    @SerialName("total_count")
    val totalCount: Int = 0,
    val workflows: List<Workflow> = emptyList()
)

@Serializable
data class WorkflowRun(
    val id: Long = 0L,
    val name: String = "",
    @SerialName("head_branch")
    val headBranch: String? = null,
    @SerialName("head_sha")
    val headSha: String = "",
    val status: String = "queued", // queued, in_progress, completed
    val conclusion: String? = null, // success, failure, cancelled, neutral
    @SerialName("workflow_id")
    val workflowId: Long = 0L,
    @SerialName("run_number")
    val runNumber: Int = 0,
    val event: String = "",
    @SerialName("html_url")
    val htmlUrl: String = "",
    @SerialName("created_at")
    val createdAt: String = "",
    @SerialName("updated_at")
    val updatedAt: String = ""
)

@Serializable
data class WorkflowRunsResponse(
    @SerialName("total_count")
    val totalCount: Int = 0,
    @SerialName("workflow_runs")
    val workflowRuns: List<WorkflowRun> = emptyList()
)

@Serializable
data class NotificationSubject(
    val title: String,
    val url: String? = null,
    @SerialName("latest_comment_url")
    val latestCommentUrl: String? = null,
    val type: String = "Issue"
)

@Serializable
data class Notification(
    val id: String,
    val unread: Boolean = true,
    val reason: String = "",
    @SerialName("updated_at")
    val updatedAt: String = "",
    val subject: NotificationSubject,
    val repository: Repository
)

@Serializable
data class Release(
    val id: Long = 0L,
    @SerialName("tag_name")
    val tagName: String,
    val name: String? = null,
    val body: String? = null,
    val draft: Boolean = false,
    val prerelease: Boolean = false,
    @SerialName("created_at")
    val createdAt: String = "",
    @SerialName("published_at")
    val publishedAt: String? = null,
    @SerialName("html_url")
    val htmlUrl: String = "",
    val author: User? = null
)
