package io.gitforge.app.ui.workspace;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.gitforge.core.database.WorkspaceDao;
import io.gitforge.core.security.TokenStorage;
import io.gitforge.feature.git.GitEngine;
import io.gitforge.feature.git.WorkspaceStorageManager;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class WorkspaceViewModel_Factory implements Factory<WorkspaceViewModel> {
  private final Provider<WorkspaceDao> workspaceDaoProvider;

  private final Provider<WorkspaceStorageManager> storageManagerProvider;

  private final Provider<GitEngine> gitEngineProvider;

  private final Provider<TokenStorage> tokenStorageProvider;

  private WorkspaceViewModel_Factory(Provider<WorkspaceDao> workspaceDaoProvider,
      Provider<WorkspaceStorageManager> storageManagerProvider,
      Provider<GitEngine> gitEngineProvider, Provider<TokenStorage> tokenStorageProvider) {
    this.workspaceDaoProvider = workspaceDaoProvider;
    this.storageManagerProvider = storageManagerProvider;
    this.gitEngineProvider = gitEngineProvider;
    this.tokenStorageProvider = tokenStorageProvider;
  }

  @Override
  public WorkspaceViewModel get() {
    return newInstance(workspaceDaoProvider.get(), storageManagerProvider.get(), gitEngineProvider.get(), tokenStorageProvider.get());
  }

  public static WorkspaceViewModel_Factory create(Provider<WorkspaceDao> workspaceDaoProvider,
      Provider<WorkspaceStorageManager> storageManagerProvider,
      Provider<GitEngine> gitEngineProvider, Provider<TokenStorage> tokenStorageProvider) {
    return new WorkspaceViewModel_Factory(workspaceDaoProvider, storageManagerProvider, gitEngineProvider, tokenStorageProvider);
  }

  public static WorkspaceViewModel newInstance(WorkspaceDao workspaceDao,
      WorkspaceStorageManager storageManager, GitEngine gitEngine, TokenStorage tokenStorage) {
    return new WorkspaceViewModel(workspaceDao, storageManager, gitEngine, tokenStorage);
  }
}
