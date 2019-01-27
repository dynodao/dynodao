package org.lemon.dynodao.processor.serialize.generate;

import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.serialize.SerializerClassData;

import javax.inject.Inject;

import static org.lemon.dynodao.processor.util.DynoDaoUtil.generatedAnnotation;

/**
 * Adds the {@link javax.annotation.Generated} annotation to the class being generated.
 */
class GeneratedAnnotationSerializerTypeSpecMutator implements SerializerTypeSpecMutator {

    @Inject
    GeneratedAnnotationSerializerTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, SerializerClassData serializerClassData) {
        typeSpec.addAnnotation(generatedAnnotation());
    }

}
