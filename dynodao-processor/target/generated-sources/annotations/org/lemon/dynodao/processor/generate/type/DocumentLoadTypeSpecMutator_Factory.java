package org.lemon.dynodao.processor.generate.type;

import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.lemon.dynodao.processor.context.ProcessorContext;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class DocumentLoadTypeSpecMutator_Factory
    implements Factory<DocumentLoadTypeSpecMutator> {
  private final Provider<ProcessorContext> processorContextProvider;

  public DocumentLoadTypeSpecMutator_Factory(Provider<ProcessorContext> processorContextProvider) {
    this.processorContextProvider = processorContextProvider;
  }

  @Override
  public DocumentLoadTypeSpecMutator get() {
    DocumentLoadTypeSpecMutator instance = new DocumentLoadTypeSpecMutator();
    DocumentLoadTypeSpecMutator_MembersInjector.injectProcessorContext(
        instance, processorContextProvider.get());
    DocumentLoadTypeSpecMutator_MembersInjector.injectInit(instance);
    return instance;
  }

  public static Factory<DocumentLoadTypeSpecMutator> create(
      Provider<ProcessorContext> processorContextProvider) {
    return new DocumentLoadTypeSpecMutator_Factory(processorContextProvider);
  }

  public static DocumentLoadTypeSpecMutator newDocumentLoadTypeSpecMutator() {
    return new DocumentLoadTypeSpecMutator();
  }
}
