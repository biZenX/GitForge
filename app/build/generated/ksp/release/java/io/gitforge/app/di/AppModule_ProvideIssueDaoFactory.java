package io.gitforge.app.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.gitforge.core.database.GitForgeDatabase;
import io.gitforge.core.database.IssueDao;
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
public final class AppModule_ProvideIssueDaoFactory implements Factory<IssueDao> {
  private final Provider<GitForgeDatabase> databaseProvider;

  private AppModule_ProvideIssueDaoFactory(Provider<GitForgeDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public IssueDao get() {
    return provideIssueDao(databaseProvider.get());
  }

  public static AppModule_ProvideIssueDaoFactory create(
      Provider<GitForgeDatabase> databaseProvider) {
    return new AppModule_ProvideIssueDaoFactory(databaseProvider);
  }

  public static IssueDao provideIssueDao(GitForgeDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideIssueDao(database));
  }
}
