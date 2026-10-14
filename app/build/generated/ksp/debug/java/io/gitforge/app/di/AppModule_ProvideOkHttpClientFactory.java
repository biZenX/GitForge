package io.gitforge.app.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.gitforge.core.security.TokenStorage;
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
public final class AppModule_ProvideOkHttpClientFactory implements Factory<OkHttpClient> {
  private final Provider<TokenStorage> tokenStorageProvider;

  private AppModule_ProvideOkHttpClientFactory(Provider<TokenStorage> tokenStorageProvider) {
    this.tokenStorageProvider = tokenStorageProvider;
  }

  @Override
  public OkHttpClient get() {
    return provideOkHttpClient(tokenStorageProvider.get());
  }

  public static AppModule_ProvideOkHttpClientFactory create(
      Provider<TokenStorage> tokenStorageProvider) {
    return new AppModule_ProvideOkHttpClientFactory(tokenStorageProvider);
  }

  public static OkHttpClient provideOkHttpClient(TokenStorage tokenStorage) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideOkHttpClient(tokenStorage));
  }
}
