package io.gitforge.app.ui.productivity

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MergeType
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.gitforge.core.designsystem.component.GitForgeDiffViewer
import io.gitforge.core.designsystem.component.GitForgeEmptyView
import io.gitforge.core.designsystem.component.GitForgeLoadingView
import io.gitforge.core.designsystem.component.GitForgeStatusBadge
import io.gitforge.core.designsystem.theme.CodeTypography
import io.gitforge.core.designsystem.theme.CyanAccentDark
import io.gitforge.core.designsystem.theme.EmeraldAccentDark
import io.gitforge.core.designsystem.theme.HeroCardShape
import io.gitforge.core.designsystem.theme.IconContainerShape
import io.gitforge.core.designsystem.theme.ItemCardShape
import io.gitforge.core.designsystem.theme.PillBadgeShape
import io.gitforge.core.designsystem.theme.RoseAccentDark
import io.gitforge.core.designsystem.theme.SubCardShape
import io.gitforge.core.designsystem.theme.TerracottaPrimaryDark
import io.gitforge.core.model.Issue
import io.gitforge.core.model.PullRequest

import io.gitforge.core.model.Repository
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductivityScreen(
    initialSection: String = "issues",
    availableRepos: List<Repository> = emptyList(),
    viewModel: ProductivityViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedSection by remember(initialSection) {
        mutableIntStateOf(
            when (initialSection.lowercase()) {
                "pulls", "pullrequests" -> 1
                "actions" -> 2
                "releases" -> 3
                else -> 0
            }
        )
    }

    var selectedRepoIndex by remember { mutableIntStateOf(0) }
    val currentRepo = availableRepos.getOrNull(selectedRepoIndex)
    val owner = currentRepo?.owner?.login ?: "biZenX"
    val repo = currentRepo?.name ?: "GitForge"

    var showCreateIssueDialog by remember { mutableStateOf(false) }
    var newIssueTitle by remember { mutableStateOf("") }
    var newIssueBody by remember { mutableStateOf("") }
    var activeViewingDiff by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(owner, repo) {
        viewModel.loadRepositoryProductivity(owner, repo)
    }

    if (activeViewingDiff != null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Pull Request Diff", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { activeViewingDiff = null }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close Diff")
                        }
                    }
                )
            }
        ) { padding ->
            GitForgeDiffViewer(patch = activeViewingDiff, modifier = Modifier.padding(padding))
        }
        return
    }

    if (showCreateIssueDialog) {
        AlertDialog(
            onDismissRequest = { showCreateIssueDialog = false },
            shape = HeroCardShape,
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            title = { Text("New Issue", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newIssueTitle,
                        onValueChange = { newIssueTitle = it },
                        label = { Text("Issue Title") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = SubCardShape
                    )
                    OutlinedTextField(
                        value = newIssueBody,
                        onValueChange = { newIssueBody = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        shape = SubCardShape
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newIssueTitle.isNotBlank()) {
                            viewModel.createIssue(owner, repo, newIssueTitle, newIssueBody)
                            newIssueTitle = ""
                            newIssueBody = ""
                            showCreateIssueDialog = false
                        }
                    },
                    shape = PillBadgeShape,
                    colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimaryDark)
                ) {
                    Text("Submit", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateIssueDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Productivity Hub", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("$owner/$repo", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    if (selectedSection == 0) {
                        IconButton(onClick = { showCreateIssueDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Create Issue", tint = TerracottaPrimaryDark)
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            PrimaryTabRow(selectedTabIndex = selectedSection) {
                Tab(
                    selected = selectedSection == 0,
                    onClick = { selectedSection = 0 },
                    text = { Text("Issues", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Outlined.BugReport, contentDescription = null) }
                )
                Tab(
                    selected = selectedSection == 1,
                    onClick = { selectedSection = 1 },
                    text = { Text("Pull Requests", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.AutoMirrored.Filled.MergeType, contentDescription = null) }
                )
                Tab(
                    selected = selectedSection == 2,
                    onClick = { selectedSection = 2 },
                    text = { Text("Actions", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = null) }
                )
                Tab(
                    selected = selectedSection == 3,
                    onClick = { selectedSection = 3 },
                    text = { Text("Releases", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.NewReleases, contentDescription = null) }
                )
            }

            if (availableRepos.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(androidx.compose.ui.graphics.Color(0xFF1E2128))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Repository:",
                        style = MaterialTheme.typography.labelMedium,
                        color = androidx.compose.ui.graphics.Color(0xFF9AA0A6),
                        fontWeight = FontWeight.Bold
                    )
                    availableRepos.forEachIndexed { index, r ->
                        FilterChip(
                            selected = index == selectedRepoIndex,
                            onClick = { selectedRepoIndex = index },
                            label = { Text(r.name) },
                            shape = PillBadgeShape,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = androidx.compose.ui.graphics.Color(0xFF2B477A),
                                selectedLabelColor = androidx.compose.ui.graphics.Color(0xFFD3E3FD),
                                containerColor = androidx.compose.ui.graphics.Color(0xFF14161B),
                                labelColor = androidx.compose.ui.graphics.Color(0xFF9AA0A6)
                            )
                        )
                    }
                }
            }

            if (uiState.isLoading && uiState.issues.isEmpty() && uiState.pullRequests.isEmpty()) {
                GitForgeLoadingView(message = "Loading productivity data...")
                return@Column
            }

            when (selectedSection) {
                0 -> {
                    // Issues
                    if (uiState.issues.isEmpty()) {
                        GitForgeEmptyView(
                            title = "No Issues Found",
                            description = "No issues matching the filter for $owner/$repo.",
                            actionLabel = "Create Issue",
                            onActionClick = { showCreateIssueDialog = true }
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.issues) { issue ->
                                WafeerIssueCard(issue = issue)
                            }
                        }
                    }
                }
                1 -> {
                    // Pull Requests
                    if (uiState.pullRequests.isEmpty()) {
                        GitForgeEmptyView(
                            title = "No Pull Requests",
                            description = "There are no pull requests for $owner/$repo."
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.pullRequests) { pr ->
                                WafeerPullRequestCard(
                                    pullRequest = pr,
                                    onViewDiff = {
                                        activeViewingDiff = """
                                            --- a/README.md
                                            +++ b/README.md
                                            @@ -1,3 +1,4 @@
                                            # Hello World
                                            +Updated features via Pull Request #${pr.number}
                                        """.trimIndent()
                                    },
                                    onMerge = {
                                        viewModel.mergePullRequest(owner, repo, pr.number, "merge")
                                    }
                                )
                            }
                        }
                    }
                }
                2 -> {
                    // Actions Workflows & Runs
                    if (uiState.workflowRuns.isEmpty()) {
                        GitForgeEmptyView(
                            title = "No Workflow Runs",
                            description = "No GitHub Actions runs recorded for $owner/$repo."
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.workflowRuns) { run ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = ItemCardShape,
                                    color = MaterialTheme.colorScheme.surfaceContainer,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(IconContainerShape)
                                                .background(EmeraldAccentDark.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = EmeraldAccentDark, modifier = Modifier.size(20.dp))
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = run.name.ifBlank { "Workflow #${run.runNumber}" },
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "${run.headBranch ?: "main"} • ${run.event}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        GitForgeStatusBadge(status = run.conclusion ?: run.status)
                                    }
                                }
                            }
                        }
                    }
                }
                3 -> {
                    // Releases
                    if (uiState.releases.isEmpty()) {
                        GitForgeEmptyView(
                            title = "No Releases",
                            description = "No releases published yet for $owner/$repo."
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.releases) { release ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = ItemCardShape,
                                    color = MaterialTheme.colorScheme.surfaceContainer,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                ) {
                                    Column(modifier = Modifier.padding(18.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = release.name ?: release.tagName,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.weight(1f)
                                            )
                                            GitForgeStatusBadge(status = if (release.prerelease) "Pre-release" else "Stable")
                                        }
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "Tag: ${release.tagName}",
                                            style = CodeTypography,
                                            color = TerracottaPrimaryDark
                                        )
                                        if (!release.body.isNullOrBlank()) {
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                text = release.body ?: "",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WafeerIssueCard(issue: Issue) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = ItemCardShape,
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(IconContainerShape)
                        .background(RoseAccentDark.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.BugReport, contentDescription = null, tint = RoseAccentDark, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "#${issue.number}",
                    style = CodeTypography,
                    color = TerracottaPrimaryDark,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = issue.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                GitForgeStatusBadge(status = issue.state)
            }
            if (!issue.body.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = issue.body ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
fun WafeerPullRequestCard(
    pullRequest: PullRequest,
    onViewDiff: () -> Unit,
    onMerge: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = ItemCardShape,
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(IconContainerShape)
                        .background(CyanAccentDark.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.AutoMirrored.Filled.MergeType, contentDescription = null, tint = CyanAccentDark, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "#${pullRequest.number}",
                    style = CodeTypography,
                    color = TerracottaPrimaryDark,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = pullRequest.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                GitForgeStatusBadge(status = pullRequest.state)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(PillBadgeShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "+${pullRequest.additions} -${pullRequest.deletions}",
                        style = CodeTypography,
                        color = EmeraldAccentDark,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                OutlinedButton(
                    onClick = onViewDiff,
                    shape = PillBadgeShape,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("Diff", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                if (pullRequest.state == "open") {
                    Button(
                        onClick = onMerge,
                        shape = PillBadgeShape,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldAccentDark, contentColor = MaterialTheme.colorScheme.onPrimary),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("Merge", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
