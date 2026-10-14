package io.gitforge.app.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.gitforge.core.database.GitForgeDatabase;
import io.gitforge.core.database.WorkspaceDao;
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
public final class AppModule_ProvideWorkspaceDaoFactory implements Factory<WorkspaceDao> {
  private final Provider<GitForgeDatabase> databaseProvider;

  private AppModule_ProvideWorkspaceDaoFactory(Provider<GitForgeDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public WorkspaceDao get() {
    return provideWorkspaceDao(databaseProvider.get());
  }

  public static AppModule_ProvideWorkspaceDaoFactory create(
      Provider<GitForgeDatabase> databaseProvider) {
    return new AppModule_ProvideWorkspaceDaoFactory(databaseProvider);
  }

  public static WorkspaceDao provideWorkspaceDao(GitForgeDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideWorkspaceDao(database));
  }
}
