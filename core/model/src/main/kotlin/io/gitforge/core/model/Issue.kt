package io.gitforge.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Label(
    val id: Long = 0L,
    val name: String,
    val color: String = "888888",
    val description: String? = null
)

@Serializable
data class Issue(
    val id: Long = 0L,
    val number: Int,
    val title: String,
    val body: String? = null,
    val state: String = "open",
    val user: User? = null,
    val labels: List<Label> = emptyList(),
    val assignees: List<User> = emptyList(),
    val comments: Int = 0,
    @SerialName("created_at")
    val createdAt: String = "",
    @SerialName("updated_at")
    val updatedAt: String = "",
    @SerialName("closed_at")
    val closedAt: String? = null,
    @SerialName("html_url")
    val htmlUrl: String = "",
    @SerialName("pull_request")
    val pullRequestMarker: PullRequestMarker? = null
) {
    val isPullRequest: Boolean
        get() = pullRequestMarker != null
}

@Serializable
data class PullRequestMarker(
    val url: String = ""
)

@Serializable
data class IssueComment(
    val id: Long = 0L,
    val body: String,
    val user: User? = null,
    @SerialName("created_at")
    val createdAt: String = "",
    @SerialName("updated_at")
    val updatedAt: String = ""
)

@Serializable
data class CreateCommentRequest(
    val body: String
)

@Serializable
data class CreateIssueRequest(
    val title: String,
    val body: String? = null,
    val labels: List<String> = emptyList(),
    val assignees: List<String> = emptyList()
)
