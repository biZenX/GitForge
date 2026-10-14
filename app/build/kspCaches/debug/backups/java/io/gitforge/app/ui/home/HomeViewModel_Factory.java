package io.gitforge.app.ui.home;

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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<GitHubRepository> repositoryProvider;

  private final Provider<TokenStorage> tokenStorageProvider;

  private HomeViewModel_Factory(Provider<GitHubRepository> repositoryProvider,
      Provider<TokenStorage> tokenStorageProvider) {
    this.repositoryProvider = repositoryProvider;
    this.tokenStorageProvider = tokenStorageProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(repositoryProvider.get(), tokenStorageProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<GitHubRepository> repositoryProvider,
      Provider<TokenStorage> tokenStorageProvider) {
    return new HomeViewModel_Factory(repositoryProvider, tokenStorageProvider);
  }

  public static HomeViewModel newInstance(GitHubRepository repository, TokenStorage tokenStorage) {
    return new HomeViewModel(repository, tokenStorage);
  }
}
