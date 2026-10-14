package io.gitforge.app.data;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.gitforge.core.database.IssueDao;
import io.gitforge.core.database.RepoDao;
import io.gitforge.core.network.GitHubApiService;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class GitHubRepository_Factory implements Factory<GitHubRepository> {
  private final Provider<GitHubApiService> apiServiceProvider;

  private final Provider<RepoDao> repoDaoProvider;

  private final Provider<IssueDao> issueDaoProvider;

  private GitHubRepository_Factory(Provider<GitHubApiService> apiServiceProvider,
      Provider<RepoDao> repoDaoProvider, Provider<IssueDao> issueDaoProvider) {
    this.apiServiceProvider = apiServiceProvider;
    this.repoDaoProvider = repoDaoProvider;
    this.issueDaoProvider = issueDaoProvider;
  }

  @Override
  public GitHubRepository get() {
    return newInstance(apiServiceProvider.get(), repoDaoProvider.get(), issueDaoProvider.get());
  }

  public static GitHubRepository_Factory create(Provider<GitHubApiService> apiServiceProvider,
      Provider<RepoDao> repoDaoProvider, Provider<IssueDao> issueDaoProvider) {
    return new GitHubRepository_Factory(apiServiceProvider, repoDaoProvider, issueDaoProvider);
  }

  public static GitHubRepository newInstance(GitHubApiService apiService, RepoDao repoDao,
      IssueDao issueDao) {
    return new GitHubRepository(apiService, repoDao, issueDao);
  }
}
