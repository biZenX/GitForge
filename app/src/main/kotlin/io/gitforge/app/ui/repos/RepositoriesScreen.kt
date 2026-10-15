package io.gitforge.app.ui.repos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.gitforge.app.ui.home.WafeerRepositoryCard
import io.gitforge.core.designsystem.component.GitForgeEmptyView
import io.gitforge.core.designsystem.component.GitForgeErrorView
import io.gitforge.core.designsystem.component.GitForgeLoadingView
import io.gitforge.core.designsystem.component.GitForgeShimmerCard
import io.gitforge.core.designsystem.theme.PillBadgeShape
import io.gitforge.core.designsystem.theme.TerracottaPrimaryDark

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

        // Expressive Pill Search Bar
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            placeholder = { Text("Search repositories on GitHub...", style = MaterialTheme.typography.bodyMedium) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = TerracottaPrimaryDark) },
            trailingIcon = {
                if (uiState.searchQuery.isNotBlank()) {
                    IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                    }
                }
            },
            shape = PillBadgeShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                focusedBorderColor = TerracottaPrimaryDark,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Expressive Pill Filter chips
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("All", "Public", "Private").forEach { filter ->
                FilterChip(
                    selected = uiState.selectedFilter == filter,
                    onClick = { viewModel.setFilter(filter) },
                    label = { Text(filter) },
                    shape = PillBadgeShape,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = TerracottaPrimaryDark.copy(alpha = 0.2f),
                        selectedLabelColor = TerracottaPrimaryDark
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (uiState.isLoading && uiState.repositories.isEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                GitForgeShimmerCard(height = 92.dp)
                GitForgeShimmerCard(height = 92.dp)
                GitForgeShimmerCard(height = 92.dp)
                GitForgeShimmerCard(height = 92.dp)
            }
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
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredRepos) { repo ->
                    WafeerRepositoryCard(
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
