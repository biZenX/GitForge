package io.gitforge.app.ui.workspace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.gitforge.core.database.WorkspaceDao
import io.gitforge.core.database.WorkspaceEntity
import io.gitforge.core.model.GitStatusResult
import io.gitforge.core.model.LocalFile
import io.gitforge.core.model.Workspace
import io.gitforge.core.security.TokenStorage
import io.gitforge.feature.git.GitEngine
import io.gitforge.feature.git.WorkspaceStorageManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

data class WorkspaceUiState(
    val isLoading: Boolean = false,
    val workspaces: List<Workspace> = emptyList(),
    val selectedWorkspace: Workspace? = null,
    val currentSubPath: String = "",
    val files: List<LocalFile> = emptyList(),
    val activeEditingFile: LocalFile? = null,
    val activeFileContent: String? = null,
    val gitStatus: GitStatusResult? = null,
    val gitDiff: String? = null,
    val branches: List<String> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class WorkspaceViewModel @Inject constructor(
    private val workspaceDao: WorkspaceDao,
    private val storageManager: WorkspaceStorageManager,
    private val gitEngine: GitEngine,
    private val tokenStorage: TokenStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkspaceUiState())
    val uiState: StateFlow<WorkspaceUiState> = _uiState.asStateFlow()

    init {
        loadWorkspaces()
    }

    fun loadWorkspaces() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            workspaceDao.getAllWorkspaces().collect { entities ->
                val list = entities.map { it.toDomain() }
                _uiState.update { it.copy(isLoading = false, workspaces = list) }
            }
        }
    }

    fun createWorkspace(name: String, remoteUrl: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val res = storageManager.createWorkspace(name, remoteUrl)
            if (res.isSuccess) {
                val ws = res.getOrThrow()
                workspaceDao.insertWorkspace(WorkspaceEntity.fromDomain(ws))
                selectWorkspace(ws)
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Failed to create workspace: ${res.exceptionOrNull()?.message}") }
            }
        }
    }

    fun selectWorkspace(workspace: Workspace) {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedWorkspace = workspace, currentSubPath = "", isLoading = true) }
            loadFiles(File(workspace.path), "")
            refreshGitStatus(File(workspace.path))
        }
    }

    fun navigateToSubPath(subPath: String) {
        val ws = _uiState.value.selectedWorkspace ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(currentSubPath = subPath, isLoading = true) }
            loadFiles(File(ws.path), subPath)
        }
    }

    private suspend fun loadFiles(workspaceDir: File, subPath: String) {
        val res = storageManager.listFiles(workspaceDir, subPath)
        _uiState.update { current ->
            current.copy(
                isLoading = false,
                files = res.getOrDefault(emptyList()),
                errorMessage = res.exceptionOrNull()?.message
            )
        }
    }

    fun openLocalFile(file: LocalFile) {
        val ws = _uiState.value.selectedWorkspace ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, activeEditingFile = file) }
            val res = storageManager.readFileContent(File(ws.path), file.path)
            _uiState.update { current ->
                current.copy(
                    isLoading = false,
                    activeFileContent = res.getOrNull(),
                    errorMessage = res.exceptionOrNull()?.message
                )
            }
        }
    }

    fun saveLocalFile(newContent: String) {
        val ws = _uiState.value.selectedWorkspace ?: return
        val activeFile = _uiState.value.activeEditingFile ?: return
        viewModelScope.launch {
            val res = storageManager.saveFileContent(File(ws.path), activeFile.path, newContent)
            if (res.isSuccess) {
                _uiState.update { it.copy(activeFileContent = newContent, successMessage = "File saved successfully!") }
                loadFiles(File(ws.path), _uiState.value.currentSubPath)
                refreshGitStatus(File(ws.path))
            } else {
                _uiState.update { it.copy(errorMessage = "Failed to save file: ${res.exceptionOrNull()?.message}") }
            }
        }
    }

    fun closeActiveFile() {
        _uiState.update { it.copy(activeEditingFile = null, activeFileContent = null) }
    }

    fun refreshGitStatus(workspaceDir: File) {
        viewModelScope.launch {
            val statusRes = gitEngine.getStatus(workspaceDir)
            val diffRes = gitEngine.getDiff(workspaceDir)
            val branchesRes = gitEngine.getBranches(workspaceDir)

            _uiState.update { current ->
                current.copy(
                    gitStatus = statusRes.getOrNull(),
                    gitDiff = diffRes.getOrNull(),
                    branches = branchesRes.getOrDefault(emptyList())
                )
            }
        }
    }

    fun cloneRepository(remoteUrl: String, workspaceName: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val createRes = storageManager.createWorkspace(workspaceName, remoteUrl)
            if (createRes.isFailure) {
                _uiState.update { it.copy(isLoading = false, errorMessage = createRes.exceptionOrNull()?.message) }
                return@launch
            }

            val ws = createRes.getOrThrow()
            val token = tokenStorage.getToken()
            val cloneRes = gitEngine.cloneRepository(
                remoteUri = remoteUrl,
                targetDir = File(ws.path),
                token = token
            )

            if (cloneRes.isSuccess) {
                val updatedWs = ws.copy(isGitInitialized = true)
                workspaceDao.insertWorkspace(WorkspaceEntity.fromDomain(updatedWs))
                selectWorkspace(updatedWs)
                _uiState.update { it.copy(isLoading = false, successMessage = "Repository cloned successfully into workspace!") }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Clone failed: ${cloneRes.exceptionOrNull()?.message}") }
            }
        }
    }

    fun stageFile(filePattern: String = ".") {
        val ws = _uiState.value.selectedWorkspace ?: return
        viewModelScope.launch {
            val res = gitEngine.stageFile(File(ws.path), filePattern)
            if (res.isSuccess) {
                refreshGitStatus(File(ws.path))
            } else {
                _uiState.update { it.copy(errorMessage = "Failed to stage: ${res.exceptionOrNull()?.message}") }
            }
        }
    }

    fun commitChanges(message: String, authorName: String, authorEmail: String) {
        val ws = _uiState.value.selectedWorkspace ?: return
        viewModelScope.launch {
            val res = gitEngine.commit(File(ws.path), message, authorName, authorEmail)
            if (res.isSuccess) {
                _uiState.update { it.copy(successMessage = "Committed: ${res.getOrNull()?.shortMessage}") }
                refreshGitStatus(File(ws.path))
            } else {
                _uiState.update { it.copy(errorMessage = "Commit failed: ${res.exceptionOrNull()?.message}") }
            }
        }
    }

    fun importFile(relativePath: String, inputStream: InputStream) {
        val ws = _uiState.value.selectedWorkspace ?: return
        viewModelScope.launch {
            val res = storageManager.importStreamToFile(File(ws.path), relativePath, inputStream)
            if (res.isSuccess) {
                loadFiles(File(ws.path), _uiState.value.currentSubPath)
                _uiState.update { it.copy(successMessage = "File imported successfully!") }
            } else {
                _uiState.update { it.copy(errorMessage = "Import failed: ${res.exceptionOrNull()?.message}") }
            }
        }
    }

    fun exportFile(relativePath: String, outputStream: OutputStream) {
        val ws = _uiState.value.selectedWorkspace ?: return
        viewModelScope.launch {
            val res = storageManager.exportFileToStream(File(ws.path), relativePath, outputStream)
            if (res.isSuccess) {
                _uiState.update { it.copy(successMessage = "File exported successfully!") }
            } else {
                _uiState.update { it.copy(errorMessage = "Export failed: ${res.exceptionOrNull()?.message}") }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
