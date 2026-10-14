package io.gitforge.app.ui.repos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.gitforge.app.data.GitHubRepository
import io.gitforge.core.model.Repository
import io.gitforge.core.security.TokenStorage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RepositoriesUiState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val repositories: List<Repository> = emptyList(),
    val errorMessage: String? = null,
    val selectedFilter: String = "All" // "All", "Public", "Private"
)

@HiltViewModel
class RepositoriesViewModel @Inject constructor(
    private val repository: GitHubRepository,
    private val tokenStorage: TokenStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(RepositoriesUiState())
    val uiState: StateFlow<RepositoriesUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadRepositories()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(400) // Debounce
            if (query.isBlank()) {
                loadRepositories()
            } else {
                search(query)
            }
        }
    }

    fun setFilter(filter: String) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun loadRepositories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val hasToken = tokenStorage.getToken() != null
            val result = if (hasToken) {
                repository.getRepositories(forceRefresh = true)
            } else {
                repository.searchRepositories("topic:android stars:>1000", page = 1)
            }

            _uiState.update { current ->
                current.copy(
                    isLoading = false,
                    repositories = result.getOrDefault(emptyList()),
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    private suspend fun search(query: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        val result = repository.searchRepositories(query)
        _uiState.update { current ->
            current.copy(
                isLoading = false,
                repositories = result.getOrDefault(emptyList()),
                errorMessage = result.exceptionOrNull()?.message
            )
        }
    }
}
