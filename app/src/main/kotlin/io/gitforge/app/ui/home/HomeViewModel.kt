package io.gitforge.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.gitforge.app.data.GitHubRepository
import io.gitforge.app.data.updater.AppUpdateInfo
import io.gitforge.app.data.updater.AppUpdateManager
import io.gitforge.core.database.WatchedReleaseDao
import io.gitforge.core.database.WatchedReleaseEntity
import io.gitforge.core.model.Repository
import io.gitforge.core.model.User
import io.gitforge.core.security.TokenStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val user: User? = null,
    val recentRepositories: List<Repository> = emptyList(),
    val watchedReleases: List<WatchedReleaseEntity> = emptyList(),
    val updateAvailable: AppUpdateInfo? = null,
    val isInstallingApk: Boolean = false,
    val installProgress: Int = 0,
    val activeInstallingRepo: String? = null,
    val errorMessage: String? = null,
    val toastMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: GitHubRepository,
    private val tokenStorage: TokenStorage,
    private val updateManager: AppUpdateManager,
    private val watchedReleaseDao: WatchedReleaseDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeAuthentication()
        observeWatchedReleases()
        checkForUpdates()
    }

    fun checkForUpdates() {
        viewModelScope.launch {
            val res = updateManager.checkForUpdates(currentVersionName = "1.0.6", currentVersionCode = 7)
            if (res.isSuccess) {
                _uiState.update { it.copy(updateAvailable = res.getOrNull()) }
            }
        }
    }

    fun dismissUpdateBanner() {
        _uiState.update { it.copy(updateAvailable = null) }
    }

    fun clearToastMessage() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    private fun observeWatchedReleases() {
        viewModelScope.launch {
            watchedReleaseDao.getAllWatched().collect { list ->
                _uiState.update { it.copy(watchedReleases = list) }
            }
        }
    }

    fun toggleWatch(repo: Repository) {
        viewModelScope.launch {
            val fullName = repo.fullName
            val isCurrentlyWatched = _uiState.value.watchedReleases.any { it.repoFullName == fullName }
            if (isCurrentlyWatched) {
                watchedReleaseDao.unwatch(fullName)
                _uiState.update { it.copy(toastMessage = "Unfollowed releases for ${repo.name}") }
            } else {
                val ownerLogin = repo.owner?.login ?: ""
                val releases = repository.getReleases(ownerLogin, repo.name, 1).getOrNull()
                val latest = releases?.firstOrNull()
                val entity = WatchedReleaseEntity(
                    repoFullName = fullName,
                    repoName = repo.name,
                    ownerLogin = ownerLogin,
                    ownerAvatarUrl = repo.owner?.avatarUrl ?: "",
                    lastKnownTag = latest?.tagName,
                    latestReleaseName = latest?.name ?: latest?.tagName,
                    latestReleaseUrl = latest?.htmlUrl,
                    latestReleaseBody = latest?.body,
                    isNotifyEnabled = true
                )
                watchedReleaseDao.insertWatched(entity)
                _uiState.update { it.copy(toastMessage = "🔔 Now following ${repo.name}! You'll be notified of new releases.") }
            }
        }
    }

    fun installReleaseApk(apkUrl: String, repoName: String, tagName: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isInstallingApk = true,
                    installProgress = 0,
                    activeInstallingRepo = "$repoName $tagName"
                )
            }
            val fileName = "${repoName}-${tagName}.apk"
            val result = updateManager.downloadAndInstallApkUrl(apkUrl, fileName) { progress ->
                _uiState.update { it.copy(installProgress = progress) }
            }
            _uiState.update {
                it.copy(
                    isInstallingApk = false,
                    activeInstallingRepo = null,
                    toastMessage = if (result.isSuccess) "Installer launched!" else "Download failed: ${result.exceptionOrNull()?.message}"
                )
            }
        }
    }

    private fun observeAuthentication() {
        viewModelScope.launch {
            tokenStorage.hasTokenFlow().collect { hasToken ->
                _uiState.update { it.copy(isAuthenticated = hasToken) }
                if (hasToken) {
                    loadDashboard()
                } else {
                    loadGuestDashboard()
                }
            }
        }
    }

    fun refresh() {
        if (_uiState.value.isAuthenticated) {
            loadDashboard()
        } else {
            loadGuestDashboard()
        }
        refreshWatchedReleases()
    }

    private fun refreshWatchedReleases() {
        viewModelScope.launch {
            val watchedList = _uiState.value.watchedReleases
            for (watched in watchedList) {
                try {
                    val releases = repository.getReleases(watched.ownerLogin, watched.repoName, 1).getOrNull()
                    val latest = releases?.firstOrNull()
                    if (latest != null && latest.tagName != watched.lastKnownTag) {
                        watchedReleaseDao.updateReleaseInfo(
                            repoFullName = watched.repoFullName,
                            tag = latest.tagName,
                            name = latest.name ?: latest.tagName,
                            url = latest.htmlUrl,
                            body = latest.body
                        )
                    }
                } catch (_: Exception) {}
            }
        }
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val userResult = repository.getAuthenticatedUser()
            val reposResult = repository.getRepositories(forceRefresh = true)

            _uiState.update { current ->
                current.copy(
                    isLoading = false,
                    user = userResult.getOrNull() ?: current.user,
                    recentRepositories = reposResult.getOrDefault(emptyList()),
                    errorMessage = userResult.exceptionOrNull()?.message ?: reposResult.exceptionOrNull()?.message
                )
            }
        }
    }

    private fun loadGuestDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val trendingResult = repository.searchRepositories("stars:>10000", page = 1)
            _uiState.update { current ->
                current.copy(
                    isLoading = false,
                    user = null,
                    recentRepositories = trendingResult.getOrDefault(emptyList()),
                    errorMessage = trendingResult.exceptionOrNull()?.message
                )
            }
        }
    }
}
