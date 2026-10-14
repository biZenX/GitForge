package io.gitforge.app.data

import io.gitforge.core.database.IssueDao
import io.gitforge.core.database.RepoDao
import io.gitforge.core.database.RepoEntity
import io.gitforge.core.model.*
import io.gitforge.core.network.GitHubApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Base64
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitHubRepository @Inject constructor(
    private val apiService: GitHubApiService,
    private val repoDao: RepoDao,
    private val issueDao: IssueDao
) {

    suspend fun getAuthenticatedUser(): Result<User> = withContext(Dispatchers.IO) {
        runCatching { apiService.getAuthenticatedUser() }
    }

    fun getCachedRepositories(): Flow<List<Repository>> {
        return repoDao.getCachedRepos().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getRepositories(forceRefresh: Boolean = false): Result<List<Repository>> = withContext(Dispatchers.IO) {
        runCatching {
            val remoteRepos = apiService.getAuthenticatedUserRepositories()
            repoDao.insertRepos(remoteRepos.map { RepoEntity.fromDomain(it) })
            remoteRepos
        }
    }

    suspend fun searchRepositories(query: String, page: Int = 1): Result<List<Repository>> = withContext(Dispatchers.IO) {
        runCatching {
            apiService.searchRepositories(query = query, page = page).items
        }
    }

    suspend fun getRepository(owner: String, repo: String): Result<Repository> = withContext(Dispatchers.IO) {
        runCatching {
            val remote = apiService.getRepository(owner, repo)
            repoDao.insertRepos(listOf(RepoEntity.fromDomain(remote)))
            remote
        }
    }

    suspend fun getBranches(owner: String, repo: String): Result<List<Branch>> = withContext(Dispatchers.IO) {
        runCatching { apiService.getBranches(owner, repo) }
    }

    suspend fun getCommits(owner: String, repo: String, branch: String? = null, page: Int = 1): Result<List<CommitInfo>> = withContext(Dispatchers.IO) {
        runCatching { apiService.getCommits(owner, repo, branch, page) }
    }

    suspend fun getContents(owner: String, repo: String, path: String = "", ref: String? = null): Result<List<FileNode>> = withContext(Dispatchers.IO) {
        runCatching { apiService.getContents(owner, repo, path, ref) }
    }

    suspend fun getFileContent(owner: String, repo: String, path: String, ref: String? = null): Result<RemoteFileContent> = withContext(Dispatchers.IO) {
        runCatching { apiService.getFileContent(owner, repo, path, ref) }
    }

    suspend fun getReadme(owner: String, repo: String, ref: String? = null): Result<RemoteFileContent> = withContext(Dispatchers.IO) {
        runCatching { apiService.getReadme(owner, repo, ref) }
    }

    suspend fun createOrUpdateFile(
        owner: String,
        repo: String,
        path: String,
        message: String,
        plainContent: String,
        sha: String? = null,
        branch: String? = null
    ): Result<FileCommitResponse> = withContext(Dispatchers.IO) {
        runCatching {
            val encodedContent = Base64.getEncoder().encodeToString(plainContent.toByteArray(Charsets.UTF_8))
            val req = CreateOrUpdateFileRequest(
                message = message,
                content = encodedContent,
                sha = sha,
                branch = branch
            )
            apiService.createOrUpdateFile(owner, repo, path, req)
        }
    }

    suspend fun deleteFile(
        owner: String,
        repo: String,
        path: String,
        message: String,
        sha: String,
        branch: String? = null
    ): Result<FileCommitResponse> = withContext(Dispatchers.IO) {
        runCatching {
            val req = DeleteFileRequest(
                message = message,
                sha = sha,
                branch = branch
            )
            apiService.deleteFile(owner, repo, path, req)
        }
    }

    suspend fun getIssues(owner: String, repo: String, state: String = "open", page: Int = 1): Result<List<Issue>> = withContext(Dispatchers.IO) {
        runCatching { apiService.getIssues(owner, repo, state, page) }
    }

    suspend fun createIssue(owner: String, repo: String, title: String, body: String? = null): Result<Issue> = withContext(Dispatchers.IO) {
        runCatching {
            apiService.createIssue(owner, repo, CreateIssueRequest(title = title, body = body))
        }
    }

    suspend fun getIssueComments(owner: String, repo: String, issueNumber: Int): Result<List<IssueComment>> = withContext(Dispatchers.IO) {
        runCatching { apiService.getIssueComments(owner, repo, issueNumber) }
    }

    suspend fun addIssueComment(owner: String, repo: String, issueNumber: Int, body: String): Result<IssueComment> = withContext(Dispatchers.IO) {
        runCatching { apiService.addIssueComment(owner, repo, issueNumber, CreateCommentRequest(body = body)) }
    }

    suspend fun getPullRequests(owner: String, repo: String, state: String = "open", page: Int = 1): Result<List<PullRequest>> = withContext(Dispatchers.IO) {
        runCatching { apiService.getPullRequests(owner, repo, state, page) }
    }

    suspend fun getPullRequest(owner: String, repo: String, pullNumber: Int): Result<PullRequest> = withContext(Dispatchers.IO) {
        runCatching { apiService.getPullRequest(owner, repo, pullNumber) }
    }

    suspend fun getPullRequestFiles(owner: String, repo: String, pullNumber: Int): Result<List<PullRequestFile>> = withContext(Dispatchers.IO) {
        runCatching { apiService.getPullRequestFiles(owner, repo, pullNumber) }
    }

    suspend fun mergePullRequest(
        owner: String,
        repo: String,
        pullNumber: Int,
        title: String? = null,
        message: String? = null,
        method: String = "merge"
    ): Result<MergeResult> = withContext(Dispatchers.IO) {
        runCatching {
            val req = MergePullRequestRequest(commitTitle = title, commitMessage = message, mergeMethod = method)
            apiService.mergePullRequest(owner, repo, pullNumber, req)
        }
    }

    suspend fun getWorkflows(owner: String, repo: String): Result<List<Workflow>> = withContext(Dispatchers.IO) {
        runCatching { apiService.getWorkflows(owner, repo).workflows }
    }

    suspend fun getWorkflowRuns(owner: String, repo: String, page: Int = 1): Result<List<WorkflowRun>> = withContext(Dispatchers.IO) {
        runCatching { apiService.getWorkflowRuns(owner, repo, page).workflowRuns }
    }

    suspend fun getNotifications(all: Boolean = false, page: Int = 1): Result<List<Notification>> = withContext(Dispatchers.IO) {
        runCatching { apiService.getNotifications(all, page) }
    }

    suspend fun getReleases(owner: String, repo: String, page: Int = 1): Result<List<Release>> = withContext(Dispatchers.IO) {
        runCatching { apiService.getReleases(owner, repo, page) }
    }
}
