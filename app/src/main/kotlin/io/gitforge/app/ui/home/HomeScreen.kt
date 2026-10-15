package io.gitforge.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MergeType
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.ForkRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import io.gitforge.core.designsystem.component.GitForgeEmptyView
import io.gitforge.core.designsystem.component.GitForgeErrorView
import io.gitforge.core.designsystem.component.GitForgeLanguageDot
import io.gitforge.core.designsystem.component.GitForgeLoadingView
import io.gitforge.core.designsystem.component.GitForgeMetricCard
import io.gitforge.core.designsystem.component.GitForgeShimmerCard
import io.gitforge.core.designsystem.component.GitForgeStatusBadge
import io.gitforge.core.designsystem.component.GitForgeWhatsNewSheet
import io.gitforge.core.designsystem.component.WafeerHeroBanner
import io.gitforge.core.designsystem.component.bounceClick
import io.gitforge.core.designsystem.theme.AmberAccentDark
import io.gitforge.core.designsystem.theme.CyanAccentDark
import io.gitforge.core.designsystem.theme.EmeraldAccentDark
import io.gitforge.core.designsystem.theme.HeroCardShape
import io.gitforge.core.designsystem.theme.IconContainerShape
import io.gitforge.core.designsystem.theme.ItemCardShape
import io.gitforge.core.designsystem.theme.PillBadgeShape
import io.gitforge.core.designsystem.theme.RoseAccentDark
import io.gitforge.core.designsystem.theme.SubCardShape
import io.gitforge.core.designsystem.theme.TerracottaPrimaryDark
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
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            GitForgeShimmerCard(height = 140.dp)
            GitForgeShimmerCard(height = 84.dp)
            GitForgeShimmerCard(height = 84.dp)
            GitForgeShimmerCard(height = 84.dp)
        }
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
        // In-App Update Available Banner
        if (uiState.updateAvailable != null) {
            val update = uiState.updateAvailable!!
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = ItemCardShape,
                    color = TerracottaPrimaryDark.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TerracottaPrimaryDark.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(TerracottaPrimaryDark.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🚀", style = MaterialTheme.typography.titleMedium)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "GitForge v${update.versionName} Available!",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Tap Update to install directly via native updater.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Button(
                                onClick = onNavigateToSettings,
                                shape = PillBadgeShape,
                                colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimaryDark),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text("Update", style = MaterialTheme.typography.labelMedium)
                            }
                            IconButton(onClick = { viewModel.dismissUpdateBanner() }) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss", modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        // Hero Card (Wafeer / Zenith caliber)
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = HeroCardShape,
                color = MaterialTheme.colorScheme.surfaceContainer,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Profile Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (uiState.user != null) {
                            AsyncImage(
                                model = uiState.user?.avatarUrl,
                                contentDescription = "User Avatar",
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(IconContainerShape)
                                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, IconContainerShape)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = uiState.user?.name ?: uiState.user?.login ?: "Developer",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "@${uiState.user?.login}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TerracottaPrimaryDark,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(PillBadgeShape)
                                    .background(EmeraldAccentDark.copy(alpha = 0.12f))
                                    .border(1.dp, EmeraldAccentDark.copy(alpha = 0.3f), PillBadgeShape)
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(EmeraldAccentDark)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Active",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = EmeraldAccentDark,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(IconContainerShape)
                                    .background(Color(0xFF21262D))
                                    .border(1.5.dp, Color(0xFF30363D), IconContainerShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Person,
                                    contentDescription = "Guest Developer",
                                    tint = Color(0xFF8B949E),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Developer Workspace",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Local offline mode • Sign in for GitHub sync",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF8B949E),
                                    fontWeight = FontWeight.Normal
                                )
                            }
                            Button(
                                onClick = onNavigateToSettings,
                                shape = PillBadgeShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF238636),
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                Text("Sign In", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Authentic Wafeer Hero Banner ("يافطة وفير الكبرى")
        item {
            var selectedFilter by remember { mutableStateOf("All") }
            val repoCount = uiState.user?.publicRepos ?: uiState.recentRepositories.size
            WafeerHeroBanner(
                title = "Workspace Overview",
                primaryMetricValue = "$repoCount Repositories",
                primaryMetricLabel = "All local projects synchronized",
                primaryMetricAvailable = "Available: 100% Offline",
                secondaryLeftTitle = "Active Branch",
                secondaryLeftValue = "main",
                secondaryLeftSubtext = "HEAD -> origin/main",
                badgeCount = "$repoCount",
                badgeLabel = "Active",
                filterOptions = listOf("All", "Starred", "Local", "Cloned"),
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )
        }

        // Quick Tools (Expressive Pill Chips)
        item {
            Text(
                text = "Workspace Tools",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    ExpressiveActionPill(
                        title = "Code Studio",
                        icon = Icons.Default.EditNote,
                        tint = CyanAccentDark,
                        onClick = onNavigateToStudio
                    )
                }
                item {
                    ExpressiveActionPill(
                        title = "Terminal",
                        icon = Icons.Default.Terminal,
                        tint = EmeraldAccentDark,
                        onClick = onNavigateToTerminal
                    )
                }
                item {
                    ExpressiveActionPill(
                        title = "Issues",
                        icon = Icons.Outlined.BugReport,
                        tint = RoseAccentDark,
                        onClick = { onNavigateToProductivity("issues") }
                    )
                }
                item {
                    ExpressiveActionPill(
                        title = "Pull Requests",
                        icon = Icons.AutoMirrored.Filled.MergeType,
                        tint = TerracottaPrimaryDark,
                        onClick = { onNavigateToProductivity("pulls") }
                    )
                }
                item {
                    ExpressiveActionPill(
                        title = "Actions CI",
                        icon = Icons.Default.PlayArrow,
                        tint = AmberAccentDark,
                        onClick = { onNavigateToProductivity("actions") }
                    )
                }
            }
        }

        // Repositories Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (uiState.isAuthenticated) "Your Repositories" else "Featured Repositories",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .clip(PillBadgeShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, PillBadgeShape)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${uiState.recentRepositories.size} Repos",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Repositories List
        if (uiState.recentRepositories.isEmpty()) {
            item {
                GitForgeEmptyView(
                    title = "No repositories found",
                    description = "Search or clone repositories to get started with your workspace.",
                    icon = Icons.Outlined.Folder
                )
            }
        } else {
            items(uiState.recentRepositories) { repo ->
                WafeerRepositoryCard(
                    repository = repo,
                    onClick = {
                        val owner = repo.owner?.login ?: ""
                        onNavigateToRepo(owner, repo.name)
                    }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ExpressiveActionPill(
    title: String,
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = PillBadgeShape,
        color = Color(0xFF21262D),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x228B949E)),
        modifier = Modifier
            .height(44.dp)
            .bounceClick(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(15.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun WafeerRepositoryCard(
    repository: Repository,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(ItemCardShape)
            .bounceClick(onClick = onClick),
        shape = ItemCardShape,
        color = Color(0xFF161B22),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x338B949E))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(IconContainerShape)
                        .background(TerracottaPrimaryDark.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (repository.private) Icons.Default.Lock else Icons.Outlined.Folder,
                        contentDescription = null,
                        tint = TerracottaPrimaryDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = repository.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = repository.fullName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                GitForgeStatusBadge(
                    status = if (repository.private) "Private" else "Public",
                    showDot = false
                )
            }

            if (!repository.description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = repository.description ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                GitForgeLanguageDot(language = repository.language)
                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .clip(PillBadgeShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, PillBadgeShape)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Stars",
                            tint = AmberAccentDark,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${repository.stargazersCount}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .clip(PillBadgeShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, PillBadgeShape)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ForkRight,
                            contentDescription = "Forks",
                            tint = CyanAccentDark,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${repository.forksCount}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
