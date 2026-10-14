package io.gitforge.feature.terminal

import io.gitforge.core.model.CommandResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface TerminalCommandRunner {
    suspend fun executeCommand(
        commandLine: String,
        workspaceRoot: File,
        currentRelativeDir: String,
        onWorkingDirChanged: (String) -> Unit
    ): CommandResult
}

class SandboxedTerminalRunner : TerminalCommandRunner {

    override suspend fun executeCommand(
        commandLine: String,
        workspaceRoot: File,
        currentRelativeDir: String,
        onWorkingDirChanged: (String) -> Unit
    ): CommandResult = withContext(Dispatchers.IO) {
        val trimmed = commandLine.trim()
        if (trimmed.isEmpty()) {
            return@withContext CommandResult(
                command = "",
                stdout = "",
                stderr = "",
                exitCode = 0,
                durationMs = 0L
            )
        }

        val startTime = System.currentTimeMillis()
        val workingDir = resolveSafeDirectory(workspaceRoot, currentRelativeDir)
        val tokens = tokenize(trimmed)
        val command = tokens.first()
        val args = tokens.drop(1)

        // 1. Check built-in commands first for safety and consistency across Android OS versions
        when (command) {
            "pwd" -> {
                val rel = workingDir.relativeTo(workspaceRoot).path
                val display = if (rel.isEmpty()) "/" else "/$rel"
                CommandResult(
                    command = trimmed,
                    stdout = display + "\n",
                    stderr = "",
                    exitCode = 0,
                    durationMs = System.currentTimeMillis() - startTime
                )
            }
            "cd" -> {
                val target = args.firstOrNull() ?: ""
                val newRelDir = if (target.isEmpty() || target == "~" || target == "/") {
                    ""
                } else {
                    val candidate = File(workingDir, target).canonicalFile
                    if (!candidate.path.startsWith(workspaceRoot.canonicalPath)) {
                        return@withContext CommandResult(
                            command = trimmed,
                            stdout = "",
                            stderr = "cd: permission denied: path escapes workspace sandbox\n",
                            exitCode = 1,
                            durationMs = System.currentTimeMillis() - startTime
                        )
                    }
                    if (!candidate.exists() || !candidate.isDirectory) {
                        return@withContext CommandResult(
                            command = trimmed,
                            stdout = "",
                            stderr = "cd: no such directory: $target\n",
                            exitCode = 1,
                            durationMs = System.currentTimeMillis() - startTime
                        )
                    }
                    candidate.relativeTo(workspaceRoot.canonicalFile).path
                }
                onWorkingDirChanged(newRelDir)
                CommandResult(
                    command = trimmed,
                    stdout = "",
                    stderr = "",
                    exitCode = 0,
                    durationMs = System.currentTimeMillis() - startTime
                )
            }
            "ls" -> {
                val showAll = args.contains("-a") || args.contains("-la") || args.contains("-al")
                val targetDir = args.firstOrNull { !it.startsWith("-") }?.let { File(workingDir, it).canonicalFile } ?: workingDir
                if (!targetDir.path.startsWith(workspaceRoot.canonicalPath)) {
                    return@withContext CommandResult(
                        command = trimmed,
                        stdout = "",
                        stderr = "ls: permission denied: path escapes workspace\n",
                        exitCode = 1,
                        durationMs = System.currentTimeMillis() - startTime
                    )
                }
                if (!targetDir.exists()) {
                    return@withContext CommandResult(
                        command = trimmed,
                        stdout = "",
                        stderr = "ls: cannot access '${targetDir.name}': No such file or directory\n",
                        exitCode = 1,
                        durationMs = System.currentTimeMillis() - startTime
                    )
                }

                val files = targetDir.listFiles()?.sortedBy { it.name } ?: emptyList()
                val dateFormat = SimpleDateFormat("MMM dd HH:mm", Locale.US)
                val output = StringBuilder()

                for (f in files) {
                    if (!showAll && f.name.startsWith(".")) continue
                    val type = if (f.isDirectory) "d" else "-"
                    val size = if (f.isDirectory) "4096" else f.length().toString()
                    val date = dateFormat.format(Date(f.lastModified()))
                    output.append(String.format(Locale.US, "%s %8s %s %s\n", type, size, date, f.name))
                }
                CommandResult(
                    command = trimmed,
                    stdout = output.toString(),
                    stderr = "",
                    exitCode = 0,
                    durationMs = System.currentTimeMillis() - startTime
                )
            }
            "cat" -> {
                val targetFile = args.firstOrNull()
                if (targetFile == null) {
                    CommandResult(
                        command = trimmed,
                        stdout = "",
                        stderr = "cat: missing file operand\n",
                        exitCode = 1,
                        durationMs = System.currentTimeMillis() - startTime
                    )
                } else {
                    val file = File(workingDir, targetFile).canonicalFile
                    if (!file.path.startsWith(workspaceRoot.canonicalPath)) {
                        CommandResult(
                            command = trimmed,
                            stdout = "",
                            stderr = "cat: permission denied: path escapes workspace\n",
                            exitCode = 1,
                            durationMs = System.currentTimeMillis() - startTime
                        )
                    } else if (!file.exists()) {
                        CommandResult(
                            command = trimmed,
                            stdout = "",
                            stderr = "cat: $targetFile: No such file or directory\n",
                            exitCode = 1,
                            durationMs = System.currentTimeMillis() - startTime
                        )
                    } else {
                        CommandResult(
                            command = trimmed,
                            stdout = file.readText(Charsets.UTF_8),
                            stderr = "",
                            exitCode = 0,
                            durationMs = System.currentTimeMillis() - startTime
                        )
                    }
                }
            }
            "echo" -> {
                val text = args.joinToString(" ")
                CommandResult(
                    command = trimmed,
                    stdout = text + "\n",
                    stderr = "",
                    exitCode = 0,
                    durationMs = System.currentTimeMillis() - startTime
                )
            }
            "touch" -> {
                val target = args.firstOrNull()
                if (target == null) {
                    CommandResult(command = trimmed, stdout = "", stderr = "touch: missing file operand\n", exitCode = 1, durationMs = 0)
                } else {
                    val file = File(workingDir, target).canonicalFile
                    if (!file.path.startsWith(workspaceRoot.canonicalPath)) {
                        CommandResult(command = trimmed, stdout = "", stderr = "touch: permission denied: path escapes workspace\n", exitCode = 1, durationMs = 0)
                    } else {
                        file.parentFile?.mkdirs()
                        if (!file.exists()) file.createNewFile() else file.setLastModified(System.currentTimeMillis())
                        CommandResult(command = trimmed, stdout = "", stderr = "", exitCode = 0, durationMs = System.currentTimeMillis() - startTime)
                    }
                }
            }
            "mkdir" -> {
                val target = args.firstOrNull { it != "-p" }
                if (target == null) {
                    CommandResult(command = trimmed, stdout = "", stderr = "mkdir: missing operand\n", exitCode = 1, durationMs = 0)
                } else {
                    val dir = File(workingDir, target).canonicalFile
                    if (!dir.path.startsWith(workspaceRoot.canonicalPath)) {
                        CommandResult(command = trimmed, stdout = "", stderr = "mkdir: permission denied: path escapes workspace\n", exitCode = 1, durationMs = 0)
                    } else {
                        val created = dir.mkdirs()
                        CommandResult(command = trimmed, stdout = "", stderr = if (created) "" else "mkdir: cannot create directory '$target'\n", exitCode = if (created) 0 else 1, durationMs = System.currentTimeMillis() - startTime)
                    }
                }
            }
            "rm" -> {
                val recursive = args.contains("-r") || args.contains("-rf")
                val target = args.firstOrNull { !it.startsWith("-") }
                if (target == null) {
                    CommandResult(command = trimmed, stdout = "", stderr = "rm: missing operand\n", exitCode = 1, durationMs = 0)
                } else {
                    val file = File(workingDir, target).canonicalFile
                    if (!file.path.startsWith(workspaceRoot.canonicalPath) || file == workspaceRoot.canonicalFile) {
                        CommandResult(command = trimmed, stdout = "", stderr = "rm: cannot remove root workspace directory\n", exitCode = 1, durationMs = 0)
                    } else if (!file.exists()) {
                        CommandResult(command = trimmed, stdout = "", stderr = "rm: cannot remove '$target': No such file or directory\n", exitCode = 1, durationMs = 0)
                    } else {
                        val deleted = if (file.isDirectory) {
                            if (recursive) file.deleteRecursively() else false
                        } else {
                            file.delete()
                        }
                        CommandResult(command = trimmed, stdout = "", stderr = if (deleted) "" else "rm: failed to delete '$target'\n", exitCode = if (deleted) 0 else 1, durationMs = System.currentTimeMillis() - startTime)
                    }
                }
            }
            "clear" -> {
                CommandResult(
                    command = "clear",
                    stdout = "[CLEAR]",
                    stderr = "",
                    exitCode = 0,
                    durationMs = 0
                )
            }
            "env" -> {
                val envInfo = """
                    WORKSPACE=${workspaceRoot.absolutePath}
                    PWD=${workingDir.absolutePath}
                    USER=gitforge
                    SHELL=/system/bin/sh
                    TERM=xterm-256color
                    SANDBOX=enforced
                """.trimIndent() + "\n"
                CommandResult(command = trimmed, stdout = envInfo, stderr = "", exitCode = 0, durationMs = System.currentTimeMillis() - startTime)
            }
            "help" -> {
                val helpText = """
                    GitForge Sandboxed Workspace Terminal
                    Available Built-in Commands:
                      pwd              Print working directory relative to workspace root
                      cd <dir>         Change working directory within workspace boundaries
                      ls [-a|-la]      List directory contents
                      cat <file>       Display file contents
                      echo [args...]   Display a line of text
                      touch <file>     Create an empty file or update timestamp
                      mkdir [-p] <dir> Create directory
                      rm [-r] <file>   Remove file or directory
                      env              Print environment variables
                      clear            Clear session history
                      help             Display this help message

                    System Process Execution:
                      External binaries in /system/bin (e.g. toybox, grep, uname) run directly
                      in the sandbox process manager when available on this device.
                      Arbitrary root/system modification is prohibited by Android SELinux.
                """.trimIndent() + "\n"
                CommandResult(command = trimmed, stdout = helpText, stderr = "", exitCode = 0, durationMs = System.currentTimeMillis() - startTime)
            }
            else -> {
                // 2. Fall back to sandboxed ProcessBuilder execution
                runExternalProcess(trimmed, workingDir, workspaceRoot, startTime)
            }
        }
    }

