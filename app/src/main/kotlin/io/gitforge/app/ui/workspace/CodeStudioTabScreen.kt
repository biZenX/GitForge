package io.gitforge.app.ui.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.gitforge.core.designsystem.component.GitForgeEmptyView
import io.gitforge.core.designsystem.component.GitForgeLoadingView
import io.gitforge.core.designsystem.theme.CodeTypography
import io.gitforge.core.designsystem.theme.ForgeEmerald
import io.gitforge.core.designsystem.theme.ForgeIndigo
import io.gitforge.core.model.LocalFile
import io.gitforge.feature.editor.CodeStudioScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeStudioTabScreen(
    viewModel: WorkspaceViewModel,
    onNavigateToTerminal: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showNewWorkspaceDialog by remember { mutableStateOf(false) }
    var showCloneDialog by remember { mutableStateOf(false) }
    var showCommitDialog by remember { mutableStateOf(false) }

    var newWsName by remember { mutableStateOf("") }
    var cloneUrl by remember { mutableStateOf("") }
    var cloneWsName by remember { mutableStateOf("") }
    var commitMessage by remember { mutableStateOf("") }

    // If active editing file is open, show the CodeStudioScreen editor
    if (uiState.activeEditingFile != null && uiState.activeFileContent != null) {
        CodeStudioScreen(
            fileName = uiState.activeEditingFile?.name ?: "Editor",
            initialContent = uiState.activeFileContent ?: "",
            onSaveContent = { newText ->
                viewModel.saveLocalFile(newText)
            },
            onClose = { viewModel.closeActiveFile() }
        )
        return
    }

    if (showNewWorkspaceDialog) {
        AlertDialog(
            onDismissRequest = { showNewWorkspaceDialog = false },
            title = { Text("New Local Workspace") },
            text = {
                OutlinedTextField(
                    value = newWsName,
                    onValueChange = { newWsName = it },
                    label = { Text("Workspace Name") },
                    placeholder = { Text("e.g. MyAndroidProject") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newWsName.isNotBlank()) {
                            viewModel.createWorkspace(newWsName)
                            newWsName = ""
                            showNewWorkspaceDialog = false
                        }
                    }
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewWorkspaceDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showCloneDialog) {
        AlertDialog(
            onDismissRequest = { showCloneDialog = false },
            title = { Text("Clone GitHub Repository") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = cloneUrl,
                        onValueChange = { cloneUrl = it },
                        label = { Text("Repository HTTPS URL") },
                        placeholder = { Text("https://github.com/owner/repo.git") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = cloneWsName,
                        onValueChange = { cloneWsName = it },
                        label = { Text("Workspace Name") },
                        placeholder = { Text("e.g. repo-clone") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (cloneUrl.isNotBlank() && cloneWsName.isNotBlank()) {
                            viewModel.cloneRepository(cloneUrl.trim(), cloneWsName.trim())
                            cloneUrl = ""
                            cloneWsName = ""
                            showCloneDialog = false
                        }
                    }
                ) {
                    Text("Clone")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCloneDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showCommitDialog) {
        AlertDialog(
            onDismissRequest = { showCommitDialog = false },
            title = { Text("Commit Workspace Changes") },
            text = {
                OutlinedTextField(
                    value = commitMessage,
                    onValueChange = { commitMessage = it },
                    label = { Text("Commit Message") },
                    placeholder = { Text("e.g. Refactor architecture") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (commitMessage.isNotBlank()) {
                            viewModel.stageFile(".")
                            viewModel.commitChanges(commitMessage, "GitForge User", "dev@gitforge.app")
                            commitMessage = ""
                            showCommitDialog = false
                        }
                    }
                ) {
                    Text("Commit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCommitDialog = false }) { Text("Cancel") }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Workspace Toolbar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = uiState.selectedWorkspace?.name ?: "Code Studio Workspace",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                if (uiState.selectedWorkspace != null) {
                    Text(
                        text = "Branch: ${uiState.gitStatus?.branch ?: "main"} • ${if (uiState.gitStatus?.isClean == true) "Clean" else "Modified"}",
                        style = CodeTypography,
                        color = if (uiState.gitStatus?.isClean == true) ForgeEmerald else MaterialTheme.colorScheme.primary
                    )
                }
            }

            IconButton(onClick = onNavigateToTerminal) {
                Icon(Icons.Default.Terminal, contentDescription = "Open Terminal", tint = ForgeEmerald)
            }

            IconButton(onClick = { showCloneDialog = true }) {
                Icon(Icons.Default.Download, contentDescription = "Clone Repo", tint = ForgeIndigo)
            }

            IconButton(onClick = { showNewWorkspaceDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "New Workspace")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (uiState.isLoading) {
            GitForgeLoadingView(message = "Processing workspace...")
            return@Column
        }

        if (uiState.workspaces.isEmpty()) {
            GitForgeEmptyView(
                title = "No Workspaces Created",
                description = "Create a workspace or clone a repository from GitHub to start local development.",
                actionLabel = "Create Workspace",
                onActionClick = { showNewWorkspaceDialog = true },
                icon = Icons.Outlined.Folder
            )
            return@Column
        }

        // Workspace Selector Row if multiple workspaces
        if (uiState.workspaces.size > 1) {
            PrimaryScrollableTabRow(
                selectedTabIndex = uiState.workspaces.indexOfFirst { it.id == uiState.selectedWorkspace?.id }.coerceAtLeast(0),
                edgePadding = 0.dp
            ) {
                uiState.workspaces.forEach { ws ->
                    Tab(
                        selected = ws.id == uiState.selectedWorkspace?.id,
                        onClick = { viewModel.selectWorkspace(ws) },
                        text = { Text(ws.name) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Git Working Tree Status Bar
        if (uiState.gitStatus != null && uiState.gitStatus?.isClean == false) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Uncommitted changes: ${uiState.gitStatus?.modified?.size ?: 0} modified, ${uiState.gitStatus?.untracked?.size ?: 0} untracked",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = { showCommitDialog = true },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("Commit")
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // File Browser in Workspace
        Text(
            text = "Files in Workspace",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (uiState.files.isEmpty()) {
            GitForgeEmptyView(
                title = "Workspace is Empty",
                description = "Add files or clone a project to start coding.",
                icon = Icons.Outlined.Description
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(uiState.files) { file ->
                    LocalFileItem(
                        file = file,
                        onClick = {
                            if (file.isDirectory) {
                                viewModel.navigateToSubPath(file.path)
                            } else {
                                viewModel.openLocalFile(file)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LocalFileItem(
    file: LocalFile,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF1E2128),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x14FFFFFF))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (file.isDirectory) Color(0x22A8C7FA) else Color(0x14FFFFFF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (file.isDirectory) Icons.Default.Folder else Icons.Outlined.Description,
                    contentDescription = null,
                    tint = if (file.isDirectory) Color(0xFFA8C7FA) else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = file.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (file.isDirectory) FontWeight.SemiBold else FontWeight.Normal,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface
            )
            if (!file.isDirectory && file.size > 0) {
                Text(
                    text = "${file.size} B",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
