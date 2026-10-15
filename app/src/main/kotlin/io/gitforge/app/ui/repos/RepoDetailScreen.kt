package io.gitforge.app.ui.repos

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import io.gitforge.core.model.ReleaseAsset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MergeType
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.ForkRight
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.InsertDriveFile
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import io.gitforge.core.designsystem.component.GitForgeEmptyView
import io.gitforge.core.designsystem.component.GitForgeErrorView
import io.gitforge.core.designsystem.component.GitForgeMarkdownView
import io.gitforge.core.designsystem.component.GitForgeShimmerCard
import io.gitforge.core.designsystem.component.GitForgeStatusBadge
import io.gitforge.core.designsystem.component.bounceClick
import io.gitforge.core.designsystem.theme.AmberAccentDark
import io.gitforge.core.designsystem.theme.CodeTypography
import io.gitforge.core.designsystem.theme.CyanAccentDark
import io.gitforge.core.designsystem.theme.EmeraldAccentDark
import io.gitforge.core.designsystem.theme.GoogleSansCodeFamily
import io.gitforge.core.designsystem.theme.GoogleSansFlexFamily
import io.gitforge.core.designsystem.theme.HeroCardShape
import io.gitforge.core.designsystem.theme.IconContainerShape
import io.gitforge.core.designsystem.theme.ItemCardShape
import io.gitforge.core.designsystem.theme.PillBadgeShape
import io.gitforge.core.designsystem.theme.RoseAccentDark
import io.gitforge.core.designsystem.theme.SubCardShape
import io.gitforge.core.designsystem.theme.TerracottaPrimaryDark
import io.gitforge.core.model.Branch
import io.gitforge.core.model.CommitInfo
import io.gitforge.core.model.FileNode
import io.gitforge.core.model.FileNodeType
import io.gitforge.core.model.Issue
import io.gitforge.core.model.PullRequest
import io.gitforge.core.model.Release
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepoDetailScreen(
    owner: String,
    repo: String,
    viewModel: RepoDetailViewModel,
    onBack: () -> Unit,
    onNavigateToEditor: (String, String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var branchFilterQuery by remember { mutableStateOf("") }
    var expandedCommitSha by remember { mutableStateOf<String?>(null) }
    var issuesFilterState by remember { mutableStateOf("issues") } // "issues" or "pulls"
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    LaunchedEffect(owner, repo) {
        viewModel.loadRepositoryDetails(owner, repo)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = repo,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = owner,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.bounceClick(onClick = onBack)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    // Watch / Follow Releases Toggle Button
                    IconButton(
                        onClick = { viewModel.toggleWatch() },
                        modifier = Modifier.bounceClick(onClick = { viewModel.toggleWatch() })
                    ) {
                        Icon(
                            imageVector = if (uiState.isWatched) Icons.Default.NotificationsActive else Icons.Default.NotificationsNone,
                            contentDescription = if (uiState.isWatched) "Unwatch Releases" else "Watch Releases",
                            tint = if (uiState.isWatched) TerracottaPrimaryDark else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Download ZIP Archive
                    if (uiState.isDownloadingArchive) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = { uiState.downloadProgress / 100f },
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = EmeraldAccentDark
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${uiState.downloadProgress}%",
                                style = CodeTypography,
                                fontSize = 11.sp
                            )
                        }
                    } else {
                        IconButton(
                            onClick = { viewModel.downloadArchive() },
                            modifier = Modifier.bounceClick(onClick = { viewModel.downloadArchive() })
                        ) {
                            Icon(Icons.Default.Download, contentDescription = "Download ZIP", tint = TerracottaPrimaryDark)
                        }
                    }

                    // Share Repo Link
                    IconButton(
                        onClick = {
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                putExtra(Intent.EXTRA_TEXT, "https://github.com/$owner/$repo")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Repository"))
                        },
                        modifier = Modifier.bounceClick(onClick = {
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                putExtra(Intent.EXTRA_TEXT, "https://github.com/$owner/$repo")
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Repository"))
                        })
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    // Branch Switcher Pill
                    Surface(
                        onClick = { viewModel.setBranchSheetOpen(true) },
                        shape = PillBadgeShape,
                        color = Color(0xFF161B22),
                        border = BorderStroke(1.dp, Color(0x338B949E)),
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .bounceClick { viewModel.setBranchSheetOpen(true) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.ForkRight,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = TerracottaPrimaryDark
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = uiState.currentBranch,
                                style = CodeTypography.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE6EDF3)
                                )
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Repository Quick Stats Header Bar
            val repoData = uiState.repository
            if (repoData != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF161B22))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Stars
                    RepoStatChip(
                        icon = Icons.Default.Star,
                        label = "${repoData.stargazersCount} Stars",
                        tint = AmberAccentDark
                    )
                    // Forks
                    RepoStatChip(
                        icon = Icons.Default.ForkRight,
                        label = "${repoData.forksCount} Forks",
                        tint = CyanAccentDark
                    )
                    // Language
                    if (!repoData.language.isNullOrBlank()) {
                        RepoStatChip(
                            icon = Icons.Outlined.Folder,
                            label = repoData.language ?: "",
                            tint = TerracottaPrimaryDark
                        )
                    }
                    // Releases Count
                    RepoStatChip(
                        icon = Icons.Default.RocketLaunch,
                        label = "${uiState.releases.size} Releases",
                        tint = EmeraldAccentDark
                    )
                }
            }

            // Tabs Row: README, Files, Commits, Issues & PRs, Releases
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = TerracottaPrimaryDark
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("README", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Outlined.Description, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Files", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Outlined.Folder, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Commits", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("Issues", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Outlined.BugReport, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                Tab(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    text = { Text("Releases", fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                    icon = { Icon(Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
            }

            // Shimmer Loading Skeleton
            if (uiState.isLoading && uiState.fileTree.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    GitForgeShimmerCard(height = 120.dp)
                    GitForgeShimmerCard(height = 64.dp)
                    GitForgeShimmerCard(height = 64.dp)
                    GitForgeShimmerCard(height = 64.dp)
                }
                return@Column
            }

            // Error View
            if (uiState.errorMessage != null && uiState.fileTree.isEmpty()) {
                GitForgeErrorView(
                    message = uiState.errorMessage ?: "Failed to load repository",
                    onRetry = { viewModel.loadRepositoryDetails(owner, repo) }
                )
                return@Column
            }

            // Animated Tab Transition
            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally(animationSpec = tween(280)) { width -> width / 4 } + fadeIn(tween(200)))
                            .togetherWith(slideOutHorizontally(animationSpec = tween(250)) { width -> -width / 4 } + fadeOut(tween(200)))
                    } else {
                        (slideInHorizontally(animationSpec = tween(280)) { width -> -width / 4 } + fadeIn(tween(200)))
                            .togetherWith(slideOutHorizontally(animationSpec = tween(250)) { width -> width / 4 } + fadeOut(tween(200)))
                    }
                },
                modifier = Modifier.weight(1f),
                label = "repoTabContent"
            ) { tab ->
                when (tab) {
                    0 -> {
                        // README Tab with Supercharged Markdown View
                        if (!uiState.readmeContent.isNullOrBlank()) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                contentPadding = PaddingValues(vertical = 16.dp)
                            ) {
                                item {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = HeroCardShape,
                                        color = Color(0xFF161B22),
                                        border = BorderStroke(1.dp, Color(0x338B949E))
                                    ) {
                                        Column(modifier = Modifier.padding(20.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Outlined.Description,
                                                        contentDescription = null,
                                                        tint = TerracottaPrimaryDark,
                                                        modifier = Modifier.size(22.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(
                                                        text = "README.md",
                                                        fontFamily = GoogleSansFlexFamily,
                                                        style = MaterialTheme.typography.titleMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFFE6EDF3)
                                                    )
                                                }
                                                Box(
                                                    modifier = Modifier
                                                        .clip(PillBadgeShape)
                                                        .background(Color(0xFF21262D))
                                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                                ) {
                                                    Text(
                                                        text = uiState.currentBranch,
                                                        fontFamily = GoogleSansCodeFamily,
                                                        fontSize = 11.sp,
                                                        color = TerracottaPrimaryDark
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(16.dp))

                                            GitForgeMarkdownView(
                                                markdownContent = uiState.readmeContent ?: "",
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            GitForgeEmptyView(
                                title = "No README found",
                                description = "This repository doesn't have a README.md file on branch ${uiState.currentBranch}.",
                                icon = Icons.Outlined.Description
                            )
                        }
                    }
                    1 -> {
                        // Files Tab
                        Column(modifier = Modifier.fillMaxSize()) {
                            // Breadcrumb
                            if (uiState.currentPath.isNotBlank()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFF161B22))
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(onClick = { viewModel.navigateToDirectory("") }) {
                                        Text("root", color = TerracottaPrimaryDark, fontWeight = FontWeight.Bold)
                                    }
                                    Text("/", color = Color(0xFF8B949E))
                                    val parent = uiState.currentPath.substringBeforeLast("/", "")
                                    TextButton(onClick = { viewModel.navigateToDirectory(parent) }) {
                                        Text(".. (back)", color = TerracottaPrimaryDark)
                                    }
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        text = uiState.currentPath,
                                        style = CodeTypography,
                                        fontSize = 12.sp,
                                        color = Color(0xFF8B949E)
                                    )
                                }
                            }

                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(uiState.fileTree) { fileNode ->
                                    WafeerFileNodeItem(
                                        fileNode = fileNode,
                                        onClick = {
                                            if (fileNode.nodeType == FileNodeType.DIR) {
                                                viewModel.navigateToDirectory(fileNode.path)
                                            } else {
                                                viewModel.openFile(fileNode)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                    2 -> {
                        // Commits Tab
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.commits) { commit ->
                                val isExpanded = expandedCommitSha == commit.sha
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .bounceClick {
                                            expandedCommitSha = if (isExpanded) null else commit.sha
                                        },
                                    shape = ItemCardShape,
                                    color = Color(0xFF161B22),
                                    border = BorderStroke(1.dp, if (isExpanded) TerracottaPrimaryDark.copy(alpha = 0.5f) else Color(0x338B949E))
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = commit.commit.message.lines().firstOrNull() ?: "",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFE6EDF3)
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(PillBadgeShape)
                                                        .background(TerracottaPrimaryDark.copy(alpha = 0.12f))
                                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                                ) {
                                                    Text(
                                                        text = commit.commit.author?.name ?: "Author",
                                                        fontFamily = GoogleSansFlexFamily,
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = TerracottaPrimaryDark,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(PillBadgeShape)
                                                        .background(Color(0xFF21262D))
                                                        .clickable {
                                                            clipboardManager.setText(AnnotatedString(commit.sha))
                                                            Toast.makeText(context, "Commit SHA copied!", Toast.LENGTH_SHORT).show()
                                                        }
                                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Text(
                                                            text = commit.sha.take(7),
                                                            style = CodeTypography,
                                                            fontSize = 11.sp,
                                                            color = Color(0xFF8B949E)
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(10.dp), tint = Color(0xFF8B949E))
                                                    }
                                                }
                                            }
                                        }

                                        // Expanded Details: Full message & Author Info
                                        AnimatedVisibility(visible = isExpanded) {
                                            Column(modifier = Modifier.padding(top = 12.dp)) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(1.dp)
                                                        .background(Color(0x228B949E))
                                                )
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = commit.commit.message,
                                                    fontFamily = GoogleSansCodeFamily,
                                                    fontSize = 12.sp,
                                                    color = Color(0xFFE6EDF3),
                                                    lineHeight = 17.sp
                                                )
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(
                                                    text = "Committed by ${commit.commit.author?.email ?: "author"}",
                                                    fontFamily = GoogleSansFlexFamily,
                                                    fontSize = 11.sp,
                                                    color = Color(0xFF8B949E)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    3 -> {
                        // Issues & Pull Requests Tab
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            // Filter Pills: Issues vs Pull Requests
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = issuesFilterState == "issues",
                                    onClick = { issuesFilterState = "issues" },
                                    label = { Text("Issues (${uiState.issues.size})") },
                                    shape = PillBadgeShape,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = TerracottaPrimaryDark.copy(alpha = 0.2f),
                                        selectedLabelColor = TerracottaPrimaryDark
                                    )
                                )
                                FilterChip(
                                    selected = issuesFilterState == "pulls",
                                    onClick = { issuesFilterState = "pulls" },
                                    label = { Text("Pull Requests (${uiState.pullRequests.size})") },
                                    shape = PillBadgeShape,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CyanAccentDark.copy(alpha = 0.2f),
                                        selectedLabelColor = CyanAccentDark
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (issuesFilterState == "issues") {
                                if (uiState.issues.isEmpty()) {
                                    GitForgeEmptyView(
                                        title = "No open issues",
                                        description = "There are no open issues found in this repository.",
                                        icon = Icons.Outlined.BugReport
                                    )
                                } else {
                                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        items(uiState.issues) { issue ->
                                            IssueItemCard(issue = issue)
                                        }
                                    }
                                }
                            } else {
                                if (uiState.pullRequests.isEmpty()) {
                                    GitForgeEmptyView(
                                        title = "No open pull requests",
                                        description = "There are no open pull requests in this repository.",
                                        icon = Icons.AutoMirrored.Filled.MergeType
                                    )
                                } else {
                                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        items(uiState.pullRequests) { pr ->
                                            PullRequestItemCard(pr = pr)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    4 -> {
                        // Releases Tab
                        if (uiState.releases.isNotEmpty()) {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Watch Releases Banner
                                item {
                                    Surface(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = ItemCardShape,
                                        color = if (uiState.isWatched) EmeraldAccentDark.copy(alpha = 0.12f) else Color(0xFF161B22),
                                        border = BorderStroke(
                                            1.dp,
                                            if (uiState.isWatched) EmeraldAccentDark.copy(alpha = 0.35f) else Color(0x338B949E)
                                        )
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
                                                        .size(36.dp)
                                                        .clip(CircleShape)
                                                        .background(if (uiState.isWatched) EmeraldAccentDark.copy(alpha = 0.2f) else TerracottaPrimaryDark.copy(alpha = 0.2f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = if (uiState.isWatched) Icons.Default.NotificationsActive else Icons.Default.NotificationsNone,
                                                        contentDescription = null,
                                                        tint = if (uiState.isWatched) EmeraldAccentDark else TerracottaPrimaryDark,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Column {
                                                    Text(
                                                        text = if (uiState.isWatched) "Following Releases" else "Follow Releases",
                                                        fontFamily = GoogleSansFlexFamily,
                                                        fontWeight = FontWeight.Bold,
                                                        style = MaterialTheme.typography.titleSmall,
                                                        color = Color(0xFFE6EDF3)
                                                    )
                                                    Text(
                                                        text = if (uiState.isWatched) "You will get push notifications for new updates." else "Get notified on phone when a new APK or release drops.",
                                                        fontFamily = GoogleSansFlexFamily,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = Color(0xFF8B949E)
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Button(
                                                onClick = { viewModel.toggleWatch() },
                                                shape = PillBadgeShape,
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (uiState.isWatched) Color(0xFF21262D) else TerracottaPrimaryDark
                                                ),
                                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                                modifier = Modifier.bounceClick { viewModel.toggleWatch() }
                                            ) {
                                                Text(
                                                    text = if (uiState.isWatched) "Unfollow" else "Follow",
                                                    fontFamily = GoogleSansFlexFamily,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }

                                items(uiState.releases) { release ->
                                    ReleaseItemCard(release = release)
                                }
                            }
                        } else {
                            GitForgeEmptyView(
                                title = "No releases found",
                                description = "This repository hasn't published any releases or tags yet.",
                                icon = Icons.Default.RocketLaunch
                            )
                        }
                    }
                }
            }

            // Message Feedbacks
            if (uiState.successMessage != null) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.clearMessages() }) {
                            Text("OK", color = EmeraldAccentDark)
                        }
                    }
                ) {
                    Text(uiState.successMessage ?: "")
                }
            }
        }
    }

    // Branch Switcher Modal Bottom Sheet
    if (uiState.isBranchSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.setBranchSheetOpen(false) },
            sheetState = sheetState,
            containerColor = Color(0xFF161B22)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Select Branch",
                    fontFamily = GoogleSansFlexFamily,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE6EDF3)
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Search Filter for Branches
                OutlinedTextField(
                    value = branchFilterQuery,
                    onValueChange = { branchFilterQuery = it },
                    placeholder = { Text("Filter branches...", fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TerracottaPrimaryDark) },
                    trailingIcon = {
                        if (branchFilterQuery.isNotBlank()) {
                            IconButton(onClick = { branchFilterQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = null)
                            }
                        }
                    },
                    shape = PillBadgeShape,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF21262D),
                        unfocusedContainerColor = Color(0xFF21262D),
                        focusedBorderColor = TerracottaPrimaryDark,
                        unfocusedBorderColor = Color(0x338B949E)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                val filteredBranches = uiState.branches.filter {
                    it.name.contains(branchFilterQuery, ignoreCase = true)
                }

                LazyColumn(
                    modifier = Modifier.heightIn(max = 350.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredBranches) { branch ->
                        val isCurrent = branch.name == uiState.currentBranch
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(ItemCardShape)
                                .clickable {
                                    viewModel.selectBranch(branch.name)
                                    viewModel.setBranchSheetOpen(false)
                                },
                            shape = ItemCardShape,
                            color = if (isCurrent) TerracottaPrimaryDark.copy(alpha = 0.15f) else Color(0xFF21262D),
                            border = BorderStroke(
                                1.dp,
                                if (isCurrent) TerracottaPrimaryDark else Color.Transparent
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.ForkRight,
                                        contentDescription = null,
                                        tint = if (isCurrent) TerracottaPrimaryDark else Color(0xFF8B949E),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = branch.name,
                                        fontFamily = GoogleSansCodeFamily,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 13.sp,
                                        color = if (isCurrent) TerracottaPrimaryDark else Color(0xFFE6EDF3)
                                    )
                                }
                                if (isCurrent) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = TerracottaPrimaryDark, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // In-App File Viewer Dialog
    if (uiState.activeViewingFile != null) {
        val file = uiState.activeViewingFile!!
        AlertDialog(
            onDismissRequest = { viewModel.closeActiveFile() },
            containerColor = Color(0xFF161B22),
            shape = ItemCardShape,
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.InsertDriveFile, contentDescription = null, tint = TerracottaPrimaryDark)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = file.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = Color(0xFFE6EDF3))
                    }
                    Box(
                        modifier = Modifier
                            .clip(PillBadgeShape)
                            .background(Color(0xFF21262D))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${file.size} B",
                            fontFamily = GoogleSansCodeFamily,
                            fontSize = 10.sp,
                            color = Color(0xFF8B949E)
                        )
                    }
                }
            },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                        .clip(SubCardShape)
                        .background(Color(0xFF0D1117))
                        .padding(12.dp)
                        .horizontalScroll(rememberScrollState())
                ) {
                    Text(
                        text = uiState.activeFileContent ?: "Loading file contents...",
                        style = CodeTypography.copy(
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            color = Color(0xFFE6EDF3),
                            fontFamily = GoogleSansCodeFamily
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val path = file.path
                        viewModel.closeActiveFile()
                        onNavigateToEditor(repo, path)
                    },
                    shape = PillBadgeShape,
                    colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimaryDark)
                ) {
                    Icon(Icons.Default.EditNote, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit in Studio")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeActiveFile() }) {
                    Text("Close", color = Color(0xFF8B949E))
                }
            }
        )
    }
}

