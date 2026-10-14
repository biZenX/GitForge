package io.gitforge.core.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Long = 0L,
    val login: String,
    val name: String? = null,
    @SerialName("avatar_url")
    val avatarUrl: String = "",
    val bio: String? = null,
    @SerialName("public_repos")
    val publicRepos: Int = 0,
    @SerialName("total_private_repos")
    val totalPrivateRepos: Int = 0,
    val followers: Int = 0,
    val following: Int = 0,
    val email: String? = null,
    @SerialName("html_url")
    val htmlUrl: String = "",
    val company: String? = null,
    val location: String? = null
)
