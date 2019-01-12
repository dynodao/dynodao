package org.lemon.dynodao.processor.generate.type;

import dagger.internal.Factory;
import javax.annotation.Generated;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class FieldTypeSpecMutator_Factory implements Factory<FieldTypeSpecMutator> {
  private static final FieldTypeSpecMutator_Factory INSTANCE = new FieldTypeSpecMutator_Factory();

  @Override
  public FieldTypeSpecMutator get() {
    return new FieldTypeSpecMutator();
  }

  public static Factory<FieldTypeSpecMutator> create() {
    return INSTANCE;
  }

  public static FieldTypeSpecMutator newFieldTypeSpecMutator() {
    return new FieldTypeSpecMutator();
  }
}
