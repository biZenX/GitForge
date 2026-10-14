package io.gitforge.app.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import io.gitforge.feature.terminal.TerminalCommandRunner;
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
public final class AppModule_ProvideTerminalRunnerFactory implements Factory<TerminalCommandRunner> {
  @Override
  public TerminalCommandRunner get() {
    return provideTerminalRunner();
  }

  public static AppModule_ProvideTerminalRunnerFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static TerminalCommandRunner provideTerminalRunner() {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideTerminalRunner());
  }

  private static final class InstanceHolder {
    static final AppModule_ProvideTerminalRunnerFactory INSTANCE = new AppModule_ProvideTerminalRunnerFactory();
  }
}
