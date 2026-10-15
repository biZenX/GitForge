package io.gitforge.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.gitforge.app.data.GitHubRepository
import io.gitforge.app.data.updater.AppUpdateInfo
import io.gitforge.app.data.updater.AppUpdateManager
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
    val updateAvailable: AppUpdateInfo? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: GitHubRepository,
    private val tokenStorage: TokenStorage,
    private val updateManager: AppUpdateManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeAuthentication()
        checkForUpdates()
    }

    fun checkForUpdates() {
        viewModelScope.launch {
            val res = updateManager.checkForUpdates(currentVersionName = "1.0.4", currentVersionCode = 5)
            if (res.isSuccess) {
                _uiState.update { it.copy(updateAvailable = res.getOrNull()) }
            }
        }
    }

    fun dismissUpdateBanner() {
        _uiState.update { it.copy(updateAvailable = null) }
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
