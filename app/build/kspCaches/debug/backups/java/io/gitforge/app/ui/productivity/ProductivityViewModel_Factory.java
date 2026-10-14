package io.gitforge.app.ui.productivity;

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
public final class ProductivityViewModel_Factory implements Factory<ProductivityViewModel> {
  private final Provider<GitHubRepository> repositoryProvider;

  private ProductivityViewModel_Factory(Provider<GitHubRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ProductivityViewModel get() {
    return newInstance(repositoryProvider.get());
  }

  public static ProductivityViewModel_Factory create(
      Provider<GitHubRepository> repositoryProvider) {
    return new ProductivityViewModel_Factory(repositoryProvider);
  }

  public static ProductivityViewModel newInstance(GitHubRepository repository) {
    return new ProductivityViewModel(repository);
  }
}
