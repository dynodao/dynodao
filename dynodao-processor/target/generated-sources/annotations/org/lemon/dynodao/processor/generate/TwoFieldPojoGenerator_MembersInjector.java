package org.lemon.dynodao.processor.generate;

import dagger.MembersInjector;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.lemon.dynodao.processor.context.ProcessorContext;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class TwoFieldPojoGenerator_MembersInjector
    implements MembersInjector<TwoFieldPojoGenerator> {
  private final Provider<ProcessorContext> processorContextProvider;

  public TwoFieldPojoGenerator_MembersInjector(
      Provider<ProcessorContext> processorContextProvider) {
    this.processorContextProvider = processorContextProvider;
  }

  public static MembersInjector<TwoFieldPojoGenerator> create(
      Provider<ProcessorContext> processorContextProvider) {
    return new TwoFieldPojoGenerator_MembersInjector(processorContextProvider);
  }

  @Override
  public void injectMembers(TwoFieldPojoGenerator instance) {
    injectProcessorContext(instance, processorContextProvider.get());
  }

  public static void injectProcessorContext(Object instance, ProcessorContext processorContext) {
    ((TwoFieldPojoGenerator) instance).processorContext = processorContext;
  }
}
