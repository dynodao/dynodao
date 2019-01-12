package org.lemon.dynodao.processor.generate.type;

import dagger.internal.Factory;
import javax.annotation.Generated;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class ModifiersTypeSpecMutator_Factory implements Factory<ModifiersTypeSpecMutator> {
  private static final ModifiersTypeSpecMutator_Factory INSTANCE =
      new ModifiersTypeSpecMutator_Factory();

  @Override
  public ModifiersTypeSpecMutator get() {
    return new ModifiersTypeSpecMutator();
  }

  public static Factory<ModifiersTypeSpecMutator> create() {
    return INSTANCE;
  }

  public static ModifiersTypeSpecMutator newModifiersTypeSpecMutator() {
    return new ModifiersTypeSpecMutator();
  }
}
