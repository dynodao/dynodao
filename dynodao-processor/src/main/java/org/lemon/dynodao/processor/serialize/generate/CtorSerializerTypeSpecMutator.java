package org.lemon.dynodao.processor.serialize.generate;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.serialize.SerializerClassData;

import javax.inject.Inject;
import javax.lang.model.element.Modifier;

/**
 * Adds a private ctor to the serializer being built.
 */
class CtorSerializerTypeSpecMutator implements SerializerTypeSpecMutator {

    @Inject CtorSerializerTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, SerializerClassData serializerClassData) {
        MethodSpec ctor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addStatement("throw new $T($S)", UnsupportedOperationException.class, "This is a utility class and cannot be instantiated.")
                .build();
        typeSpec.addMethod(ctor);
    }

}
