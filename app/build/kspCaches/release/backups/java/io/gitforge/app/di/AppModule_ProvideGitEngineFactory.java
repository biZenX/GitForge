package io.gitforge.app.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.gitforge.feature.git.GitEngine;
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
public final class AppModule_ProvideGitEngineFactory implements Factory<GitEngine> {
  @Override
  public GitEngine get() {
    return provideGitEngine();
  }

  public static AppModule_ProvideGitEngineFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static GitEngine provideGitEngine() {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideGitEngine());
  }

  private static final class InstanceHolder {
    static final AppModule_ProvideGitEngineFactory INSTANCE = new AppModule_ProvideGitEngineFactory();
  }
}
