package io.gitforge.app.ui.productivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.gitforge.app.data.GitHubRepository
import io.gitforge.core.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductivityUiState(
    val isLoading: Boolean = false,
    val selectedRepo: Repository? = null,
    val issues: List<Issue> = emptyList(),
    val issueFilter: String = "open", // open, closed
    val pullRequests: List<PullRequest> = emptyList(),
    val prFilter: String = "open", // open, closed
    val workflows: List<Workflow> = emptyList(),
    val workflowRuns: List<WorkflowRun> = emptyList(),
    val notifications: List<Notification> = emptyList(),
    val releases: List<Release> = emptyList(),
    val activeIssueComments: List<IssueComment> = emptyList(),
    val activePullRequestFiles: List<PullRequestFile> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class ProductivityViewModel @Inject constructor(
    private val repository: GitHubRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductivityUiState())
    val uiState: StateFlow<ProductivityUiState> = _uiState.asStateFlow()

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val res = repository.getNotifications()
            _uiState.update { current ->
                current.copy(
                    isLoading = false,
                    notifications = res.getOrDefault(emptyList()),
                    errorMessage = res.exceptionOrNull()?.message
                )
            }
        }
    }

    fun loadRepositoryProductivity(owner: String, repo: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val issuesRes = repository.getIssues(owner, repo, state = _uiState.value.issueFilter)
            val prsRes = repository.getPullRequests(owner, repo, state = _uiState.value.prFilter)
            val workflowsRes = repository.getWorkflows(owner, repo)
            val runsRes = repository.getWorkflowRuns(owner, repo)
            val releasesRes = repository.getReleases(owner, repo)

            _uiState.update { current ->
                current.copy(
                    isLoading = false,
                    issues = issuesRes.getOrDefault(emptyList()),
                    pullRequests = prsRes.getOrDefault(emptyList()),
                    workflows = workflowsRes.getOrDefault(emptyList()),
                    workflowRuns = runsRes.getOrDefault(emptyList()),
                    releases = releasesRes.getOrDefault(emptyList()),
                    errorMessage = issuesRes.exceptionOrNull()?.message ?: prsRes.exceptionOrNull()?.message
                )
            }
        }
    }

    fun setIssueFilter(owner: String, repo: String, state: String) {
        _uiState.update { it.copy(issueFilter = state) }
        viewModelScope.launch {
            val res = repository.getIssues(owner, repo, state = state)
            _uiState.update { it.copy(issues = res.getOrDefault(emptyList())) }
        }
    }

    fun createIssue(owner: String, repo: String, title: String, body: String? = null) {
        viewModelScope.launch {
            val res = repository.createIssue(owner, repo, title, body)
            if (res.isSuccess) {
                _uiState.update { it.copy(successMessage = "Issue created successfully!") }
                val issuesRes = repository.getIssues(owner, repo, state = _uiState.value.issueFilter)
                _uiState.update { it.copy(issues = issuesRes.getOrDefault(emptyList())) }
            } else {
                _uiState.update { it.copy(errorMessage = "Failed to create issue: ${res.exceptionOrNull()?.message}") }
            }
        }
    }

    fun setPrFilter(owner: String, repo: String, state: String) {
        _uiState.update { it.copy(prFilter = state) }
        viewModelScope.launch {
            val res = repository.getPullRequests(owner, repo, state = state)
            _uiState.update { it.copy(pullRequests = res.getOrDefault(emptyList())) }
        }
    }

    fun loadIssueComments(owner: String, repo: String, issueNumber: Int) {
        viewModelScope.launch {
            val res = repository.getIssueComments(owner, repo, issueNumber)
            _uiState.update { it.copy(activeIssueComments = res.getOrDefault(emptyList())) }
        }
    }

    fun addIssueComment(owner: String, repo: String, issueNumber: Int, body: String) {
        viewModelScope.launch {
            val res = repository.addIssueComment(owner, repo, issueNumber, body)
            if (res.isSuccess) {
                loadIssueComments(owner, repo, issueNumber)
                _uiState.update { it.copy(successMessage = "Comment posted!") }
            } else {
                _uiState.update { it.copy(errorMessage = "Failed to post comment: ${res.exceptionOrNull()?.message}") }
            }
        }
    }

    fun loadPullRequestFiles(owner: String, repo: String, pullNumber: Int) {
        viewModelScope.launch {
            val res = repository.getPullRequestFiles(owner, repo, pullNumber)
            _uiState.update { it.copy(activePullRequestFiles = res.getOrDefault(emptyList())) }
        }
    }

    fun mergePullRequest(owner: String, repo: String, pullNumber: Int, method: String) {
        viewModelScope.launch {
            val res = repository.mergePullRequest(owner, repo, pullNumber, method = method)
            if (res.isSuccess) {
                _uiState.update { it.copy(successMessage = "Pull request merged successfully!") }
                loadRepositoryProductivity(owner, repo)
            } else {
                _uiState.update { it.copy(errorMessage = "Merge failed: ${res.exceptionOrNull()?.message}") }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
