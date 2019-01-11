package org.lemon.dynodao.processor.generate;

import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.lemon.dynodao.processor.context.ProcessorContext;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class TwoFieldPojoGenerator_Factory implements Factory<TwoFieldPojoGenerator> {
  private final Provider<ProcessorContext> processorContextProvider;

  public TwoFieldPojoGenerator_Factory(Provider<ProcessorContext> processorContextProvider) {
    this.processorContextProvider = processorContextProvider;
  }

  @Override
  public TwoFieldPojoGenerator get() {
    TwoFieldPojoGenerator instance = new TwoFieldPojoGenerator();
    TwoFieldPojoGenerator_MembersInjector.injectProcessorContext(
        instance, processorContextProvider.get());
    return instance;
  }

  public static Factory<TwoFieldPojoGenerator> create(
      Provider<ProcessorContext> processorContextProvider) {
    return new TwoFieldPojoGenerator_Factory(processorContextProvider);
  }

  public static TwoFieldPojoGenerator newTwoFieldPojoGenerator() {
    return new TwoFieldPojoGenerator();
  }
}
