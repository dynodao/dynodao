package org.lemon.dynodao.processor.generate.method;

import dagger.internal.Factory;
import javax.annotation.Generated;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class CtorTypeGenerator_Factory implements Factory<CtorTypeGenerator> {
  private static final CtorTypeGenerator_Factory INSTANCE = new CtorTypeGenerator_Factory();

  @Override
  public CtorTypeGenerator get() {
    return new CtorTypeGenerator();
  }

  public static Factory<CtorTypeGenerator> create() {
    return INSTANCE;
  }

  public static CtorTypeGenerator newCtorTypeGenerator() {
    return new CtorTypeGenerator();
  }
}
