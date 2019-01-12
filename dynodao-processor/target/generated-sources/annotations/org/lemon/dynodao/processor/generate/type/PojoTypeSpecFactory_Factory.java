package org.lemon.dynodao.processor.generate.type;

import dagger.internal.Factory;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class PojoTypeSpecFactory_Factory implements Factory<PojoTypeSpecFactory> {
  private final Provider<ModifiersTypeSpecMutator> modifiersTypeSpecMutatorProvider;

  private final Provider<SuperInterfaceTypeSpecMutator> superInterfaceTypeSpecMutatorProvider;

  private final Provider<FieldTypeSpecMutator> fieldTypeSpecMutatorProvider;

  private final Provider<CtorTypeSpecMutator> ctorTypeSpecMutatorProvider;

  private final Provider<DocumentLoadTypeSpecMutator> documentLoadTypeSpecMutatorProvider;

  private final Provider<DocumentQueryTypeSpecMutator> documentQueryTypeSpecMutatorProvider;

  public PojoTypeSpecFactory_Factory(
      Provider<ModifiersTypeSpecMutator> modifiersTypeSpecMutatorProvider,
      Provider<SuperInterfaceTypeSpecMutator> superInterfaceTypeSpecMutatorProvider,
      Provider<FieldTypeSpecMutator> fieldTypeSpecMutatorProvider,
      Provider<CtorTypeSpecMutator> ctorTypeSpecMutatorProvider,
      Provider<DocumentLoadTypeSpecMutator> documentLoadTypeSpecMutatorProvider,
      Provider<DocumentQueryTypeSpecMutator> documentQueryTypeSpecMutatorProvider) {
    this.modifiersTypeSpecMutatorProvider = modifiersTypeSpecMutatorProvider;
    this.superInterfaceTypeSpecMutatorProvider = superInterfaceTypeSpecMutatorProvider;
    this.fieldTypeSpecMutatorProvider = fieldTypeSpecMutatorProvider;
    this.ctorTypeSpecMutatorProvider = ctorTypeSpecMutatorProvider;
    this.documentLoadTypeSpecMutatorProvider = documentLoadTypeSpecMutatorProvider;
    this.documentQueryTypeSpecMutatorProvider = documentQueryTypeSpecMutatorProvider;
  }

  @Override
  public PojoTypeSpecFactory get() {
    PojoTypeSpecFactory instance = new PojoTypeSpecFactory();
    PojoTypeSpecFactory_MembersInjector.injectModifiersTypeSpecMutator(
        instance, modifiersTypeSpecMutatorProvider.get());
    PojoTypeSpecFactory_MembersInjector.injectSuperInterfaceTypeSpecMutator(
        instance, superInterfaceTypeSpecMutatorProvider.get());
    PojoTypeSpecFactory_MembersInjector.injectFieldTypeSpecMutator(
        instance, fieldTypeSpecMutatorProvider.get());
    PojoTypeSpecFactory_MembersInjector.injectCtorTypeSpecMutator(
        instance, ctorTypeSpecMutatorProvider.get());
    PojoTypeSpecFactory_MembersInjector.injectDocumentLoadTypeSpecMutator(
        instance, documentLoadTypeSpecMutatorProvider.get());
    PojoTypeSpecFactory_MembersInjector.injectDocumentQueryTypeSpecMutator(
        instance, documentQueryTypeSpecMutatorProvider.get());
    return instance;
  }

  public static Factory<PojoTypeSpecFactory> create(
      Provider<ModifiersTypeSpecMutator> modifiersTypeSpecMutatorProvider,
      Provider<SuperInterfaceTypeSpecMutator> superInterfaceTypeSpecMutatorProvider,
      Provider<FieldTypeSpecMutator> fieldTypeSpecMutatorProvider,
      Provider<CtorTypeSpecMutator> ctorTypeSpecMutatorProvider,
      Provider<DocumentLoadTypeSpecMutator> documentLoadTypeSpecMutatorProvider,
      Provider<DocumentQueryTypeSpecMutator> documentQueryTypeSpecMutatorProvider) {
    return new PojoTypeSpecFactory_Factory(
        modifiersTypeSpecMutatorProvider,
        superInterfaceTypeSpecMutatorProvider,
        fieldTypeSpecMutatorProvider,
        ctorTypeSpecMutatorProvider,
        documentLoadTypeSpecMutatorProvider,
        documentQueryTypeSpecMutatorProvider);
  }

  public static PojoTypeSpecFactory newPojoTypeSpecFactory() {
    return new PojoTypeSpecFactory();
  }
}
