package org.lemon.dynodao.processor.generate;

import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.lemon.dynodao.processor.context.ProcessorContext;
import org.lemon.dynodao.processor.generate.method.CtorTypeGenerator;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class TwoFieldPojoGenerator_Factory implements Factory<TwoFieldPojoGenerator> {
  private final Provider<ProcessorContext> processorContextProvider;

  private final Provider<CtorTypeGenerator> ctorGeneratorProvider;

  public TwoFieldPojoGenerator_Factory(
      Provider<ProcessorContext> processorContextProvider,
      Provider<CtorTypeGenerator> ctorGeneratorProvider) {
    this.processorContextProvider = processorContextProvider;
    this.ctorGeneratorProvider = ctorGeneratorProvider;
  }

  @Override
  public TwoFieldPojoGenerator get() {
    TwoFieldPojoGenerator instance = new TwoFieldPojoGenerator();
    TwoFieldPojoGenerator_MembersInjector.injectProcessorContext(
        instance, processorContextProvider.get());
    TwoFieldPojoGenerator_MembersInjector.injectCtorGenerator(
        instance, ctorGeneratorProvider.get());
    return instance;
  }

  public static Factory<TwoFieldPojoGenerator> create(
      Provider<ProcessorContext> processorContextProvider,
      Provider<CtorTypeGenerator> ctorGeneratorProvider) {
    return new TwoFieldPojoGenerator_Factory(processorContextProvider, ctorGeneratorProvider);
  }

  public static TwoFieldPojoGenerator newTwoFieldPojoGenerator() {
    return new TwoFieldPojoGenerator();
  }
}
