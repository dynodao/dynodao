package org.lemon.dynodao.processor.generate.type;

import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.lemon.dynodao.processor.context.ProcessorContext;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class DocumentQueryTypeSpecMutator_Factory
    implements Factory<DocumentQueryTypeSpecMutator> {
  private final Provider<ProcessorContext> processorContextProvider;

  public DocumentQueryTypeSpecMutator_Factory(Provider<ProcessorContext> processorContextProvider) {
    this.processorContextProvider = processorContextProvider;
  }

  @Override
  public DocumentQueryTypeSpecMutator get() {
    DocumentQueryTypeSpecMutator instance = new DocumentQueryTypeSpecMutator();
    DocumentQueryTypeSpecMutator_MembersInjector.injectProcessorContext(
        instance, processorContextProvider.get());
    DocumentQueryTypeSpecMutator_MembersInjector.injectInit(instance);
    return instance;
  }

  public static Factory<DocumentQueryTypeSpecMutator> create(
      Provider<ProcessorContext> processorContextProvider) {
    return new DocumentQueryTypeSpecMutator_Factory(processorContextProvider);
  }

  public static DocumentQueryTypeSpecMutator newDocumentQueryTypeSpecMutator() {
    return new DocumentQueryTypeSpecMutator();
  }
}