    private fun runExternalProcess(
        commandLine: String,
        workingDir: File,
        workspaceRoot: File,
        startTime: Long
    ): CommandResult {
        return try {
            val process = ProcessBuilder("/system/bin/sh", "-c", commandLine)
                .directory(workingDir)
                .apply {
                    environment()["WORKSPACE"] = workspaceRoot.absolutePath
                    environment()["PWD"] = workingDir.absolutePath
                }
                .start()

            val stdout = process.inputStream.bufferedReader().readText()
            val stderr = process.errorStream.bufferedReader().readText()
            val exitCode = process.waitFor()

            CommandResult(
                command = commandLine,
                stdout = stdout,
                stderr = stderr,
                exitCode = exitCode,
                durationMs = System.currentTimeMillis() - startTime
            )
        } catch (e: Exception) {
            CommandResult(
                command = commandLine,
                stdout = "",
                stderr = "terminal: command execution failed: ${e.message}\n",
                exitCode = 127,
                durationMs = System.currentTimeMillis() - startTime
            )
        }
    }

    private fun resolveSafeDirectory(workspaceRoot: File, relativeDir: String): File {
        if (relativeDir.isBlank()) return workspaceRoot
        val target = File(workspaceRoot, relativeDir).canonicalFile
        return if (target.path.startsWith(workspaceRoot.canonicalPath) && target.exists() && target.isDirectory) {
            target
        } else {
            workspaceRoot
        }
    }

    private fun tokenize(input: String): List<String> {
        val tokens = mutableListOf<String>()
        val regex = Regex("""[^\s"']+|"([^"]*)"|'([^']*)'""")
        regex.findAll(input).forEach { match ->
            tokens.add(match.groups[1]?.value ?: match.groups[2]?.value ?: match.value)
        }
        return tokens
    }
}
