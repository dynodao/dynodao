package org.lemon.dynodao.processor.generate.type;

import dagger.MembersInjector;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class PojoTypeSpecFactory_MembersInjector
    implements MembersInjector<PojoTypeSpecFactory> {
  private final Provider<ModifiersTypeSpecMutator> modifiersTypeSpecMutatorProvider;

  private final Provider<SuperInterfaceTypeSpecMutator> superInterfaceTypeSpecMutatorProvider;

  private final Provider<FieldTypeSpecMutator> fieldTypeSpecMutatorProvider;

  private final Provider<CtorTypeSpecMutator> ctorTypeSpecMutatorProvider;

  private final Provider<DocumentLoadTypeSpecMutator> documentLoadTypeSpecMutatorProvider;

  private final Provider<DocumentQueryTypeSpecMutator> documentQueryTypeSpecMutatorProvider;

  public PojoTypeSpecFactory_MembersInjector(
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

  public static MembersInjector<PojoTypeSpecFactory> create(
      Provider<ModifiersTypeSpecMutator> modifiersTypeSpecMutatorProvider,
      Provider<SuperInterfaceTypeSpecMutator> superInterfaceTypeSpecMutatorProvider,
      Provider<FieldTypeSpecMutator> fieldTypeSpecMutatorProvider,
      Provider<CtorTypeSpecMutator> ctorTypeSpecMutatorProvider,
      Provider<DocumentLoadTypeSpecMutator> documentLoadTypeSpecMutatorProvider,
      Provider<DocumentQueryTypeSpecMutator> documentQueryTypeSpecMutatorProvider) {
    return new PojoTypeSpecFactory_MembersInjector(
        modifiersTypeSpecMutatorProvider,
        superInterfaceTypeSpecMutatorProvider,
        fieldTypeSpecMutatorProvider,
        ctorTypeSpecMutatorProvider,
        documentLoadTypeSpecMutatorProvider,
        documentQueryTypeSpecMutatorProvider);
  }

  @Override
  public void injectMembers(PojoTypeSpecFactory instance) {
    injectModifiersTypeSpecMutator(instance, modifiersTypeSpecMutatorProvider.get());
    injectSuperInterfaceTypeSpecMutator(instance, superInterfaceTypeSpecMutatorProvider.get());
    injectFieldTypeSpecMutator(instance, fieldTypeSpecMutatorProvider.get());
    injectCtorTypeSpecMutator(instance, ctorTypeSpecMutatorProvider.get());
    injectDocumentLoadTypeSpecMutator(instance, documentLoadTypeSpecMutatorProvider.get());
    injectDocumentQueryTypeSpecMutator(instance, documentQueryTypeSpecMutatorProvider.get());
  }

  public static void injectModifiersTypeSpecMutator(
      PojoTypeSpecFactory instance, Object modifiersTypeSpecMutator) {
    instance.modifiersTypeSpecMutator = (ModifiersTypeSpecMutator) modifiersTypeSpecMutator;
  }

  public static void injectSuperInterfaceTypeSpecMutator(
      PojoTypeSpecFactory instance, Object superInterfaceTypeSpecMutator) {
    instance.superInterfaceTypeSpecMutator =
        (SuperInterfaceTypeSpecMutator) superInterfaceTypeSpecMutator;
  }

  public static void injectFieldTypeSpecMutator(
      PojoTypeSpecFactory instance, Object fieldTypeSpecMutator) {
    instance.fieldTypeSpecMutator = (FieldTypeSpecMutator) fieldTypeSpecMutator;
  }

  public static void injectCtorTypeSpecMutator(
      PojoTypeSpecFactory instance, Object ctorTypeSpecMutator) {
    instance.ctorTypeSpecMutator = (CtorTypeSpecMutator) ctorTypeSpecMutator;
  }

  public static void injectDocumentLoadTypeSpecMutator(
      PojoTypeSpecFactory instance, Object documentLoadTypeSpecMutator) {
    instance.documentLoadTypeSpecMutator =
        (DocumentLoadTypeSpecMutator) documentLoadTypeSpecMutator;
  }

  public static void injectDocumentQueryTypeSpecMutator(
      PojoTypeSpecFactory instance, Object documentQueryTypeSpecMutator) {
    instance.documentQueryTypeSpecMutator =
        (DocumentQueryTypeSpecMutator) documentQueryTypeSpecMutator;
  }
}
