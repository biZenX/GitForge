package io.gitforge.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepoPermissions(
    val admin: Boolean = false,
    val maintain: Boolean = false,
    val push: Boolean = false,
    val triage: Boolean = false,
    val pull: Boolean = true
)

@Serializable
data class Repository(
    val id: Long = 0L,
    val name: String,
    @SerialName("full_name")
    val fullName: String,
    val owner: User? = null,
    val description: String? = null,
    val private: Boolean = false,
    val fork: Boolean = false,
    @SerialName("html_url")
    val htmlUrl: String = "",
    @SerialName("default_branch")
    val defaultBranch: String = "main",
    @SerialName("stargazers_count")
    val stargazersCount: Int = 0,
    @SerialName("forks_count")
    val forksCount: Int = 0,
    @SerialName("open_issues_count")
    val openIssuesCount: Int = 0,
    val language: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null,
    val size: Long = 0L,
    val permissions: RepoPermissions = RepoPermissions()
)

@Serializable
data class Branch(
    val name: String,
    val commit: BranchCommit? = null,
    val `protected`: Boolean = false
)

@Serializable
data class BranchCommit(
    val sha: String,
    val url: String = ""
)

@Serializable
data class CommitInfo(
    val sha: String,
    val commit: CommitDetail,
    val author: User? = null,
    val committer: User? = null,
    @SerialName("html_url")
    val htmlUrl: String = ""
)

@Serializable
data class CommitDetail(
    val message: String,
    val author: CommitAuthor? = null,
    val committer: CommitAuthor? = null
)

@Serializable
data class CommitAuthor(
    val name: String,
    val email: String,
    val date: String
)
