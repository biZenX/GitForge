package io.gitforge.app.ui.repos

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.ForkRight
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import io.gitforge.core.designsystem.component.GitForgeEmptyView
import io.gitforge.core.designsystem.component.GitForgeErrorView
import io.gitforge.core.designsystem.component.GitForgeLoadingView
import io.gitforge.core.designsystem.theme.CodeTypography
import io.gitforge.core.designsystem.theme.CyanAccentDark
import io.gitforge.core.designsystem.theme.EmeraldAccentDark
import io.gitforge.core.designsystem.theme.HeroCardShape
import io.gitforge.core.designsystem.theme.IconContainerShape
import io.gitforge.core.designsystem.theme.ItemCardShape
import io.gitforge.core.designsystem.theme.PillBadgeShape
import io.gitforge.core.designsystem.theme.SubCardShape
import io.gitforge.core.designsystem.theme.TerracottaPrimaryDark
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
                title = {
                    Column {
                        Text(text = repo, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text(text = owner, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                    // Archive Download Action & Progress
                    if (uiState.isDownloadingArchive) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = { uiState.downloadProgress / 100f },
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = EmeraldAccentDark
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${uiState.downloadProgress}%",
                                style = CodeTypography,
                                fontSize = 11.sp
                            )
                            IconButton(onClick = { viewModel.cancelDownload() }) {
                                Icon(Icons.Default.Cancel, contentDescription = "Cancel Download", modifier = Modifier.size(18.dp))
                            }
                        }
                    } else {
                        IconButton(onClick = { viewModel.downloadArchive() }) {
                            Icon(Icons.Default.Download, contentDescription = "Download ZIP Archive", tint = TerracottaPrimaryDark)
                        }
                    }

                    // Branch Selector Pill
                    Box {
                        OutlinedButton(
                            onClick = { branchMenuExpanded = true },
                            shape = PillBadgeShape,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.ForkRight, contentDescription = null, modifier = Modifier.size(16.dp), tint = TerracottaPrimaryDark)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = uiState.currentBranch, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
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
            PrimaryTabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Files", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Outlined.Folder, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Commits", fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.History, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("README", fontWeight = FontWeight.SemiBold) },
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
                                    Text("root", color = TerracottaPrimaryDark)
                                }
                                Text("/", color = MaterialTheme.colorScheme.outline)
                                val parent = uiState.currentPath.substringBeforeLast("/", "")
                                TextButton(onClick = { viewModel.navigateToDirectory(parent) }) {
                                    Text(".. (back)", color = TerracottaPrimaryDark)
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
                1 -> {
                    // Commits Tab
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.commits) { commit ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = ItemCardShape,
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = commit.commit.message.lines().firstOrNull() ?: "",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .clip(PillBadgeShape)
                                                .background(TerracottaPrimaryDark.copy(alpha = 0.12f))
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = commit.commit.author?.name ?: "Author",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TerracottaPrimaryDark,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(PillBadgeShape)
                                                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
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
                }
                2 -> {
                    // README Tab
                    if (!uiState.readmeContent.isNullOrBlank()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            item {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = HeroCardShape,
                                    color = MaterialTheme.colorScheme.surfaceContainer,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                                ) {
                                    Column(modifier = Modifier.padding(20.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Outlined.Description,
                                                contentDescription = null,
                                                tint = TerracottaPrimaryDark,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "README.md",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(14.dp))
                                        Text(
                                            text = uiState.readmeContent ?: "",
                                            style = CodeTypography,
                                            color = MaterialTheme.colorScheme.onSurface
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
            }
        }
    }
}

@Composable
fun WafeerFileNodeItem(
    fileNode: FileNode,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SubCardShape)
            .clickable(onClick = onClick),
        shape = SubCardShape,
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(IconContainerShape)
                    .background(
                        if (fileNode.nodeType == FileNodeType.DIR)
                            TerracottaPrimaryDark.copy(alpha = 0.15f)
                        else
                            CyanAccentDark.copy(alpha = 0.12f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (fileNode.nodeType == FileNodeType.DIR) Icons.Default.Folder else Icons.Outlined.Description,
                    contentDescription = null,
                    tint = if (fileNode.nodeType == FileNodeType.DIR) TerracottaPrimaryDark else CyanAccentDark,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = fileNode.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (fileNode.nodeType == FileNodeType.DIR) FontWeight.Bold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            if (fileNode.nodeType == FileNodeType.FILE && fileNode.size > 0) {
                Box(
                    modifier = Modifier
                        .clip(PillBadgeShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${fileNode.size / 1024} KB",
                        style = CodeTypography,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
