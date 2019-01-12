package org.lemon.dynodao.processor.generate.type;

import dagger.MembersInjector;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.lemon.dynodao.processor.context.ProcessorContext;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class DocumentLoadTypeSpecMutator_MembersInjector
    implements MembersInjector<DocumentLoadTypeSpecMutator> {
  private final Provider<ProcessorContext> processorContextProvider;

  public DocumentLoadTypeSpecMutator_MembersInjector(
      Provider<ProcessorContext> processorContextProvider) {
    this.processorContextProvider = processorContextProvider;
  }

  public static MembersInjector<DocumentLoadTypeSpecMutator> create(
      Provider<ProcessorContext> processorContextProvider) {
    return new DocumentLoadTypeSpecMutator_MembersInjector(processorContextProvider);
  }

  @Override
  public void injectMembers(DocumentLoadTypeSpecMutator instance) {
    injectProcessorContext(instance, processorContextProvider.get());
    injectInit(instance);
  }

  public static void injectProcessorContext(Object instance, ProcessorContext processorContext) {
    ((DocumentLoadTypeSpecMutator) instance).processorContext = processorContext;
  }

  public static void injectInit(Object instance) {
    ((DocumentLoadTypeSpecMutator) instance).init();
  }
}
