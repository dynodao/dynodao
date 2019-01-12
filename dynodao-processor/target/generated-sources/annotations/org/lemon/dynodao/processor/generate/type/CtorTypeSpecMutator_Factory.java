package org.lemon.dynodao.processor.generate.type;

import dagger.internal.Factory;
import javax.annotation.Generated;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class CtorTypeSpecMutator_Factory implements Factory<CtorTypeSpecMutator> {
  private static final CtorTypeSpecMutator_Factory INSTANCE = new CtorTypeSpecMutator_Factory();

  @Override
  public CtorTypeSpecMutator get() {
    return new CtorTypeSpecMutator();
  }

  public static Factory<CtorTypeSpecMutator> create() {
    return INSTANCE;
  }

  public static CtorTypeSpecMutator newCtorTypeSpecMutator() {
    return new CtorTypeSpecMutator();
  }
}
