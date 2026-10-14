package io.gitforge.app.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.gitforge.core.database.GitForgeDatabase;
import io.gitforge.core.database.RepoDao;
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
public final class AppModule_ProvideRepoDaoFactory implements Factory<RepoDao> {
  private final Provider<GitForgeDatabase> databaseProvider;

  private AppModule_ProvideRepoDaoFactory(Provider<GitForgeDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public RepoDao get() {
    return provideRepoDao(databaseProvider.get());
  }

  public static AppModule_ProvideRepoDaoFactory create(
      Provider<GitForgeDatabase> databaseProvider) {
    return new AppModule_ProvideRepoDaoFactory(databaseProvider);
  }

  public static RepoDao provideRepoDao(GitForgeDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideRepoDao(database));
  }
}
