package io.gitforge.app.ui.repos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.gitforge.app.ui.home.RepositoryCard
import io.gitforge.core.designsystem.component.GitForgeEmptyView
import io.gitforge.core.designsystem.component.GitForgeErrorView
import io.gitforge.core.designsystem.component.GitForgeLoadingView

@Composable
fun RepositoriesScreen(
    viewModel: RepositoriesViewModel,
    onNavigateToRepo: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Search Outlined Box
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            placeholder = { Text("Search repositories on GitHub...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (uiState.searchQuery.isNotBlank()) {
                    IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filter chips
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("All", "Public", "Private").forEach { filter ->
                FilterChip(
                    selected = uiState.selectedFilter == filter,
                    onClick = { viewModel.setFilter(filter) },
                    label = { Text(filter) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (uiState.isLoading && uiState.repositories.isEmpty()) {
            GitForgeLoadingView(message = "Searching repositories...")
        } else if (uiState.errorMessage != null && uiState.repositories.isEmpty()) {
            GitForgeErrorView(
                message = uiState.errorMessage ?: "Failed to load repositories",
                onRetry = { viewModel.loadRepositories() }
            )
        } else if (uiState.repositories.isEmpty()) {
            GitForgeEmptyView(
                title = "No repositories found",
                description = "Try a different search query or check your filters.",
                icon = Icons.Outlined.Folder
            )
        } else {
            val filteredRepos = uiState.repositories.filter { repo ->
                when (uiState.selectedFilter) {
                    "Public" -> !repo.private
                    "Private" -> repo.private
                    else -> true
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredRepos) { repo ->
                    RepositoryCard(
                        repository = repo,
                        onClick = {
                            val owner = repo.owner?.login ?: ""
                            onNavigateToRepo(owner, repo.name)
                        }
                    )
                }
            }
        }
    }
}
