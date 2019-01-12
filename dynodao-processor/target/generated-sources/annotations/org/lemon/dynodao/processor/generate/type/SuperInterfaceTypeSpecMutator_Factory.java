package org.lemon.dynodao.processor.generate.type;

import dagger.internal.Factory;
import javax.annotation.Generated;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class SuperInterfaceTypeSpecMutator_Factory
    implements Factory<SuperInterfaceTypeSpecMutator> {
  private static final SuperInterfaceTypeSpecMutator_Factory INSTANCE =
      new SuperInterfaceTypeSpecMutator_Factory();

  @Override
  public SuperInterfaceTypeSpecMutator get() {
    return new SuperInterfaceTypeSpecMutator();
  }

  public static Factory<SuperInterfaceTypeSpecMutator> create() {
    return INSTANCE;
  }

  public static SuperInterfaceTypeSpecMutator newSuperInterfaceTypeSpecMutator() {
    return new SuperInterfaceTypeSpecMutator();
  }
}
