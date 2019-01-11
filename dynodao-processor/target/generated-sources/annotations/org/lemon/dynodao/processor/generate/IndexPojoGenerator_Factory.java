package org.lemon.dynodao.processor.generate;

import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.lemon.dynodao.processor.context.ProcessorContext;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class IndexPojoGenerator_Factory implements Factory<IndexPojoGenerator> {
  private final Provider<ProcessorContext> processorContextProvider;

  private final Provider<TwoFieldPojoGenerator> twoFieldPojoGeneratorProvider;

  public IndexPojoGenerator_Factory(
      Provider<ProcessorContext> processorContextProvider,
      Provider<TwoFieldPojoGenerator> twoFieldPojoGeneratorProvider) {
    this.processorContextProvider = processorContextProvider;
    this.twoFieldPojoGeneratorProvider = twoFieldPojoGeneratorProvider;
  }

  @Override
  public IndexPojoGenerator get() {
    IndexPojoGenerator instance = new IndexPojoGenerator();
    IndexPojoGenerator_MembersInjector.injectProcessorContext(
        instance, processorContextProvider.get());
    IndexPojoGenerator_MembersInjector.injectTwoFieldPojoGenerator(
        instance, twoFieldPojoGeneratorProvider.get());
    return instance;
  }

  public static Factory<IndexPojoGenerator> create(
      Provider<ProcessorContext> processorContextProvider,
      Provider<TwoFieldPojoGenerator> twoFieldPojoGeneratorProvider) {
    return new IndexPojoGenerator_Factory(processorContextProvider, twoFieldPojoGeneratorProvider);
  }

  public static IndexPojoGenerator newIndexPojoGenerator() {
    return new IndexPojoGenerator();
  }
}
