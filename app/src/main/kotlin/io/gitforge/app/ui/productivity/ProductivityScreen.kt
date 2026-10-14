package io.gitforge.app.ui.productivity

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.gitforge.core.designsystem.component.GitForgeDiffViewer
import io.gitforge.core.designsystem.component.GitForgeEmptyView
import io.gitforge.core.designsystem.component.GitForgeLoadingView
import io.gitforge.core.designsystem.component.GitForgeStatusBadge
import io.gitforge.core.designsystem.theme.CodeTypography
import io.gitforge.core.designsystem.theme.ForgeEmerald
import io.gitforge.core.model.Issue
import io.gitforge.core.model.PullRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductivityScreen(
    initialSection: String = "issues",
    viewModel: ProductivityViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedSection by remember(initialSection) {
        mutableStateOf(
            when (initialSection.lowercase()) {
                "pulls", "pullrequests" -> 1
                "actions" -> 2
                "releases" -> 3
                else -> 0
            }
        )
    }

    var showCreateIssueDialog by remember { mutableStateOf(false) }
    var newIssueTitle by remember { mutableStateOf("") }
    var newIssueBody by remember { mutableStateOf("") }
    var activeViewingDiff by remember { mutableStateOf<String?>(null) }

    // Owner and Repo can be prompted or passed; default to active or popular repo
    val owner = remember { "octocat" }
    val repo = remember { "Hello-World" }

    LaunchedEffect(owner, repo) {
        viewModel.loadRepositoryProductivity(owner, repo)
    }

    if (activeViewingDiff != null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Pull Request Diff") },
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
            title = { Text("New Issue") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newIssueTitle,
                        onValueChange = { newIssueTitle = it },
                        label = { Text("Issue Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newIssueBody,
                        onValueChange = { newIssueBody = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
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
                    }
                ) {
                    Text("Submit")
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
                title = { Text("Productivity ($owner/$repo)") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (selectedSection == 0) {
                        IconButton(onClick = { showCreateIssueDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Create Issue")
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
            TabRow(selectedTabIndex = selectedSection) {
                Tab(
                    selected = selectedSection == 0,
                    onClick = { selectedSection = 0 },
                    text = { Text("Issues") },
                    icon = { Icon(Icons.Outlined.BugReport, contentDescription = null) }
                )
                Tab(
                    selected = selectedSection == 1,
                    onClick = { selectedSection = 1 },
                    text = { Text("Pull Requests") },
                    icon = { Icon(Icons.Default.MergeType, contentDescription = null) }
                )
                Tab(
                    selected = selectedSection == 2,
                    onClick = { selectedSection = 2 },
                    text = { Text("Actions") },
                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = null) }
                )
                Tab(
                    selected = selectedSection == 3,
                    onClick = { selectedSection = 3 },
                    text = { Text("Releases") },
                    icon = { Icon(Icons.Default.NewReleases, contentDescription = null) }
                )
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
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(uiState.issues) { issue ->
                                IssueCard(issue = issue)
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
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(uiState.pullRequests) { pr ->
                                PullRequestCard(
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
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(uiState.workflowRuns) { run ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = run.name.ifBlank { "Workflow #${run.runNumber}" },
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Branch: ${run.headBranch ?: "main"} • Event: ${run.event}",
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
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(uiState.releases) { release ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = release.name ?: release.tagName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Tag: ${release.tagName} • ${if (release.prerelease) "Pre-release" else "Stable"}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        if (!release.body.isNullOrBlank()) {
                                            Spacer(modifier = Modifier.height(8.dp))
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
fun IssueCard(issue: Issue) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "#${issue.number}",
                    style = CodeTypography,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = issue.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                GitForgeStatusBadge(status = issue.state)
            }
            if (!issue.body.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
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
fun PullRequestCard(
    pullRequest: PullRequest,
    onViewDiff: () -> Unit,
    onMerge: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "#${pullRequest.number}",
                    style = CodeTypography,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = pullRequest.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                GitForgeStatusBadge(status = pullRequest.state)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "+${pullRequest.additions} -${pullRequest.deletions}",
                    style = CodeTypography,
                    color = ForgeEmerald
                )
                Spacer(modifier = Modifier.weight(1f))
                OutlinedButton(
                    onClick = onViewDiff,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("View Diff", fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                if (pullRequest.state == "open") {
                    Button(
                        onClick = onMerge,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("Merge", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
