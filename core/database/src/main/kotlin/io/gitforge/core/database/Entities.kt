package io.gitforge.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.gitforge.core.model.Repository
import io.gitforge.core.model.User
import io.gitforge.core.model.Workspace

@Entity(tableName = "repos")
data class RepoEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val fullName: String,
    val ownerLogin: String,
    val ownerAvatarUrl: String,
    val description: String?,
    val isPrivate: Boolean,
    val defaultBranch: String,
    val stargazersCount: Int,
    val forksCount: Int,
    val language: String?,
    val updatedAt: String?,
    val lastCached: Long = System.currentTimeMillis()
) {
    fun toDomain(): Repository = Repository(
        id = id,
        name = name,
        fullName = fullName,
        owner = User(login = ownerLogin, avatarUrl = ownerAvatarUrl),
        description = description,
        private = isPrivate,
        defaultBranch = defaultBranch,
        stargazersCount = stargazersCount,
        forksCount = forksCount,
        language = language,
        updatedAt = updatedAt
    )

    companion object {
        fun fromDomain(repo: Repository): RepoEntity = RepoEntity(
            id = repo.id,
            name = repo.name,
            fullName = repo.fullName,
            ownerLogin = repo.owner?.login ?: "",
            ownerAvatarUrl = repo.owner?.avatarUrl ?: "",
            description = repo.description,
            isPrivate = repo.private,
            defaultBranch = repo.defaultBranch,
            stargazersCount = repo.stargazersCount,
            forksCount = repo.forksCount,
            language = repo.language,
            updatedAt = repo.updatedAt
        )
    }
}

@Entity(tableName = "issues")
data class IssueEntity(
    @PrimaryKey val id: Long,
    val number: Int,
    val repoFullName: String,
    val title: String,
    val body: String?,
    val state: String,
    val authorLogin: String,
    val authorAvatarUrl: String,
    val commentsCount: Int,
    val createdAt: String,
    val updatedAt: String
)

@Entity(tableName = "workspaces")
data class WorkspaceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val path: String,
    val remoteUrl: String?,
    val currentBranch: String,
    val lastAccessed: Long,
    val isGitInitialized: Boolean
) {
    fun toDomain(): Workspace = Workspace(
        id = id,
        name = name,
        path = path,
        remoteUrl = remoteUrl,
        currentBranch = currentBranch,
        lastAccessed = lastAccessed,
        isGitInitialized = isGitInitialized
    )

    companion object {
        fun fromDomain(workspace: Workspace): WorkspaceEntity = WorkspaceEntity(
            id = workspace.id,
            name = workspace.name,
            path = workspace.path,
            remoteUrl = workspace.remoteUrl,
            currentBranch = workspace.currentBranch,
            lastAccessed = workspace.lastAccessed,
            isGitInitialized = workspace.isGitInitialized
        )
    }
}
