package com.github.dynodao.processor.serialize.generate;

import com.github.dynodao.processor.schema.DynamoSchema;
import com.squareup.javapoet.TypeSpec;

import javax.inject.Inject;

import static com.github.dynodao.processor.util.DynoDaoUtil.generatedAnnotation;

/**
 * Adds the {@link javax.annotation.Generated} annotation to the class being generated.
 */
class GeneratedAnnotationSerializerTypeSpecMutator implements SerializerTypeSpecMutator {

    @Inject GeneratedAnnotationSerializerTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, DynamoSchema schema) {
        typeSpec.addAnnotation(generatedAnnotation());
    }

}
