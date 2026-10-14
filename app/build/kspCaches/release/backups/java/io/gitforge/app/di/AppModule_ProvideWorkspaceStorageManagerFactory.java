package io.gitforge.app.di;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.gitforge.feature.git.WorkspaceStorageManager;
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
public final class AppModule_ProvideWorkspaceStorageManagerFactory implements Factory<WorkspaceStorageManager> {
  private final Provider<Context> contextProvider;

  private AppModule_ProvideWorkspaceStorageManagerFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public WorkspaceStorageManager get() {
    return provideWorkspaceStorageManager(contextProvider.get());
  }

  public static AppModule_ProvideWorkspaceStorageManagerFactory create(
      Provider<Context> contextProvider) {
    return new AppModule_ProvideWorkspaceStorageManagerFactory(contextProvider);
  }

  public static WorkspaceStorageManager provideWorkspaceStorageManager(Context context) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideWorkspaceStorageManager(context));
  }
}
