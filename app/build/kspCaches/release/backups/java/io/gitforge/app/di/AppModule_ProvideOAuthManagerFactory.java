package io.gitforge.app.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.gitforge.core.security.OAuthManager;
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
public final class AppModule_ProvideOAuthManagerFactory implements Factory<OAuthManager> {
  @Override
  public OAuthManager get() {
    return provideOAuthManager();
  }

  public static AppModule_ProvideOAuthManagerFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static OAuthManager provideOAuthManager() {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideOAuthManager());
  }

  private static final class InstanceHolder {
    static final AppModule_ProvideOAuthManagerFactory INSTANCE = new AppModule_ProvideOAuthManagerFactory();
  }
}
