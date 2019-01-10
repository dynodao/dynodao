package org.lemon.dynodao.processor;

import dagger.internal.Factory;
import dagger.internal.Preconditions;
import javax.annotation.Generated;
import org.lemon.dynodao.processor.context.ProcessorContext;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class ContextModule_ProvidesProcessorContextFactory
    implements Factory<ProcessorContext> {
  private final ContextModule module;

  public ContextModule_ProvidesProcessorContextFactory(ContextModule module) {
    this.module = module;
  }

  @Override
  public ProcessorContext get() {
    return Preconditions.checkNotNull(
        module.providesProcessorContext(),
        "Cannot return null from a non-@Nullable @Provides method");
  }

  public static Factory<ProcessorContext> create(ContextModule module) {
    return new ContextModule_ProvidesProcessorContextFactory(module);
  }

  public static ProcessorContext proxyProvidesProcessorContext(ContextModule instance) {
    return instance.providesProcessorContext();
  }
}
