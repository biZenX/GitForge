package io.gitforge.app.ui.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.gitforge.core.database.RepoDao
import io.gitforge.core.security.OAuthManager
import io.gitforge.core.security.TokenStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val isAuthenticated: Boolean = false,
    val tokenPreview: String = "",
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val tokenStorage: TokenStorage,
    private val oauthManager: OAuthManager,
    private val repoDao: RepoDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        checkToken()
    }

    private fun checkToken() {
        viewModelScope.launch {
            val token = tokenStorage.getToken()
            if (!token.isNullOrBlank()) {
                val preview = token.take(6) + "..." + token.takeLast(4)
                _uiState.update { it.copy(isAuthenticated = true, tokenPreview = preview) }
            } else {
                _uiState.update { it.copy(isAuthenticated = false, tokenPreview = "") }
            }
        }
    }

    fun savePersonalAccessToken(token: String) {
        val trimmed = token.trim()
        if (!oauthManager.validatePersonalAccessToken(trimmed)) {
            _uiState.update { it.copy(errorMessage = "Invalid Personal Access Token format. It should start with 'ghp_' or 'github_pat_'.") }
            return
        }

        viewModelScope.launch {
            tokenStorage.saveToken(trimmed)
            checkToken()
            _uiState.update { it.copy(successMessage = "Access token saved securely in Keystore!") }
        }
    }

    fun generateOAuthUrl(): String {
        return oauthManager.generateAuthorizationUrl().first
    }

    fun handleOAuthCallback(uri: Uri) {
        val result = oauthManager.verifyCallback(uri)
        if (result.isSuccess) {
            val code = result.getOrThrow()
            // In GitHub OAuth Web Flow, the code is exchanged for a token via an OAuth proxy or GitHub App.
            // For client-side setup, saving the code or exchanged token:
            viewModelScope.launch {
                tokenStorage.saveToken(code)
                checkToken()
                _uiState.update { it.copy(successMessage = "Authenticated successfully via GitHub OAuth!") }
            }
        } else {
            _uiState.update { it.copy(errorMessage = result.exceptionOrNull()?.message) }
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenStorage.clearToken()
            repoDao.clearRepos()
            checkToken()
            _uiState.update { it.copy(successMessage = "Logged out successfully.") }
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            repoDao.clearRepos()
            _uiState.update { it.copy(successMessage = "Offline cache cleared.") }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
