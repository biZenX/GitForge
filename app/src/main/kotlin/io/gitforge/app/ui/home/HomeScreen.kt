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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.sp
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
import io.gitforge.core.designsystem.theme.LavenderAccentDark
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
    var selectedFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    val filteredRepositories = remember(uiState.recentRepositories, selectedFilter, searchQuery) {
        uiState.recentRepositories
            .filter { repo ->
                when (selectedFilter) {
                    "Starred" -> repo.stargazersCount > 0
                    "Forks" -> repo.fork
                    "Sources" -> !repo.fork
                    else -> true
                }
            }
            .filter { repo ->
                searchQuery.isBlank() ||
                repo.name.contains(searchQuery, ignoreCase = true) ||
                (repo.description?.contains(searchQuery, ignoreCase = true) == true)
            }
    }

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
        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // In-App Update Available Banner
        if (uiState.updateAvailable != null) {
            val update = uiState.updateAvailable!!
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = ItemCardShape,
                    color = Color(0xFF1E2838),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33A8C7FA))
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
                                    .background(Color(0x22A8C7FA)),
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
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text("Update", style = MaterialTheme.typography.labelMedium, color = Color(0xFF041E49))
                            }
                            IconButton(onClick = { viewModel.dismissUpdateBanner() }) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss", modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        // Minimal Profile & Sync Header
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                color = Color(0xFF1E2128),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x1AFFFFFF))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (uiState.user != null) {
                        AsyncImage(
                            model = uiState.user?.avatarUrl,
                            contentDescription = "User Avatar",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, Color(0x33A8C7FA), RoundedCornerShape(16.dp))
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = uiState.user?.name ?: uiState.user?.login ?: "Developer",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "@${uiState.user?.login}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF262A33))
                                .border(1.dp, Color(0x14FFFFFF), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = "Developer",
                                tint = Color(0xFF9AA0A6),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Developer Workspace",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Local offline storage active",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF9AA0A6)
                            )
                        }
                        Button(
                            onClick = onNavigateToSettings,
                            shape = PillBadgeShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF234275),
                                contentColor = Color(0xFFD3E3FD)
                            ),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text("Sign In", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Authentic Wafeer Hero Banner ("يافطة وفير الكبرى") - Real Dynamic Functionality
        item {
            val userLogin = uiState.user?.login ?: "biZenX"
            WafeerHeroBanner(
                title = "Workspace Hub",
                primaryMetricValue = "${filteredRepositories.size} Repositories",
                primaryMetricLabel = if (selectedFilter == "All") "All repositories synchronized" else "Filtered by $selectedFilter",
                primaryMetricAvailable = if (uiState.isAuthenticated) "Cloud Sync: Active" else "Local Workspace: Ready",
                secondaryLeftTitle = "Active Developer",
                secondaryLeftValue = userLogin,
                secondaryLeftSubtext = if (uiState.isAuthenticated) "GitHub Connected" else "Guest Mode",
                badgeCount = "${filteredRepositories.size}",
                badgeLabel = if (selectedFilter == "All") "Total" else selectedFilter,
                filterOptions = listOf("All", "Starred", "Sources", "Forks"),
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it },
                onEditClick = { viewModel.refresh() }
            )
        }

        // Real-Time Instant Search Filter
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = PillBadgeShape,
                color = Color(0xFF1E2128),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x14FFFFFF))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Folder,
                        contentDescription = "Search",
                        tint = Color(0xFF9AA0A6),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    androidx.compose.foundation.text.BasicTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f).padding(vertical = 12.dp),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                        decorationBox = { innerTextField ->
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Quick search in ${filteredRepositories.size} repositories...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF6B7280)
                                )
                            }
                            innerTextField()
                        }
                    )
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color(0xFF9AA0A6), modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // Quick Tools (Essentials Pill Chips with Soft Pastels)
        item {
            Text(
                text = "Developer Studio & Tools",
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
                item {
                    ExpressiveActionPill(
                        title = "Sync",
                        icon = Icons.Default.Close,
                        tint = LavenderAccentDark,
                        onClick = { viewModel.refresh() }
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
                    text = if (selectedFilter == "All") "Your Repositories" else "$selectedFilter Repositories",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .clip(PillBadgeShape)
                        .background(Color(0xFF262A33))
                        .border(1.dp, Color(0x14FFFFFF), PillBadgeShape)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${filteredRepositories.size} Repos",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFB0B7C3),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Repositories List
        if (filteredRepositories.isEmpty()) {
            item {
                GitForgeEmptyView(
                    title = "No repositories matching filter",
                    description = if (searchQuery.isNotEmpty()) "No results matching \"$searchQuery\"." else "No repositories under $selectedFilter filter.",
                    icon = Icons.Outlined.Folder
                )
            }
        } else {
            items(filteredRepositories, key = { it.id }) { repo ->
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
            Spacer(modifier = Modifier.height(24.dp))
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
        color = Color(0xFF1E2128),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x1AFFFFFF)),
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
            .clip(RoundedCornerShape(22.dp))
            .bounceClick(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFF1E2128),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x1AFFFFFF))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x22A8C7FA)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (repository.private) Icons.Default.Lock else Icons.Outlined.Folder,
                        contentDescription = null,
                        tint = Color(0xFFA8C7FA),
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
                        .background(Color(0xFF262A33))
                        .border(1.dp, Color(0x14FFFFFF), PillBadgeShape)
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
                        .background(Color(0xFF262A33))
                        .border(1.dp, Color(0x14FFFFFF), PillBadgeShape)
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
