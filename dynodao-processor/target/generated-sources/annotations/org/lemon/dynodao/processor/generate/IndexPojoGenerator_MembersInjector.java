package org.lemon.dynodao.processor.generate;

import dagger.MembersInjector;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.lemon.dynodao.processor.context.ProcessorContext;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class IndexPojoGenerator_MembersInjector
    implements MembersInjector<IndexPojoGenerator> {
  private final Provider<ProcessorContext> processorContextProvider;

  private final Provider<TwoFieldPojoGenerator> twoFieldPojoGeneratorProvider;

  public IndexPojoGenerator_MembersInjector(
      Provider<ProcessorContext> processorContextProvider,
      Provider<TwoFieldPojoGenerator> twoFieldPojoGeneratorProvider) {
    this.processorContextProvider = processorContextProvider;
    this.twoFieldPojoGeneratorProvider = twoFieldPojoGeneratorProvider;
  }

  public static MembersInjector<IndexPojoGenerator> create(
      Provider<ProcessorContext> processorContextProvider,
      Provider<TwoFieldPojoGenerator> twoFieldPojoGeneratorProvider) {
    return new IndexPojoGenerator_MembersInjector(
        processorContextProvider, twoFieldPojoGeneratorProvider);
  }

  @Override
  public void injectMembers(IndexPojoGenerator instance) {
    injectProcessorContext(instance, processorContextProvider.get());
    injectTwoFieldPojoGenerator(instance, twoFieldPojoGeneratorProvider.get());
  }

  public static void injectProcessorContext(
      IndexPojoGenerator instance, ProcessorContext processorContext) {
    instance.processorContext = processorContext;
  }

  public static void injectTwoFieldPojoGenerator(
      IndexPojoGenerator instance, Object twoFieldPojoGenerator) {
    instance.twoFieldPojoGenerator = (TwoFieldPojoGenerator) twoFieldPojoGenerator;
  }
}
