package org.lemon.dynodao.processor;

import dagger.MembersInjector;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.lemon.dynodao.processor.index.DynamoIndexParser;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class DynoDaoProcessor_MembersInjector implements MembersInjector<DynoDaoProcessor> {
  private final Provider<DynamoIndexParser> dynamoIndexParserProvider;

  public DynoDaoProcessor_MembersInjector(Provider<DynamoIndexParser> dynamoIndexParserProvider) {
    this.dynamoIndexParserProvider = dynamoIndexParserProvider;
  }

  public static MembersInjector<DynoDaoProcessor> create(
      Provider<DynamoIndexParser> dynamoIndexParserProvider) {
    return new DynoDaoProcessor_MembersInjector(dynamoIndexParserProvider);
  }

  @Override
  public void injectMembers(DynoDaoProcessor instance) {
    injectDynamoIndexParser(instance, dynamoIndexParserProvider.get());
  }

  public static void injectDynamoIndexParser(
      DynoDaoProcessor instance, DynamoIndexParser dynamoIndexParser) {
    instance.dynamoIndexParser = dynamoIndexParser;
  }
}
