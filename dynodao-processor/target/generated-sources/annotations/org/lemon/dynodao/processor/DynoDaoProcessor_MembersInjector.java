package org.lemon.dynodao.processor;

import dagger.MembersInjector;
import javax.annotation.Generated;
import javax.inject.Provider;
import org.lemon.dynodao.processor.generate.IndexPojoGenerator;
import org.lemon.dynodao.processor.index.DynamoIndexParser;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class DynoDaoProcessor_MembersInjector implements MembersInjector<DynoDaoProcessor> {
  private final Provider<DynamoIndexParser> dynamoIndexParserProvider;

  private final Provider<IndexPojoGenerator> indexPojoGeneratorProvider;

  public DynoDaoProcessor_MembersInjector(
      Provider<DynamoIndexParser> dynamoIndexParserProvider,
      Provider<IndexPojoGenerator> indexPojoGeneratorProvider) {
    this.dynamoIndexParserProvider = dynamoIndexParserProvider;
    this.indexPojoGeneratorProvider = indexPojoGeneratorProvider;
  }

  public static MembersInjector<DynoDaoProcessor> create(
      Provider<DynamoIndexParser> dynamoIndexParserProvider,
      Provider<IndexPojoGenerator> indexPojoGeneratorProvider) {
    return new DynoDaoProcessor_MembersInjector(
        dynamoIndexParserProvider, indexPojoGeneratorProvider);
  }

  @Override
  public void injectMembers(DynoDaoProcessor instance) {
    injectDynamoIndexParser(instance, dynamoIndexParserProvider.get());
    injectIndexPojoGenerator(instance, indexPojoGeneratorProvider.get());
  }

  public static void injectDynamoIndexParser(
      DynoDaoProcessor instance, DynamoIndexParser dynamoIndexParser) {
    instance.dynamoIndexParser = dynamoIndexParser;
  }

  public static void injectIndexPojoGenerator(
      DynoDaoProcessor instance, IndexPojoGenerator indexPojoGenerator) {
    instance.indexPojoGenerator = indexPojoGenerator;
  }
}
