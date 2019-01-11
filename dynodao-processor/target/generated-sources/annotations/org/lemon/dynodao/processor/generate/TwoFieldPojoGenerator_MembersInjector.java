package org.lemon.dynodao.processor.generate;

import dagger.MembersInjector;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.lemon.dynodao.processor.context.ProcessorContext;
import org.lemon.dynodao.processor.generate.method.CtorTypeGenerator;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class TwoFieldPojoGenerator_MembersInjector
    implements MembersInjector<TwoFieldPojoGenerator> {
  private final Provider<ProcessorContext> processorContextProvider;

  private final Provider<CtorTypeGenerator> ctorGeneratorProvider;

  public TwoFieldPojoGenerator_MembersInjector(
      Provider<ProcessorContext> processorContextProvider,
      Provider<CtorTypeGenerator> ctorGeneratorProvider) {
    this.processorContextProvider = processorContextProvider;
    this.ctorGeneratorProvider = ctorGeneratorProvider;
  }

  public static MembersInjector<TwoFieldPojoGenerator> create(
      Provider<ProcessorContext> processorContextProvider,
      Provider<CtorTypeGenerator> ctorGeneratorProvider) {
    return new TwoFieldPojoGenerator_MembersInjector(
        processorContextProvider, ctorGeneratorProvider);
  }

  @Override
  public void injectMembers(TwoFieldPojoGenerator instance) {
    injectProcessorContext(instance, processorContextProvider.get());
    injectCtorGenerator(instance, ctorGeneratorProvider.get());
  }

  public static void injectProcessorContext(Object instance, ProcessorContext processorContext) {
    ((TwoFieldPojoGenerator) instance).processorContext = processorContext;
  }

  public static void injectCtorGenerator(Object instance, CtorTypeGenerator ctorGenerator) {
    ((TwoFieldPojoGenerator) instance).ctorGenerator = ctorGenerator;
  }
}
