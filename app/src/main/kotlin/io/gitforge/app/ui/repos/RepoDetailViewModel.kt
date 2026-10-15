package io.gitforge.app.ui.repos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.gitforge.app.data.GitHubRepository
import io.gitforge.core.model.Branch
import io.gitforge.core.model.CommitInfo
import io.gitforge.core.model.FileNode
import io.gitforge.core.model.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Base64
import javax.inject.Inject

import io.gitforge.app.work.RepoArchiveDownloadWorker
import io.gitforge.app.work.WorkManagerHelper
import io.gitforge.core.database.WatchedReleaseDao
import io.gitforge.core.database.WatchedReleaseEntity
import io.gitforge.core.model.Release
import java.util.UUID

data class RepoDetailUiState(
    val isLoading: Boolean = false,
    val repository: Repository? = null,
    val branches: List<Branch> = emptyList(),
    val currentBranch: String = "main",
    val commits: List<CommitInfo> = emptyList(),
    val releases: List<Release> = emptyList(),
    val isWatched: Boolean = false,
    val currentPath: String = "",
    val fileTree: List<FileNode> = emptyList(),
    val readmeContent: String? = null,
    val activeViewingFile: FileNode? = null,
    val activeFileContent: String? = null,
    val isCommittingFile: Boolean = false,
    val isDownloadingArchive: Boolean = false,
    val downloadProgress: Int = 0,
    val downloadedFilePath: String? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class RepoDetailViewModel @Inject constructor(
    private val repository: GitHubRepository,
    private val workManagerHelper: WorkManagerHelper,
    private val watchedReleaseDao: WatchedReleaseDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(RepoDetailUiState())
    val uiState: StateFlow<RepoDetailUiState> = _uiState.asStateFlow()
    private var currentDownloadWorkId: UUID? = null

    fun downloadArchive() {
        val repo = _uiState.value.repository ?: return
        val owner = repo.owner?.login ?: ""
        val branch = _uiState.value.currentBranch
        val workId = workManagerHelper.enqueueArchiveDownload(owner, repo.name, branch)
        currentDownloadWorkId = workId

        viewModelScope.launch {
            _uiState.update { it.copy(isDownloadingArchive = true, downloadProgress = 0) }
            workManagerHelper.observeWorkInfo(workId).collect { workInfo ->
                if (workInfo != null) {
                    val progress = workInfo.progress.getInt(RepoArchiveDownloadWorker.KEY_PROGRESS, 0)
                    _uiState.update { it.copy(downloadProgress = progress) }

                    if (workInfo.state.isFinished) {
                        val path = workInfo.outputData.getString(RepoArchiveDownloadWorker.KEY_OUTPUT_PATH)
                        _uiState.update {
                            it.copy(
                                isDownloadingArchive = false,
                                downloadedFilePath = path,
                                successMessage = if (path != null) "ZIP archive downloaded successfully!" else null
                            )
                        }
                    }
                }
            }
        }
    }

    fun cancelDownload() {
        currentDownloadWorkId?.let { workManagerHelper.cancelWork(it) }
        _uiState.update { it.copy(isDownloadingArchive = false, downloadProgress = 0) }
    }

    fun loadRepositoryDetails(owner: String, repo: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val fullName = "$owner/$repo"

            // Observe watch status
            launch {
                watchedReleaseDao.isWatchedFlow(fullName).collect { entity ->
                    _uiState.update { it.copy(isWatched = entity != null) }
                }
            }

            val repoRes = repository.getRepository(owner, repo)
            val branchesRes = repository.getBranches(owner, repo)
            val defaultBranch = repoRes.getOrNull()?.defaultBranch ?: "main"

            val commitsRes = repository.getCommits(owner, repo, branch = defaultBranch)
            val treeRes = repository.getContents(owner, repo, path = "", ref = defaultBranch)
            val readmeRes = repository.getReadme(owner, repo, ref = defaultBranch)
            val releasesRes = repository.getReleases(owner, repo)

            val decodedReadme = readmeRes.getOrNull()?.content?.let { raw ->
                try {
                    String(Base64.getMimeDecoder().decode(raw.replace("\n", "")))
                } catch (e: Exception) {
                    null
                }
            }

            _uiState.update { current ->
                current.copy(
                    isLoading = false,
                    repository = repoRes.getOrNull(),
                    branches = branchesRes.getOrDefault(emptyList()),
                    currentBranch = defaultBranch,
                    commits = commitsRes.getOrDefault(emptyList()),
                    releases = releasesRes.getOrDefault(emptyList()),
                    fileTree = treeRes.getOrDefault(emptyList()),
                    readmeContent = decodedReadme,
                    errorMessage = repoRes.exceptionOrNull()?.message
                )
            }
        }
    }

    fun toggleWatch() {
        val repo = _uiState.value.repository ?: return
        val ownerLogin = repo.owner?.login ?: ""
        val fullName = repo.fullName
        val currentWatched = _uiState.value.isWatched

        viewModelScope.launch {
            if (currentWatched) {
                watchedReleaseDao.unwatch(fullName)
                _uiState.update { it.copy(successMessage = "Unwatched releases for $fullName") }
            } else {
                val latestTag = _uiState.value.releases.firstOrNull()?.tagName
                val latest = _uiState.value.releases.firstOrNull()
                val entity = WatchedReleaseEntity(
                    repoFullName = fullName,
                    repoName = repo.name,
                    ownerLogin = ownerLogin,
                    ownerAvatarUrl = repo.owner?.avatarUrl ?: "",
                    lastKnownTag = latestTag,
                    latestReleaseName = latest?.name,
                    latestReleaseUrl = latest?.htmlUrl,
                    latestReleaseBody = latest?.body,
                    isNotifyEnabled = true
                )
                watchedReleaseDao.insertWatched(entity)
                _uiState.update { it.copy(successMessage = "🔔 Now following releases for $fullName! You'll be notified of new releases.") }
            }
        }
    }

    fun loadReleases(owner: String, repo: String) {
        viewModelScope.launch {
            val res = repository.getReleases(owner, repo)
            if (res.isSuccess) {
                _uiState.update { it.copy(releases = res.getOrDefault(emptyList())) }
            }
        }
    }

    fun selectBranch(branch: String) {
        val repo = _uiState.value.repository ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(currentBranch = branch, isLoading = true) }
            val commits = repository.getCommits(repo.owner?.login ?: "", repo.name, branch = branch)
            val contents = repository.getContents(repo.owner?.login ?: "", repo.name, path = _uiState.value.currentPath, ref = branch)

            _uiState.update { current ->
                current.copy(
                    isLoading = false,
                    commits = commits.getOrDefault(emptyList()),
                    fileTree = contents.getOrDefault(emptyList())
                )
            }
        }
    }

    fun navigateToDirectory(path: String) {
        val repo = _uiState.value.repository ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(currentPath = path, isLoading = true) }
            val contents = repository.getContents(repo.owner?.login ?: "", repo.name, path = path, ref = _uiState.value.currentBranch)
            _uiState.update { current ->
                current.copy(
                    isLoading = false,
                    fileTree = contents.getOrDefault(emptyList())
                )
            }
        }
    }

    fun openFile(fileNode: FileNode) {
        val repo = _uiState.value.repository ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, activeViewingFile = fileNode) }
            val contentRes = repository.getFileContent(
                repo.owner?.login ?: "",
                repo.name,
                path = fileNode.path,
                ref = _uiState.value.currentBranch
            )
            val decoded = contentRes.getOrNull()?.content?.let { raw ->
                try {
                    String(Base64.getMimeDecoder().decode(raw.replace("\n", "")))
                } catch (e: Exception) {
                    raw
                }
            }
            _uiState.update { current ->
                current.copy(
                    isLoading = false,
                    activeFileContent = decoded
                )
            }
        }
    }

    fun closeActiveFile() {
        _uiState.update { it.copy(activeViewingFile = null, activeFileContent = null) }
    }

    fun saveRemoteFile(newContent: String, commitMessage: String) {
        val repo = _uiState.value.repository ?: return
        val activeFile = _uiState.value.activeViewingFile ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isCommittingFile = true) }
            val result = repository.createOrUpdateFile(
                owner = repo.owner?.login ?: "",
                repo = repo.name,
                path = activeFile.path,
                message = commitMessage.ifBlank { "Update ${activeFile.name} via GitForge" },
                plainContent = newContent,
                sha = activeFile.sha,
                branch = _uiState.value.currentBranch
            )

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        isCommittingFile = false,
                        activeFileContent = newContent,
                        successMessage = "Committed changes to GitHub successfully!"
                    )
                }
                // Refresh commits and contents
                selectBranch(_uiState.value.currentBranch)
            } else {
                _uiState.update {
                    it.copy(
                        isCommittingFile = false,
                        errorMessage = "Commit failed: ${result.exceptionOrNull()?.message}"
                    )
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
