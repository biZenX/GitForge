package io.gitforge.feature.terminal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.gitforge.core.designsystem.theme.CodeTypography
import io.gitforge.core.designsystem.theme.ForgeEmerald
import io.gitforge.core.designsystem.theme.ForgeRose
import io.gitforge.core.model.CommandResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun TerminalScreen(
    workspaceRoot: File,
    runner: TerminalCommandRunner = remember { SandboxedTerminalRunner() },
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var currentRelDir by remember { mutableStateOf("") }
    var inputCommand by remember { mutableStateOf("") }
    var isRunning by remember { mutableStateOf(false) }
    var runningJob by remember { mutableStateOf<Job?>(null) }
    val history = remember { mutableStateListOf<CommandResult>() }
    val listState = rememberLazyListState()

    fun runCommand(cmd: String) {
        val trimmed = cmd.trim()
        if (trimmed.isEmpty()) return

        inputCommand = ""
        isRunning = true

        val job = coroutineScope.launch {
            val result = runner.executeCommand(
                commandLine = trimmed,
                workspaceRoot = workspaceRoot,
                currentRelativeDir = currentRelDir,
                onWorkingDirChanged = { newDir -> currentRelDir = newDir }
            )
            if (result.stdout == "[CLEAR]") {
                history.clear()
            } else {
                history.add(result)
            }
            isRunning = false
            runningJob = null
        }
        runningJob = job
    }

    LaunchedEffect(history.size) {
        if (history.isNotEmpty()) {
            listState.animateScrollToItem(history.size - 1)
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFF0F172A) // Rich Terminal Dark Background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            // Header Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = null,
                    tint = ForgeEmerald,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "GitForge Workspace Terminal",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1f))
                val dirDisplay = if (currentRelDir.isEmpty()) "~" else "~/$currentRelDir"
                Text(
                    text = dirDisplay,
                    style = CodeTypography,
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            }

            // Command Output History
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFF090D16), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                if (history.isEmpty()) {
                    item {
                        Text(
                            text = "GitForge Terminal Session Initialized.\nType 'help' for available commands and sandbox details.",
                            style = CodeTypography,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                items(history) { entry ->
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        // Prompt and Command
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "➜ ",
                                color = ForgeEmerald,
                                style = CodeTypography
                            )
                            Text(
                                text = entry.command,
                                color = Color.White,
                                style = CodeTypography
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            // Exit status badge
                            val badgeColor = if (entry.exitCode == 0) ForgeEmerald else ForgeRose
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(badgeColor.copy(alpha = 0.2f))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${entry.exitCode} (${entry.durationMs}ms)",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = badgeColor
                                )
                            }
                        }

                        // Stdout
                        if (entry.stdout.isNotBlank()) {
                            Text(
                                text = entry.stdout,
                                color = Color(0xFFE2E8F0),
                                style = CodeTypography,
                                modifier = Modifier.padding(start = 12.dp, top = 2.dp)
                            )
                        }

                        // Stderr
                        if (entry.stderr.isNotBlank()) {
                            Text(
                                text = entry.stderr,
                                color = ForgeRose,
                                style = CodeTypography,
                                modifier = Modifier.padding(start = 12.dp, top = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Command Input Line
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = inputCommand,
                    onValueChange = { inputCommand = it },
                    placeholder = {
                        Text(
                            text = if (isRunning) "Command running..." else "Enter command (e.g. ls, pwd, git status)...",
                            color = Color(0xFF64748B),
                            style = CodeTypography
                        )
                    },
                    modifier = Modifier.weight(1f),
                    textStyle = CodeTypography.copy(color = Color.White),
                    singleLine = true,
                    enabled = !isRunning,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForgeEmerald,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedContainerColor = Color(0xFF090D16),
                        unfocusedContainerColor = Color(0xFF090D16)
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { runCommand(inputCommand) })
                )

                Spacer(modifier = Modifier.width(8.dp))

                if (isRunning) {
                    IconButton(
                        onClick = {
                            runningJob?.cancel()
                            isRunning = false
                            history.add(
                                CommandResult(
                                    command = inputCommand,
                                    stdout = "",
                                    stderr = "^C (Process aborted by user)\n",
                                    exitCode = 130,
                                    durationMs = 0
                                )
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = "Cancel",
                            tint = ForgeRose
                        )
                    }
                } else {
                    IconButton(
                        onClick = { runCommand(inputCommand) },
                        enabled = inputCommand.isNotBlank()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = if (inputCommand.isNotBlank()) ForgeEmerald else Color(0xFF475569)
                        )
                    }
                }
            }
        }
    }
}
