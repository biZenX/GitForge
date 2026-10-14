package io.gitforge.app.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.gitforge.core.network.GitHubApiService;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;

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
public final class AppModule_ProvideGitHubApiServiceFactory implements Factory<GitHubApiService> {
  private final Provider<OkHttpClient> okHttpClientProvider;

  private AppModule_ProvideGitHubApiServiceFactory(Provider<OkHttpClient> okHttpClientProvider) {
    this.okHttpClientProvider = okHttpClientProvider;
  }

  @Override
  public GitHubApiService get() {
    return provideGitHubApiService(okHttpClientProvider.get());
  }

  public static AppModule_ProvideGitHubApiServiceFactory create(
      Provider<OkHttpClient> okHttpClientProvider) {
    return new AppModule_ProvideGitHubApiServiceFactory(okHttpClientProvider);
  }

  public static GitHubApiService provideGitHubApiService(OkHttpClient okHttpClient) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideGitHubApiService(okHttpClient));
  }
}
