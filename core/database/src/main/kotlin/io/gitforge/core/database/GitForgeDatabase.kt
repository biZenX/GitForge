package io.gitforge.core.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RepoDao {
    @Query("SELECT * FROM repos ORDER BY lastCached DESC")
    fun getCachedRepos(): Flow<List<RepoEntity>>

    @Query("SELECT * FROM repos WHERE fullName = :fullName LIMIT 1")
    suspend fun getRepoByFullName(fullName: String): RepoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepos(repos: List<RepoEntity>)

    @Query("DELETE FROM repos")
    suspend fun clearRepos()
}

@Dao
interface IssueDao {
    @Query("SELECT * FROM issues WHERE repoFullName = :repoFullName ORDER BY number DESC")
    fun getIssuesForRepo(repoFullName: String): Flow<List<IssueEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIssues(issues: List<IssueEntity>)

    @Query("DELETE FROM issues WHERE repoFullName = :repoFullName")
    suspend fun clearIssuesForRepo(repoFullName: String)
}

@Dao
interface WorkspaceDao {
    @Query("SELECT * FROM workspaces ORDER BY lastAccessed DESC")
    fun getAllWorkspaces(): Flow<List<WorkspaceEntity>>

    @Query("SELECT * FROM workspaces WHERE id = :id LIMIT 1")
    suspend fun getWorkspaceById(id: String): WorkspaceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkspace(workspace: WorkspaceEntity)

    @Delete
    suspend fun deleteWorkspace(workspace: WorkspaceEntity)

    @Query("DELETE FROM workspaces WHERE id = :id")
    suspend fun deleteWorkspaceById(id: String)
}

@Dao
interface WatchedReleaseDao {
    @Query("SELECT * FROM watched_releases ORDER BY lastChecked DESC")
    fun getAllWatched(): Flow<List<WatchedReleaseEntity>>

    @Query("SELECT * FROM watched_releases WHERE repoFullName = :repoFullName LIMIT 1")
    fun isWatchedFlow(repoFullName: String): Flow<WatchedReleaseEntity?>

    @Query("SELECT * FROM watched_releases WHERE repoFullName = :repoFullName LIMIT 1")
    suspend fun getWatched(repoFullName: String): WatchedReleaseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatched(watched: WatchedReleaseEntity)

    @Query("DELETE FROM watched_releases WHERE repoFullName = :repoFullName")
    suspend fun unwatch(repoFullName: String)

    @Query("UPDATE watched_releases SET lastKnownTag = :tag, latestReleaseName = :name, latestReleaseUrl = :url, latestReleaseBody = :body, lastChecked = :time WHERE repoFullName = :repoFullName")
    suspend fun updateReleaseInfo(repoFullName: String, tag: String, name: String?, url: String?, body: String?, time: Long = System.currentTimeMillis())
}

@Database(
    entities = [RepoEntity::class, IssueEntity::class, WorkspaceEntity::class, WatchedReleaseEntity::class],
    version = 2,
    exportSchema = false
)
abstract class GitForgeDatabase : RoomDatabase() {
    abstract fun repoDao(): RepoDao
    abstract fun issueDao(): IssueDao
    abstract fun workspaceDao(): WorkspaceDao
    abstract fun watchedReleaseDao(): WatchedReleaseDao
}

