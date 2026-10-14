package io.gitforge.feature.git

import io.gitforge.core.model.GitCommitResult
import io.gitforge.core.model.GitStatusResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ResetCommand
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.ByteArrayOutputStream
import java.io.File

interface GitEngine {
    suspend fun initRepository(repoDir: File): Result<Unit>
    suspend fun cloneRepository(
        remoteUri: String,
        targetDir: File,
        token: String? = null,
        branch: String? = null
    ): Result<Unit>
    suspend fun getStatus(repoDir: File): Result<GitStatusResult>
    suspend fun stageFile(repoDir: File, filePattern: String = "."): Result<Unit>
    suspend fun unstageFile(repoDir: File, filePattern: String): Result<Unit>
    suspend fun commit(
        repoDir: File,
        message: String,
        authorName: String,
        authorEmail: String
    ): Result<GitCommitResult>
    suspend fun getBranches(repoDir: File): Result<List<String>>
    suspend fun checkoutBranch(repoDir: File, branchName: String, createBranch: Boolean = false): Result<Unit>
    suspend fun deleteBranch(repoDir: File, branchName: String): Result<Unit>
    suspend fun getDiff(repoDir: File): Result<String>
    suspend fun pull(repoDir: File, token: String? = null): Result<Unit>
    suspend fun push(repoDir: File, token: String? = null): Result<Unit>
}

class JGitEngine : GitEngine {

    override suspend fun initRepository(repoDir: File): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            if (!repoDir.exists()) repoDir.mkdirs()
            Git.init().setDirectory(repoDir).call().close()
        }
    }

    override suspend fun cloneRepository(
        remoteUri: String,
        targetDir: File,
        token: String?,
        branch: String?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            if (!targetDir.exists()) targetDir.mkdirs()
            val cloneCommand = Git.cloneRepository()
                .setURI(remoteUri)
                .setDirectory(targetDir)

            if (!token.isNullOrBlank()) {
                cloneCommand.setCredentialsProvider(UsernamePasswordCredentialsProvider("oauth2", token))
            }
            if (!branch.isNullOrBlank()) {
                cloneCommand.setBranch(branch)
            }

            cloneCommand.call().close()
        }
    }

    override suspend fun getStatus(repoDir: File): Result<GitStatusResult> = withContext(Dispatchers.IO) {
        runCatching {
            Git.open(repoDir).use { git ->
                val status = git.status().call()
                val currentBranch = runCatching { git.repository.branch }.getOrDefault("main")

                GitStatusResult(
                    branch = currentBranch,
                    added = status.added.toList(),
                    changed = status.changed.toList(),
                    removed = status.removed.toList(),
                    missing = status.missing.toList(),
                    modified = status.modified.toList(),
                    untracked = status.untracked.toList(),
                    conflicting = status.conflicting.toList(),
                    isClean = status.isClean
                )
            }
        }
    }

    override suspend fun stageFile(repoDir: File, filePattern: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            Git.open(repoDir).use { git ->
                git.add().addFilepattern(filePattern).call()
            }
            Unit
        }
    }

    override suspend fun unstageFile(repoDir: File, filePattern: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            Git.open(repoDir).use { git ->
                git.reset().setMode(ResetCommand.ResetType.MIXED).addPath(filePattern).call()
            }
            Unit
        }
    }

    override suspend fun commit(
        repoDir: File,
        message: String,
        authorName: String,
        authorEmail: String
    ): Result<GitCommitResult> = withContext(Dispatchers.IO) {
        runCatching {
            Git.open(repoDir).use { git ->
                val revCommit = git.commit()
                    .setMessage(message)
                    .setAuthor(authorName, authorEmail)
                    .call()

                GitCommitResult(
                    commitId = revCommit.name,
                    shortMessage = revCommit.shortMessage,
                    author = "$authorName <$authorEmail>"
                )
            }
        }
    }

    override suspend fun getBranches(repoDir: File): Result<List<String>> = withContext(Dispatchers.IO) {
        runCatching {
            Git.open(repoDir).use { git ->
                git.branchList().call().map { ref ->
                    git.repository.shortenRemoteBranchName(ref.name)
                }
            }
        }
    }

    override suspend fun checkoutBranch(
        repoDir: File,
        branchName: String,
        createBranch: Boolean
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            Git.open(repoDir).use { git ->
                git.checkout()
                    .setName(branchName)
                    .setCreateBranch(createBranch)
                    .call()
            }
            Unit
        }
    }

    override suspend fun deleteBranch(repoDir: File, branchName: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            Git.open(repoDir).use { git ->
                git.branchDelete().setBranchNames(branchName).setForce(true).call()
            }
            Unit
        }
    }

    override suspend fun getDiff(repoDir: File): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            Git.open(repoDir).use { git ->
                val out = ByteArrayOutputStream()
                DiffFormatter(out).use { formatter ->
                    formatter.setRepository(git.repository)
                    val entries = git.diff().setOutputStream(out).call()
                    for (entry in entries) {
                        formatter.format(entry)
                    }
                }
                out.toString(Charsets.UTF_8.name())
            }
        }
    }

    override suspend fun pull(repoDir: File, token: String?): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            Git.open(repoDir).use { git ->
                val pullCmd = git.pull()
                if (!token.isNullOrBlank()) {
                    pullCmd.setCredentialsProvider(UsernamePasswordCredentialsProvider("oauth2", token))
                }
                pullCmd.call()
            }
            Unit
        }
    }

    override suspend fun push(repoDir: File, token: String?): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            Git.open(repoDir).use { git ->
                val pushCmd = git.push()
                if (!token.isNullOrBlank()) {
                    pushCmd.setCredentialsProvider(UsernamePasswordCredentialsProvider("oauth2", token))
                }
                pushCmd.call()
            }
            Unit
        }
    }
}
