package io.gitforge.app.ui.settings;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.gitforge.core.database.RepoDao;
import io.gitforge.core.security.OAuthManager;
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<TokenStorage> tokenStorageProvider;

  private final Provider<OAuthManager> oauthManagerProvider;

  private final Provider<RepoDao> repoDaoProvider;

  private SettingsViewModel_Factory(Provider<TokenStorage> tokenStorageProvider,
      Provider<OAuthManager> oauthManagerProvider, Provider<RepoDao> repoDaoProvider) {
    this.tokenStorageProvider = tokenStorageProvider;
    this.oauthManagerProvider = oauthManagerProvider;
    this.repoDaoProvider = repoDaoProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(tokenStorageProvider.get(), oauthManagerProvider.get(), repoDaoProvider.get());
  }

  public static SettingsViewModel_Factory create(Provider<TokenStorage> tokenStorageProvider,
      Provider<OAuthManager> oauthManagerProvider, Provider<RepoDao> repoDaoProvider) {
    return new SettingsViewModel_Factory(tokenStorageProvider, oauthManagerProvider, repoDaoProvider);
  }

  public static SettingsViewModel newInstance(TokenStorage tokenStorage, OAuthManager oauthManager,
      RepoDao repoDao) {
    return new SettingsViewModel(tokenStorage, oauthManager, repoDao);
  }
}
