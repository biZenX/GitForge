package io.gitforge.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import io.gitforge.core.designsystem.component.GitForgeEmptyView
import io.gitforge.core.designsystem.component.GitForgeErrorView
import io.gitforge.core.designsystem.component.GitForgeLanguageDot
import io.gitforge.core.designsystem.component.GitForgeLoadingView
import io.gitforge.core.designsystem.theme.ForgeCyan
import io.gitforge.core.designsystem.theme.ForgeEmerald
import io.gitforge.core.designsystem.theme.ForgeIndigo
import io.gitforge.core.designsystem.theme.ForgeRose
import io.gitforge.core.model.Repository

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToRepo: (String, String) -> Unit,
    onNavigateToTerminal: () -> Unit,
    onNavigateToStudio: () -> Unit,
    onNavigateToProductivity: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading && uiState.recentRepositories.isEmpty()) {
        GitForgeLoadingView(message = "Loading dashboard...")
        return
    }

    if (uiState.errorMessage != null && uiState.recentRepositories.isEmpty()) {
        GitForgeErrorView(
            message = uiState.errorMessage ?: "Unknown error",
            onRetry = { viewModel.refresh() }
        )
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile Card / Guest Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (uiState.user != null) {
                        AsyncImage(
                            model = uiState.user?.avatarUrl,
                            contentDescription = "User Avatar",
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = uiState.user?.name ?: uiState.user?.login ?: "Developer",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "@${uiState.user?.login}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (!uiState.user?.bio.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = uiState.user?.bio ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(ForgeIndigo.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = null,
                                tint = ForgeIndigo,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Welcome to GitForge",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Explore GitHub repositories or sign in for private access.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Button(onClick = onNavigateToSettings) {
                            Text("Sign In")
                        }
                    }
                }
            }
        }

        // Quick Workspace & Tool Shortcuts
        item {
            Text(
                text = "Quick Tools",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    QuickActionChip(
                        title = "Code Studio",
                        icon = Icons.Default.EditNote,
                        color = ForgeIndigo,
                        onClick = onNavigateToStudio
                    )
                }
                item {
                    QuickActionChip(
                        title = "Terminal",
                        icon = Icons.Default.Terminal,
                        color = ForgeEmerald,
                        onClick = onNavigateToTerminal
                    )
                }
                item {
                    QuickActionChip(
                        title = "Issues",
                        icon = Icons.Outlined.BugReport,
                        color = ForgeRose,
                        onClick = { onNavigateToProductivity("issues") }
                    )
                }
                item {
                    QuickActionChip(
                        title = "Pull Requests",
                        icon = Icons.Default.MergeType,
                        color = ForgeCyan,
                        onClick = { onNavigateToProductivity("pulls") }
                    )
                }
                item {
                    QuickActionChip(
                        title = "Actions",
                        icon = Icons.Default.PlayArrow,
                        color = MaterialTheme.colorScheme.primary,
                        onClick = { onNavigateToProductivity("actions") }
                    )
                }
            }
        }

        // Repositories Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (uiState.isAuthenticated) "Your Repositories" else "Trending Repositories",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${uiState.recentRepositories.size} repositories",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Repositories List
        if (uiState.recentRepositories.isEmpty()) {
            item {
                GitForgeEmptyView(
                    title = "No repositories found",
                    description = "Search or clone repositories to get started.",
                    icon = Icons.Outlined.Folder
                )
            }
        } else {
            items(uiState.recentRepositories) { repo ->
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

@Composable
fun QuickActionChip(
    title: String,
    icon: ImageVector,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = color.copy(alpha = 0.12f),
        modifier = Modifier.height(48.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = title, style = MaterialTheme.typography.labelLarge, color = color, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun RepositoryCard(
    repository: Repository,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (repository.private) Icons.Default.Lock else Icons.Outlined.Folder,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = repository.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (!repository.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = repository.description ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                GitForgeLanguageDot(language = repository.language)
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    imageVector = Icons.Outlined.Star,
                    contentDescription = "Stars",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${repository.stargazersCount}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(16.dp))
                Icon(
                    imageVector = Icons.Default.ForkRight,
                    contentDescription = "Forks",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${repository.forksCount}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
