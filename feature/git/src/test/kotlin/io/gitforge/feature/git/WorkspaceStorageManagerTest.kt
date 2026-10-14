package io.gitforge.feature.git

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File

@RunWith(JUnit4::class)
class WorkspaceStorageManagerTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var storageManager: WorkspaceStorageManager

    @Before
    fun setup() {
        storageManager = WorkspaceStorageManager(customRootDir = tempFolder.root)
    }

    @Test
    fun `createWorkspace creates folder and returns valid Workspace model`() = runBlocking {
        val result = storageManager.createWorkspace("MyTestApp", "https://github.com/test/repo.git")

        assertThat(result.isSuccess).isTrue()
        val ws = result.getOrThrow()
        assertThat(ws.name).isEqualTo("MyTestApp")
        assertThat(ws.remoteUrl).isEqualTo("https://github.com/test/repo.git")
        assertThat(File(ws.path).exists()).isTrue()
    }

    @Test
    fun `save and read file content works accurately`() = runBlocking {
        val ws = storageManager.createWorkspace("FileTestWs").getOrThrow()
        val wsDir = File(ws.path)

        val sampleText = "fun main() {\n    println(\"Hello GitForge\")\n}"
        val saveResult = storageManager.saveFileContent(wsDir, "src/Main.kt", sampleText)
        assertThat(saveResult.isSuccess).isTrue()

        val readResult = storageManager.readFileContent(wsDir, "src/Main.kt")
        assertThat(readResult.isSuccess).isTrue()
        assertThat(readResult.getOrThrow()).isEqualTo(sampleText)
    }

    @Test
    fun `listFiles accurately lists directory entries`() = runBlocking {
        val ws = storageManager.createWorkspace("ListTestWs").getOrThrow()
        val wsDir = File(ws.path)

        storageManager.saveFileContent(wsDir, "README.md", "# Readme")
        storageManager.saveFileContent(wsDir, "build.gradle.kts", "// build script")

        val listResult = storageManager.listFiles(wsDir)
        assertThat(listResult.isSuccess).isTrue()
        val names = listResult.getOrThrow().map { it.name }
        assertThat(names).contains("README.md")
        assertThat(names).contains("build.gradle.kts")
    }

    @Test
    fun `path traversal attempts are caught and rejected`() = runBlocking {
        val ws = storageManager.createWorkspace("SecurityTestWs").getOrThrow()
        val wsDir = File(ws.path)

        val readAttempt = storageManager.readFileContent(wsDir, "../../../etc/shadow")
        assertThat(readAttempt.isFailure).isTrue()
        assertThat(readAttempt.exceptionOrNull()).isInstanceOf(SecurityException::class.java)

        val saveAttempt = storageManager.saveFileContent(wsDir, "../../evil.sh", "malicious")
        assertThat(saveAttempt.isFailure).isTrue()
        assertThat(saveAttempt.exceptionOrNull()).isInstanceOf(SecurityException::class.java)
    }
}
