package io.gitforge.app.ui.repos;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.gitforge.app.data.GitHubRepository;
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
public final class RepoDetailViewModel_Factory implements Factory<RepoDetailViewModel> {
  private final Provider<GitHubRepository> repositoryProvider;

  private RepoDetailViewModel_Factory(Provider<GitHubRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public RepoDetailViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static RepoDetailViewModel_Factory create(Provider<GitHubRepository> repositoryProvider) {
    return new RepoDetailViewModel_Factory(repositoryProvider);
  }

  public static RepoDetailViewModel newInstance(GitHubRepository repository) {
    return new RepoDetailViewModel(repository);
  }
}