@Composable
private fun RepoStatChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color
) {
    Surface(
        shape = PillBadgeShape,
        color = Color(0xFF21262D),
        border = BorderStroke(1.dp, Color(0x228B949E))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(13.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontFamily = GoogleSansFlexFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.5.sp,
                color = Color(0xFFE6EDF3)
            )
        }
    }
}

@Composable
private fun IssueItemCard(issue: Issue) {
    val isOpen = issue.state == "open"
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = ItemCardShape,
        color = Color(0xFF161B22),
        border = BorderStroke(1.dp, Color(0x228B949E))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(PillBadgeShape)
                            .background(if (isOpen) EmeraldAccentDark.copy(alpha = 0.15f) else Color(0x338B949E))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (isOpen) "Open" else "Closed",
                            fontFamily = GoogleSansFlexFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = if (isOpen) EmeraldAccentDark else Color(0xFF8B949E)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "#${issue.number}",
                        style = CodeTypography,
                        fontSize = 12.sp,
                        color = Color(0xFF8B949E)
                    )
                }
                Text(
                    text = "${issue.comments} comments",
                    fontFamily = GoogleSansFlexFamily,
                    fontSize = 11.sp,
                    color = Color(0xFF8B949E)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = issue.title,
                fontFamily = GoogleSansFlexFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFFE6EDF3)
            )

            if (issue.labels.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    issue.labels.take(3).forEach { label ->
                        Box(
                            modifier = Modifier
                                .clip(PillBadgeShape)
                                .background(Color(0xFF21262D))
                                .border(1.dp, Color(0x338B949E), PillBadgeShape)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = label.name,
                                fontFamily = GoogleSansFlexFamily,
                                fontSize = 10.sp,
                                color = CyanAccentDark
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PullRequestItemCard(pr: PullRequest) {
    val isOpen = pr.state == "open"
    val isMerged = pr.merged
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = ItemCardShape,
        color = Color(0xFF161B22),
        border = BorderStroke(1.dp, Color(0x228B949E))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(PillBadgeShape)
                            .background(
                                when {
                                    isMerged -> Color(0xFFA855F7).copy(alpha = 0.2f)
                                    isOpen -> EmeraldAccentDark.copy(alpha = 0.15f)
                                    else -> RoseAccentDark.copy(alpha = 0.15f)
                                }
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = when {
                                isMerged -> "Merged"
                                isOpen -> "Open"
                                else -> "Closed"
                            },
                            fontFamily = GoogleSansFlexFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = when {
                                isMerged -> Color(0xFFA855F7)
                                isOpen -> EmeraldAccentDark
                                else -> RoseAccentDark
                            }
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "#${pr.number}",
                        style = CodeTypography,
                        fontSize = 12.sp,
                        color = Color(0xFF8B949E)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "+${pr.additions}", style = CodeTypography, fontSize = 11.sp, color = EmeraldAccentDark)
                    Text(text = "-${pr.deletions}", style = CodeTypography, fontSize = 11.sp, color = RoseAccentDark)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = pr.title,
                fontFamily = GoogleSansFlexFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFFE6EDF3)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "${pr.head.label} → ${pr.base.label}",
                fontFamily = GoogleSansCodeFamily,
                fontSize = 11.sp,
                color = Color(0xFF8B949E)
            )
        }
    }
}

