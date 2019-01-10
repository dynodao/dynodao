package org.lemon.dynodao.processor.index;

import dagger.MembersInjector;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.lemon.dynodao.processor.context.ProcessorContext;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class DynamoIndexParser_MembersInjector implements MembersInjector<DynamoIndexParser> {
  private final Provider<ProcessorContext> processorContextProvider;

  public DynamoIndexParser_MembersInjector(Provider<ProcessorContext> processorContextProvider) {
    this.processorContextProvider = processorContextProvider;
  }

  public static MembersInjector<DynamoIndexParser> create(
      Provider<ProcessorContext> processorContextProvider) {
    return new DynamoIndexParser_MembersInjector(processorContextProvider);
  }

  @Override
  public void injectMembers(DynamoIndexParser instance) {
    injectProcessorContext(instance, processorContextProvider.get());
  }

  public static void injectProcessorContext(
      DynamoIndexParser instance, ProcessorContext processorContext) {
    instance.processorContext = processorContext;
  }
}
