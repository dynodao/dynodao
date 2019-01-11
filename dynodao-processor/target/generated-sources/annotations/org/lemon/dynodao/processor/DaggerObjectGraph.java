package org.lemon.dynodao.processor;

import dagger.internal.Preconditions;
import javax.annotation.Generated;
import org.lemon.dynodao.processor.generate.IndexPojoGenerator;
import org.lemon.dynodao.processor.generate.IndexPojoGenerator_Factory;
import org.lemon.dynodao.processor.generate.IndexPojoGenerator_MembersInjector;
import org.lemon.dynodao.processor.generate.TwoFieldPojoGenerator_Factory;
import org.lemon.dynodao.processor.generate.TwoFieldPojoGenerator_MembersInjector;
import org.lemon.dynodao.processor.index.DynamoIndexParser;
import org.lemon.dynodao.processor.index.DynamoIndexParser_Factory;
import org.lemon.dynodao.processor.index.DynamoIndexParser_MembersInjector;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class DaggerObjectGraph implements ObjectGraph {
  private ContextModule contextModule;

  private DaggerObjectGraph(Builder builder) {
    initialize(builder);
  }

  public static Builder builder() {
    return new Builder();
  }

  @SuppressWarnings("unchecked")
  private void initialize(final Builder builder) {
    this.contextModule = builder.contextModule;
  }

  @Override
  public void inject(DynoDaoProcessor processor) {
    injectDynoDaoProcessor(processor);
  }

  private DynamoIndexParser injectDynamoIndexParser(DynamoIndexParser instance) {
    DynamoIndexParser_MembersInjector.injectProcessorContext(
        instance,
        Preconditions.checkNotNull(
            contextModule.providesProcessorContext(),
            "Cannot return null from a non-@Nullable @Provides method"));
    return instance;
  }

  private Object injectTwoFieldPojoGenerator(Object instance) {
    TwoFieldPojoGenerator_MembersInjector.injectProcessorContext(
        instance,
        Preconditions.checkNotNull(
            contextModule.providesProcessorContext(),
            "Cannot return null from a non-@Nullable @Provides method"));
    return instance;
  }

  private IndexPojoGenerator injectIndexPojoGenerator(IndexPojoGenerator instance) {
    IndexPojoGenerator_MembersInjector.injectProcessorContext(
        instance,
        Preconditions.checkNotNull(
            contextModule.providesProcessorContext(),
            "Cannot return null from a non-@Nullable @Provides method"));
    IndexPojoGenerator_MembersInjector.injectTwoFieldPojoGenerator(
        instance,
        injectTwoFieldPojoGenerator(TwoFieldPojoGenerator_Factory.newTwoFieldPojoGenerator()));
    return instance;
  }

  private DynoDaoProcessor injectDynoDaoProcessor(DynoDaoProcessor instance) {
    DynoDaoProcessor_MembersInjector.injectDynamoIndexParser(
        instance, injectDynamoIndexParser(DynamoIndexParser_Factory.newDynamoIndexParser()));
    DynoDaoProcessor_MembersInjector.injectIndexPojoGenerator(
        instance, injectIndexPojoGenerator(IndexPojoGenerator_Factory.newIndexPojoGenerator()));
    return instance;
  }

  public static final class Builder {
    private ContextModule contextModule;

    private Builder() {}

    public ObjectGraph build() {
      if (contextModule == null) {
        throw new IllegalStateException(ContextModule.class.getCanonicalName() + " must be set");
      }
      return new DaggerObjectGraph(this);
    }

    public Builder contextModule(ContextModule contextModule) {
      this.contextModule = Preconditions.checkNotNull(contextModule);
      return this;
    }
  }
}
