package io.gitforge.app.ui.repos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.gitforge.core.designsystem.component.GitForgeDiffViewer
import io.gitforge.core.designsystem.component.GitForgeEmptyView
import io.gitforge.core.designsystem.component.GitForgeErrorView
import io.gitforge.core.designsystem.component.GitForgeLoadingView
import io.gitforge.core.designsystem.theme.CodeTypography
import io.gitforge.core.model.FileNode
import io.gitforge.core.model.FileNodeType
import io.gitforge.feature.editor.CodeStudioScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepoDetailScreen(
    owner: String,
    repo: String,
    viewModel: RepoDetailViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Files, 1: Commits, 2: README
    var branchMenuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(owner, repo) {
        viewModel.loadRepositoryDetails(owner, repo)
    }

    // Active File in Code Studio
    if (uiState.activeViewingFile != null) {
        CodeStudioScreen(
            fileName = uiState.activeViewingFile?.name ?: "File",
            initialContent = uiState.activeFileContent ?: "Loading file...",
            onSaveContent = { newContent ->
                viewModel.saveRemoteFile(newContent, "Update ${uiState.activeViewingFile?.name} via GitForge")
            },
            onClose = { viewModel.closeActiveFile() }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = repo, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Branch Selector
                    Box {
                        OutlinedButton(
                            onClick = { branchMenuExpanded = true },
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.ForkRight, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = uiState.currentBranch, style = MaterialTheme.typography.bodySmall)
                        }
                        DropdownMenu(
                            expanded = branchMenuExpanded,
                            onDismissRequest = { branchMenuExpanded = false }
                        ) {
                            uiState.branches.forEach { branch ->
                                DropdownMenuItem(
                                    text = { Text(branch.name) },
                                    onClick = {
                                        viewModel.selectBranch(branch.name)
                                        branchMenuExpanded = false
                                    }
                                )
                            }
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
            // Tabs: Files, Commits, README
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Files") },
                    icon = { Icon(Icons.Outlined.Folder, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Commits") },
                    icon = { Icon(Icons.Default.History, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("README") },
                    icon = { Icon(Icons.Outlined.Description, contentDescription = null) }
                )
            }

            if (uiState.isLoading && uiState.fileTree.isEmpty()) {
                GitForgeLoadingView(message = "Loading repository data...")
                return@Column
            }

            if (uiState.errorMessage != null && uiState.fileTree.isEmpty()) {
                GitForgeErrorView(
                    message = uiState.errorMessage ?: "Failed to load repository",
                    onRetry = { viewModel.loadRepositoryDetails(owner, repo) }
                )
                return@Column
            }

            when (selectedTab) {
                0 -> {
                    // Files Tab
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Current Path Breadcrumb
                        if (uiState.currentPath.isNotBlank()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = { viewModel.navigateToDirectory("") }) {
                                    Text("root")
                                }
                                Text("/")
                                val parent = uiState.currentPath.substringBeforeLast("/", "")
                                TextButton(onClick = { viewModel.navigateToDirectory(parent) }) {
                                    Text(".. (back)")
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = uiState.currentPath,
                                    style = CodeTypography,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.fileTree) { fileNode ->
                                FileNodeItem(
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
                1 -> {
                    // Commits Tab
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.commits) { commit ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = commit.commit.message.lines().firstOrNull() ?: "",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = commit.commit.author?.name ?: "Author",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = commit.sha.take(7),
                                            style = CodeTypography,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // README Tab
                    if (!uiState.readmeContent.isNullOrBlank()) {
                        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            item {
                                Text(
                                    text = uiState.readmeContent ?: "",
                                    style = CodeTypography,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
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
            }
        }
    }
}

@Composable
fun FileNodeItem(
    fileNode: FileNode,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (fileNode.nodeType == FileNodeType.DIR) Icons.Default.Folder else Icons.Outlined.Description,
                contentDescription = null,
                tint = if (fileNode.nodeType == FileNodeType.DIR) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = fileNode.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (fileNode.nodeType == FileNodeType.DIR) FontWeight.SemiBold else FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )
            if (fileNode.nodeType == FileNodeType.FILE && fileNode.size > 0) {
                Text(
                    text = "${fileNode.size / 1024} KB",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
