package io.gitforge.app.di;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.gitforge.core.security.TokenStorage;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class AppModule_ProvideTokenStorageFactory implements Factory<TokenStorage> {
  private final Provider<Context> contextProvider;

  private AppModule_ProvideTokenStorageFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public TokenStorage get() {
    return provideTokenStorage(contextProvider.get());
  }

  public static AppModule_ProvideTokenStorageFactory create(Provider<Context> contextProvider) {
    return new AppModule_ProvideTokenStorageFactory(contextProvider);
  }

  public static TokenStorage provideTokenStorage(Context context) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideTokenStorage(context));
  }
}
