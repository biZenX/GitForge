package io.gitforge.app.data

import com.google.common.truth.Truth.assertThat
import io.gitforge.core.database.IssueDao
import io.gitforge.core.database.IssueEntity
import io.gitforge.core.database.RepoDao
import io.gitforge.core.database.RepoEntity
import io.gitforge.core.model.*
import io.gitforge.core.network.GitHubApiService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GitHubRepositoryTest {

    private lateinit var fakeApiService: FakeGitHubApiService
    private lateinit var fakeRepoDao: FakeRepoDao
    private lateinit var fakeIssueDao: FakeIssueDao
    private lateinit var repository: GitHubRepository

    @Before
    fun setup() {
        fakeApiService = FakeGitHubApiService()
        fakeRepoDao = FakeRepoDao()
        fakeIssueDao = FakeIssueDao()
        repository = GitHubRepository(fakeApiService, fakeRepoDao, fakeIssueDao)
    }

    @Test
    fun `getAuthenticatedUser returns user from api`() = runBlocking {
        val result = repository.getAuthenticatedUser()

        assertThat(result.isSuccess).isTrue()
        val user = result.getOrThrow()
        assertThat(user.login).isEqualTo("octocat")
        assertThat(user.name).isEqualTo("The Octocat")
    }

    @Test
    fun `getRepositories caches items into local Room DAO`() = runBlocking {
        val result = repository.getRepositories(forceRefresh = true)

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrThrow()).hasSize(1)
        assertThat(fakeRepoDao.inserted).hasSize(1)
        assertThat(fakeRepoDao.inserted.first().fullName).isEqualTo("octocat/Hello-World")
    }

    @Test
    fun `searchRepositories returns matching search items`() = runBlocking {
        val result = repository.searchRepositories("kotlin")

        assertThat(result.isSuccess).isTrue()
        assertThat(result.getOrThrow()).isNotEmpty()
    }
}

class FakeRepoDao : RepoDao {
    val inserted = mutableListOf<RepoEntity>()

    override fun getCachedRepos(): Flow<List<RepoEntity>> = flowOf(inserted)
    override suspend fun getRepoByFullName(fullName: String): RepoEntity? = inserted.find { it.fullName == fullName }
    override suspend fun insertRepos(repos: List<RepoEntity>) { inserted.addAll(repos) }
    override suspend fun clearRepos() { inserted.clear() }
}

class FakeIssueDao : IssueDao {
    val inserted = mutableListOf<IssueEntity>()

    override fun getIssuesForRepo(repoFullName: String): Flow<List<IssueEntity>> = flowOf(inserted)
    override suspend fun insertIssues(issues: List<IssueEntity>) { inserted.addAll(issues) }
    override suspend fun clearIssuesForRepo(repoFullName: String) { inserted.clear() }
}

class FakeGitHubApiService : GitHubApiService {
    override suspend fun getAuthenticatedUser(): User = User(id = 1L, login = "octocat", name = "The Octocat")
    override suspend fun getUser(username: String): User = User(id = 2L, login = username)
    override suspend fun getAuthenticatedUserRepositories(page: Int, perPage: Int, sort: String): List<Repository> =
        listOf(Repository(id = 101L, name = "Hello-World", fullName = "octocat/Hello-World"))
    override suspend fun getUserRepositories(username: String, page: Int, perPage: Int): List<Repository> = emptyList()
    override suspend fun searchRepositories(query: String, page: Int, perPage: Int, sort: String): SearchResponse<Repository> =
        SearchResponse(total_count = 1, items = listOf(Repository(id = 202L, name = "Kotlin-Sample", fullName = "android/Kotlin-Sample")))
    override suspend fun getRepository(owner: String, repo: String): Repository =
        Repository(id = 303L, name = repo, fullName = "$owner/$repo")
    override suspend fun getBranches(owner: String, repo: String): List<Branch> = listOf(Branch(name = "main"))
    override suspend fun getCommits(owner: String, repo: String, branch: String?, page: Int, perPage: Int): List<CommitInfo> = emptyList()
    override suspend fun getContents(owner: String, repo: String, path: String, ref: String?): List<FileNode> = emptyList()
    override suspend fun getFileContent(owner: String, repo: String, path: String, ref: String?): RemoteFileContent =
        RemoteFileContent(name = "test.kt", path = "test.kt", sha = "abc", content = "cHJpbnRsbigiSGk2NCIp")
    override suspend fun createOrUpdateFile(owner: String, repo: String, path: String, body: CreateOrUpdateFileRequest): FileCommitResponse =
        FileCommitResponse(commit = CommitSummary(sha = "def", message = body.message))
    override suspend fun deleteFile(owner: String, repo: String, path: String, body: DeleteFileRequest): FileCommitResponse =
        FileCommitResponse(commit = CommitSummary(sha = "del", message = body.message))
    override suspend fun getReadme(owner: String, repo: String, ref: String?): RemoteFileContent =
        RemoteFileContent(name = "README.md", path = "README.md", sha = "readme_sha", content = "IyBIdWdlIFJlYWRtZQ==")
    override suspend fun getIssues(owner: String, repo: String, state: String, page: Int, perPage: Int): List<Issue> = emptyList()
    override suspend fun getIssue(owner: String, repo: String, number: Int): Issue =
        Issue(id = 1L, number = number, title = "Issue #$number")
    override suspend fun createIssue(owner: String, repo: String, request: CreateIssueRequest): Issue =
        Issue(id = 2L, number = 10, title = request.title, body = request.body)
    override suspend fun getIssueComments(owner: String, repo: String, number: Int): List<IssueComment> = emptyList()
    override suspend fun addIssueComment(owner: String, repo: String, number: Int, body: CreateCommentRequest): IssueComment =
        IssueComment(id = 1L, body = body.body)
    override suspend fun getPullRequests(owner: String, repo: String, state: String, page: Int, perPage: Int): List<PullRequest> = emptyList()
    override suspend fun getPullRequest(owner: String, repo: String, number: Int): PullRequest =
        PullRequest(id = 1L, number = number, title = "PR #$number")
    override suspend fun getPullRequestFiles(owner: String, repo: String, number: Int): List<PullRequestFile> = emptyList()
    override suspend fun mergePullRequest(owner: String, repo: String, number: Int, body: MergePullRequestRequest): MergeResult =
        MergeResult(sha = "merged_sha", merged = true, message = "Merged")
    override suspend fun getWorkflows(owner: String, repo: String): WorkflowResponse = WorkflowResponse()
    override suspend fun getWorkflowRuns(owner: String, repo: String, page: Int, perPage: Int): WorkflowRunsResponse = WorkflowRunsResponse()
    override suspend fun getNotifications(all: Boolean, page: Int, perPage: Int): List<Notification> = emptyList()
    override suspend fun getReleases(owner: String, repo: String, page: Int, perPage: Int): List<Release> = emptyList()
    override suspend fun downloadArchive(owner: String, repo: String, ref: String): ResponseBody =
        byteArrayOf().toResponseBody("application/zip".toMediaTypeOrNull())
}
