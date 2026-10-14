package io.gitforge.app.ui.repos;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.gitforge.app.data.GitHubRepository;
import io.gitforge.core.security.TokenStorage;
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
public final class RepositoriesViewModel_Factory implements Factory<RepositoriesViewModel> {
  private final Provider<GitHubRepository> repositoryProvider;

  private final Provider<TokenStorage> tokenStorageProvider;

  private RepositoriesViewModel_Factory(Provider<GitHubRepository> repositoryProvider,
      Provider<TokenStorage> tokenStorageProvider) {
    this.repositoryProvider = repositoryProvider;
    this.tokenStorageProvider = tokenStorageProvider;
  }

  @Override
  public RepositoriesViewModel get() {
    return newInstance(repositoryProvider.get(), tokenStorageProvider.get());
  }

  public static RepositoriesViewModel_Factory create(Provider<GitHubRepository> repositoryProvider,
      Provider<TokenStorage> tokenStorageProvider) {
    return new RepositoriesViewModel_Factory(repositoryProvider, tokenStorageProvider);
  }

  public static RepositoriesViewModel newInstance(GitHubRepository repository,
      TokenStorage tokenStorage) {
    return new RepositoriesViewModel(repository, tokenStorage);
  }
}