@Composable
fun WafeerFileNodeItem(
    fileNode: FileNode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDir = fileNode.nodeType == FileNodeType.DIR

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .bounceClick(onClick = onClick),
        shape = ItemCardShape,
        color = Color(0xFF161B22),
        border = BorderStroke(1.dp, Color(0x228B949E))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(IconContainerShape)
                    .background(if (isDir) TerracottaPrimaryDark.copy(alpha = 0.15f) else Color(0xFF21262D)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isDir) Icons.Outlined.Folder else Icons.Outlined.InsertDriveFile,
                    contentDescription = null,
                    tint = if (isDir) TerracottaPrimaryDark else CyanAccentDark,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = fileNode.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isDir) FontWeight.Bold else FontWeight.Normal,
                    color = Color(0xFFE6EDF3)
                )
                if (!isDir && fileNode.size > 0) {
                    Text(
                        text = "${fileNode.size} bytes",
                        style = CodeTypography,
                        fontSize = 10.5.sp,
                        color = Color(0xFF8B949E)
                    )
                }
            }
        }
    }
}

@Composable
fun ReleaseItemCard(
    release: Release,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val releasePublished = release.publishedAt ?: ""
    val releaseBody = release.body ?: ""

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = ItemCardShape,
        color = Color(0xFF161B22),
        border = BorderStroke(1.dp, Color(0x338B949E))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(PillBadgeShape)
                            .background(TerracottaPrimaryDark.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = release.tagName,
                            fontFamily = GoogleSansCodeFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = TerracottaPrimaryDark
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    if (releasePublished.isNotBlank()) {
                        Text(
                            text = releasePublished.take(10),
                            fontFamily = GoogleSansFlexFamily,
                            fontSize = 11.sp,
                            color = Color(0xFF8B949E)
                        )
                    }
                }
                GitForgeStatusBadge(
                    status = if (release.prerelease) "Pre-release" else "Release",
                    showDot = false
                )
            }

            if (!release.name.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = release.name ?: "",
                    fontFamily = GoogleSansFlexFamily,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE6EDF3)
                )
            }

            if (releaseBody.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                GitForgeMarkdownView(
                    markdownContent = releaseBody,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Assets Downloads
            val releaseAssets = release.assets
            if (releaseAssets.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Release Assets (${releaseAssets.size}):",
                    fontFamily = GoogleSansFlexFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFFE6EDF3)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    releaseAssets.forEach { asset ->
                        val isApk = asset.name.endsWith(".apk", ignoreCase = true)
                        val assetBytes = asset.size
                        val assetMb = assetBytes / (1024.0 * 1024.0)
                        val formattedSize = if (assetMb >= 1.0) String.format("%.1f MB", assetMb) else "${assetBytes / 1024} KB"

                        Surface(
                            shape = SubCardShape,
                            color = if (isApk) TerracottaPrimaryDark.copy(alpha = 0.12f) else Color(0xFF21262D),
                            border = BorderStroke(1.dp, if (isApk) TerracottaPrimaryDark.copy(alpha = 0.35f) else Color(0x228B949E)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .bounceClick {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(asset.browserDownloadUrl))
                                    context.startActivity(intent)
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isApk) Icons.Default.RocketLaunch else Icons.Default.Download,
                                        contentDescription = null,
                                        tint = if (isApk) TerracottaPrimaryDark else Color(0xFFE6EDF3),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = asset.name,
                                            style = CodeTypography,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Color(0xFFE6EDF3)
                                        )
                                        Text(
                                            text = "$formattedSize • ${asset.downloadCount} downloads",
                                            fontFamily = GoogleSansFlexFamily,
                                            fontSize = 11.sp,
                                            color = Color(0xFF8B949E)
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = "Download",
                                    tint = TerracottaPrimaryDark,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
