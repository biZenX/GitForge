package io.gitforge.feature.terminal

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TerminalRunnerTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var runner: SandboxedTerminalRunner

    @Before
    fun setup() {
        runner = SandboxedTerminalRunner()
    }

    @Test
    fun `pwd returns workspace root initially`() = runBlocking {
        val root = tempFolder.root
        var currentDir = ""

        val result = runner.executeCommand(
            commandLine = "pwd",
            workspaceRoot = root,
            currentRelativeDir = currentDir,
            onWorkingDirChanged = { currentDir = it }
        )

        assertThat(result.exitCode).isEqualTo(0)
        assertThat(result.stdout).isEqualTo("/\n")
    }

    @Test
    fun `echo returns text argument`() = runBlocking {
        val root = tempFolder.root
        var currentDir = ""

        val result = runner.executeCommand(
            commandLine = "echo Hello GitForge",
            workspaceRoot = root,
            currentRelativeDir = currentDir,
            onWorkingDirChanged = { currentDir = it }
        )

        assertThat(result.exitCode).isEqualTo(0)
        assertThat(result.stdout).isEqualTo("Hello GitForge\n")
    }

    @Test
    fun `mkdir touch and ls create and list files`() = runBlocking {
        val root = tempFolder.root
        var currentDir = ""

        // 1. mkdir
        val mkdirResult = runner.executeCommand("mkdir src", root, currentDir) { currentDir = it }
        assertThat(mkdirResult.exitCode).isEqualTo(0)

        // 2. touch
        val touchResult = runner.executeCommand("touch src/Main.kt", root, currentDir) { currentDir = it }
        assertThat(touchResult.exitCode).isEqualTo(0)

        // 3. ls
        val lsResult = runner.executeCommand("ls src", root, currentDir) { currentDir = it }
        assertThat(lsResult.exitCode).isEqualTo(0)
        assertThat(lsResult.stdout).contains("Main.kt")
    }

    @Test
    fun `cd escapes are blocked by sandbox`() = runBlocking {
        val root = tempFolder.root
        var currentDir = ""

        val result = runner.executeCommand(
            commandLine = "cd ../../../etc",
            workspaceRoot = root,
            currentRelativeDir = currentDir,
            onWorkingDirChanged = { currentDir = it }
        )

        assertThat(result.exitCode).isEqualTo(1)
        assertThat(result.stderr).contains("permission denied")
        assertThat(currentDir).isEqualTo("") // Did not change
    }

    @Test
    fun `help prints available commands`() = runBlocking {
        val root = tempFolder.root
        val result = runner.executeCommand("help", root, "") {}

        assertThat(result.exitCode).isEqualTo(0)
        assertThat(result.stdout).contains("GitForge Sandboxed Workspace Terminal")
        assertThat(result.stdout).contains("Available Built-in Commands:")
    }
}
