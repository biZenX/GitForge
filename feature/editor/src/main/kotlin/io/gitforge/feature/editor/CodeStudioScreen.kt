package io.gitforge.feature.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Difference
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.gitforge.core.designsystem.component.GitForgeConfirmationDialog
import io.gitforge.core.designsystem.component.GitForgeDiffViewer
import io.gitforge.core.designsystem.theme.CodeTypography
import io.gitforge.core.designsystem.theme.ForgeEmerald
import io.gitforge.core.designsystem.theme.ForgeIndigo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeStudioScreen(
    fileName: String,
    initialContent: String,
    onSaveContent: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var content by remember(initialContent) { mutableStateOf(initialContent) }
    val originalContent = remember(initialContent) { initialContent }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Editor, 1: Diff Preview
    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showDiscardDialog by remember { mutableStateOf(false) }

    val hasUnsavedChanges = content != originalContent
    val lineCount = remember(content) { content.lines().size.coerceAtLeast(1) }

    val horizontalScroll = rememberScrollState()
    val verticalScroll = rememberScrollState()

    if (showDiscardDialog) {
        GitForgeConfirmationDialog(
            title = "Discard Changes?",
            message = "You have unsaved changes in '$fileName'. Discarding will revert to the last saved version.",
            confirmLabel = "Discard",
            isDestructive = true,
            onConfirm = {
                content = originalContent
                showDiscardDialog = false
            },
            onDismiss = { showDiscardDialog = false }
        )
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Studio Header
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = fileName + if (hasUnsavedChanges) " •" else "",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (hasUnsavedChanges) ForgeEmerald else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isSearching = !isSearching }) {
                        Icon(Icons.Default.Search, contentDescription = "Search in file")
                    }
                    if (hasUnsavedChanges) {
                        IconButton(onClick = { showDiscardDialog = true }) {
                            Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Discard changes")
                        }
                    }
                    IconButton(
                        onClick = { onSaveContent(content) },
                        enabled = hasUnsavedChanges
                    ) {
                        Icon(
                            Icons.Default.Save,
                            contentDescription = "Save file",
                            tint = if (hasUnsavedChanges) ForgeEmerald else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                    }
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close editor")
                    }
                }
            )

            // Search Bar in File
            if (isSearching) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Find in file...", style = CodeTypography) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        textStyle = CodeTypography
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    val matchCount = remember(content, searchQuery) {
                        if (searchQuery.isBlank()) 0
                        else Regex.escape(searchQuery).toRegex(RegexOption.IGNORE_CASE).findAll(content).count()
                    }
                    Text(
                        text = "$matchCount matches",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Tab bar: Editor vs Diff
            PrimaryTabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Editor ($lineCount lines)") },
                    icon = { Icon(Icons.Default.Edit, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Diff Preview" + if (hasUnsavedChanges) " (Unsaved)" else "") },
                    icon = { Icon(Icons.Default.Difference, contentDescription = null) }
                )
            }

            when (selectedTab) {
                0 -> {
                    // Editor with line numbers
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .background(Color(0xFF0F172A)) // Dark editor background
                    ) {
                        // Line numbers gutter
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(48.dp)
                                .verticalScroll(verticalScroll)
                                .background(Color(0xFF090D16))
                                .padding(vertical = 12.dp)
                        ) {
                            for (i in 1..lineCount) {
                                Text(
                                    text = "$i",
                                    style = CodeTypography,
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B),
                                    textAlign = TextAlign.End,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(end = 8.dp)
                                )
                            }
                        }

                        // Code area
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .verticalScroll(verticalScroll)
                                .horizontalScroll(horizontalScroll)
                                .padding(12.dp)
                        ) {
                            BasicTextField(
                                value = content,
                                onValueChange = { content = it },
                                textStyle = CodeTypography.copy(color = Color(0xFFE2E8F0)),
                                cursorBrush = SolidColor(ForgeEmerald),
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
                1 -> {
                    // Diff Viewer
                    val simpleDiff = remember(content, originalContent) {
                        generateSimpleUnifiedDiff(fileName, originalContent, content)
                    }
                    GitForgeDiffViewer(
                        patch = simpleDiff,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

private fun generateSimpleUnifiedDiff(fileName: String, oldText: String, newText: String): String {
    val oldLines = oldText.lines()
    val newLines = newText.lines()
    val sb = StringBuilder()
    sb.append("--- a/$fileName\n")
    sb.append("+++ b/$fileName\n")
    sb.append("@@ -1,${oldLines.size} +1,${newLines.size} @@\n")

    val max = maxOf(oldLines.size, newLines.size)
    for (i in 0 until max) {
        val o = oldLines.getOrNull(i)
        val n = newLines.getOrNull(i)
        if (o == n && o != null) {
            sb.append(" $o\n")
        } else {
            if (o != null) sb.append("-$o\n")
            if (n != null) sb.append("+$n\n")
        }
    }
    return sb.toString()
}
