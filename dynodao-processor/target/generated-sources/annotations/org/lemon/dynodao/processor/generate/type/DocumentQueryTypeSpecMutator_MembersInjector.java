package org.lemon.dynodao.processor.generate.type;

import dagger.MembersInjector;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.lemon.dynodao.processor.context.ProcessorContext;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class DocumentQueryTypeSpecMutator_MembersInjector
    implements MembersInjector<DocumentQueryTypeSpecMutator> {
  private final Provider<ProcessorContext> processorContextProvider;

  public DocumentQueryTypeSpecMutator_MembersInjector(
      Provider<ProcessorContext> processorContextProvider) {
    this.processorContextProvider = processorContextProvider;
  }

  public static MembersInjector<DocumentQueryTypeSpecMutator> create(
      Provider<ProcessorContext> processorContextProvider) {
    return new DocumentQueryTypeSpecMutator_MembersInjector(processorContextProvider);
  }

  @Override
  public void injectMembers(DocumentQueryTypeSpecMutator instance) {
    injectProcessorContext(instance, processorContextProvider.get());
    injectInit(instance);
  }

  public static void injectProcessorContext(Object instance, ProcessorContext processorContext) {
    ((DocumentQueryTypeSpecMutator) instance).processorContext = processorContext;
  }

  public static void injectInit(Object instance) {
    ((DocumentQueryTypeSpecMutator) instance).init();
  }
}
