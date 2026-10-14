package io.gitforge.app.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.gitforge.core.database.GitForgeDatabase
import io.gitforge.core.database.IssueDao
import io.gitforge.core.database.RepoDao
import io.gitforge.core.database.WorkspaceDao
import io.gitforge.core.network.GitHubApiService
import io.gitforge.core.network.NetworkClientFactory
import io.gitforge.core.security.EncryptedTokenStorage
import io.gitforge.core.security.OAuthManager
import io.gitforge.core.security.TokenStorage
import io.gitforge.feature.git.GitEngine
import io.gitforge.feature.git.JGitEngine
import io.gitforge.feature.git.WorkspaceStorageManager
import io.gitforge.feature.terminal.SandboxedTerminalRunner
import io.gitforge.feature.terminal.TerminalCommandRunner
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTokenStorage(@ApplicationContext context: Context): TokenStorage {
        return EncryptedTokenStorage(context)
    }

    @Provides
    @Singleton
    fun provideOAuthManager(): OAuthManager {
        return OAuthManager()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(tokenStorage: TokenStorage): OkHttpClient {
        return NetworkClientFactory.createOkHttpClient(tokenStorage, isDebug = false)
    }

    @Provides
    @Singleton
    fun provideGitHubApiService(okHttpClient: OkHttpClient): GitHubApiService {
        return NetworkClientFactory.createGitHubApiService(okHttpClient)
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): androidx.work.WorkManager {
        return androidx.work.WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GitForgeDatabase {
        return Room.databaseBuilder(
            context,
            GitForgeDatabase::class.java,
            "gitforge_local.db"
        ).fallbackToDestructiveMigration(true).build()
    }

    @Provides
    fun provideRepoDao(database: GitForgeDatabase): RepoDao = database.repoDao()

    @Provides
    fun provideIssueDao(database: GitForgeDatabase): IssueDao = database.issueDao()

    @Provides
    fun provideWorkspaceDao(database: GitForgeDatabase): WorkspaceDao = database.workspaceDao()

    @Provides
    @Singleton
    fun provideGitEngine(): GitEngine = JGitEngine()

    @Provides
    @Singleton
    fun provideWorkspaceStorageManager(@ApplicationContext context: Context): WorkspaceStorageManager {
        return WorkspaceStorageManager(context)
    }

    @Provides
    @Singleton
    fun provideTerminalRunner(): TerminalCommandRunner {
        return SandboxedTerminalRunner()
    }
}
