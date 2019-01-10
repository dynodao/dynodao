package org.lemon.dynodao.processor.index;

import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.lemon.dynodao.processor.context.ProcessorContext;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class DynamoIndexParser_Factory implements Factory<DynamoIndexParser> {
  private final Provider<ProcessorContext> processorContextProvider;

  public DynamoIndexParser_Factory(Provider<ProcessorContext> processorContextProvider) {
    this.processorContextProvider = processorContextProvider;
  }

  @Override
  public DynamoIndexParser get() {
    DynamoIndexParser instance = new DynamoIndexParser();
    DynamoIndexParser_MembersInjector.injectProcessorContext(
        instance, processorContextProvider.get());
    return instance;
  }

  public static Factory<DynamoIndexParser> create(
      Provider<ProcessorContext> processorContextProvider) {
    return new DynamoIndexParser_Factory(processorContextProvider);
  }

  public static DynamoIndexParser newDynamoIndexParser() {
    return new DynamoIndexParser();
  }
}
