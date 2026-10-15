package io.gitforge.app.ui.home

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MergeType
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.ForkRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import io.gitforge.core.database.WatchedReleaseEntity
import io.gitforge.core.designsystem.component.EssentialsGroupedItem
import io.gitforge.core.designsystem.component.GitForgeDeveloperWorkspaceHero
import io.gitforge.core.designsystem.component.GitForgeEmptyView
import io.gitforge.core.designsystem.component.GitForgeErrorView
import io.gitforge.app.ui.repos.GitForgeRepositoryCard
import io.gitforge.core.designsystem.component.GitForgeShimmerCard
import io.gitforge.core.designsystem.component.GitForgeStatusBadge
import io.gitforge.core.designsystem.component.GroupedCardPosition
import io.gitforge.core.designsystem.component.bounceClick
import io.gitforge.core.designsystem.theme.AmberAccentDark
import io.gitforge.core.designsystem.theme.CodeTypography
import io.gitforge.core.designsystem.theme.CyanAccentDark
import io.gitforge.core.designsystem.theme.GoogleSansCodeFamily
import io.gitforge.core.designsystem.theme.GoogleSansFlexFamily
import io.gitforge.core.designsystem.theme.ItemCardShape
import io.gitforge.core.designsystem.theme.PillBadgeShape
import io.gitforge.core.designsystem.theme.SubCardShape
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
    val context = LocalContext.current
    var selectedFilter by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    // Toast Feedback
    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToastMessage()
        }
    }

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
            GitForgeShimmerCard(height = 180.dp)
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
        contentPadding = PaddingValues(top = 8.dp, bottom = 88.dp),
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
                    border = BorderStroke(1.dp, Color(0x33A8C7FA))
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
                                    .size(40.dp)
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
                                    color = Color(0xFFEDF0F5)
                                )
                                Text(
                                    text = "Direct 1-tap update via native installer.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF9AA0A6)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Button(
                                onClick = onNavigateToSettings,
                                shape = PillBadgeShape,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA8C7FA)),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text("Update", fontWeight = FontWeight.Bold, color = Color(0xFF041E49))
                            }
                            IconButton(onClick = { viewModel.dismissUpdateBanner() }) {
                                Icon(Icons.Default.Close, contentDescription = "Dismiss", modifier = Modifier.size(18.dp), tint = Color(0xFF9AA0A6))
                            }
                        }
                    }
                }
            }
        }

        // Active APK Downloading / Installing Banner
        if (uiState.isInstallingApk) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = ItemCardShape,
                    color = Color(0xFF14241B),
                    border = BorderStroke(1.dp, Color(0x3381C784))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    progress = { uiState.installProgress / 100f },
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.5.dp,
                                    color = Color(0xFF81C784)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Downloading APK: ${uiState.activeInstallingRepo ?: ""}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFEDF0F5)
                                )
                            }
                            Text(
                                text = "${uiState.installProgress}%",
                                style = CodeTypography,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF81C784)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { uiState.installProgress / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(PillBadgeShape),
                            color = Color(0xFF81C784),
                            trackColor = Color(0x2281C784)
                        )
                    }
                }
            }
        }

        // Authentic Developer Workspace Hero Banner
        item {
            val devName = uiState.user?.name ?: uiState.user?.login ?: "biZenX"
            val devHandle = if (uiState.user != null) "@${uiState.user?.login}" else "@biZenX"
            val avatar = uiState.user?.avatarUrl ?: "https://github.com/biZenX.png"

            GitForgeDeveloperWorkspaceHero(
                developerName = devName,
                developerHandle = devHandle,
                avatarUrl = avatar,
                repositoryCount = uiState.recentRepositories.size,
                starredCount = uiState.recentRepositories.count { it.stargazersCount > 0 },
                trackedReleasesCount = uiState.watchedReleases.size,
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                filterOptions = listOf("All", "Tracking", "Starred", "Sources", "Forks"),
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it },
                onRefresh = { viewModel.refresh() }
            )
        }

        // Watched Releases Section (Active when "Tracking" filter is selected)
        if (selectedFilter == "Tracking") {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Followed App Releases",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEDF0F5)
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
                            text = "${uiState.watchedReleases.size} Apps",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFB0B7C3),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (uiState.watchedReleases.isEmpty()) {
                item {
                    GitForgeEmptyView(
                        title = "No tracked app releases",
                        description = "Tap the 🔔 Watch icon on any repository to follow new releases and get 1-tap APK installation!",
                        icon = Icons.Default.NotificationsActive
                    )
                }
            } else {
                items(uiState.watchedReleases, key = { it.repoFullName }) { watched ->
                    WatchedReleaseCard(
                        watched = watched,
                        onInstallApk = { apkUrl ->
                            viewModel.installReleaseApk(apkUrl, watched.repoName, watched.lastKnownTag ?: "latest")
                        },
                        onOpenRepo = {
                            onNavigateToRepo(watched.ownerLogin, watched.repoName)
                        }
                    )
                }
            }
        } else {
            // Essentials Grouped Developer Tools
            item {
                Text(
                    text = "Developer Tools & Actions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEDF0F5)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column {
                    EssentialsGroupedItem(
                        title = "Code Studio",
                        subtitle = "Integrated offline code editor & syntax engine",
                        icon = Icons.Default.EditNote,
                        iconBackgroundColor = Color(0xFFC2E7FF),
                        iconTintColor = Color(0xFF004A77),
                        position = GroupedCardPosition.Top,
                        onClick = onNavigateToStudio
                    )
                    EssentialsGroupedItem(
                        title = "Sandboxed Terminal",
                        subtitle = "Local shell environment & JGit execution",
                        icon = Icons.Default.Terminal,
                        iconBackgroundColor = Color(0xFFC4EED0),
                        iconTintColor = Color(0xFF00522B),
                        position = GroupedCardPosition.Middle,
                        onClick = onNavigateToTerminal
                    )
                    EssentialsGroupedItem(
                        title = "Issues & Bug Tracker",
                        subtitle = "Inspect discussions, comments, and open tasks",
                        icon = Icons.Outlined.BugReport,
                        iconBackgroundColor = Color(0xFFFFD8E4),
                        iconTintColor = Color(0xFF633B48),
                        position = GroupedCardPosition.Middle,
                        onClick = { onNavigateToProductivity("issues") }
                    )
                    EssentialsGroupedItem(
                        title = "Pull Requests & Merges",
                        subtitle = "Review branch diffs and manage integrations",
                        icon = Icons.AutoMirrored.Filled.MergeType,
                        iconBackgroundColor = Color(0xFFEADDFF),
                        iconTintColor = Color(0xFF4F378B),
                        position = GroupedCardPosition.Bottom,
                        onClick = { onNavigateToProductivity("pulls") }
                    )
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
                        color = Color(0xFFEDF0F5)
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
                    val isWatched = uiState.watchedReleases.any { it.repoFullName == repo.fullName }
                    GitForgeRepositoryCard(
                        repository = repo,
                        isWatched = isWatched,
                        onToggleWatch = { viewModel.toggleWatch(repo) },
                        onClick = {
                            val owner = repo.owner?.login ?: ""
                            onNavigateToRepo(owner, repo.name)
                        }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Authentic Watched Release Card for followed apps.
 * Allows 1-tap APK downloading and direct opening.
 */
@Composable
private fun WatchedReleaseCard(
    watched: WatchedReleaseEntity,
    onInstallApk: (String) -> Unit,
    onOpenRepo: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .bounceClick(onClick = onOpenRepo),
        shape = RoundedCornerShape(22.dp),
        color = Color(0xFF1E2128),
        border = BorderStroke(1.dp, Color(0x1AFFFFFF))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row: Avatar, Name, and Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = watched.ownerAvatarUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0x33A8C7FA), RoundedCornerShape(12.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = watched.repoName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEDF0F5)
                    )
                    Text(
                        text = watched.repoFullName,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9AA0A6)
                    )
                }

                if (!watched.lastKnownTag.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(PillBadgeShape)
                            .background(Color(0x2281C784))
                            .border(1.dp, Color(0x4481C784), PillBadgeShape)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = watched.lastKnownTag ?: "",
                            style = CodeTypography,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color(0xFF81C784)
                        )
                    }
                }
            }

            if (!watched.latestReleaseName.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = watched.latestReleaseName ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFEDF0F5),
                    maxLines = 1
                )
            }

            if (!watched.latestReleaseBody.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = watched.latestReleaseBody ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF9AA0A6),
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // If release URL or APK is available
                val apkUrl = watched.latestReleaseUrl
                Button(
                    onClick = {
                        if (!apkUrl.isNullOrBlank()) {
                            onInstallApk(apkUrl)
                        } else {
                            onOpenRepo()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = PillBadgeShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFA8C7FA),
                        contentColor = Color(0xFF041E49)
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RocketLaunch,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Install APK",
                        fontFamily = GoogleSansFlexFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Button(
                    onClick = onOpenRepo,
                    modifier = Modifier.weight(1f),
                    shape = PillBadgeShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF262A33),
                        contentColor = Color(0xFFEDF0F5)
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "View Release",
                        fontFamily = GoogleSansFlexFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
