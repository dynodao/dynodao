package org.lemon.dynodao.processor.serialize.generate;

import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.serialize.SerializerClassData;

import javax.inject.Inject;

import static org.lemon.dynodao.processor.util.DynamoDbUtil.attributeValue;

/**
 * Adds the class javadoc to the serializer.
 */
class JavaDocSerializerTypeSpecMutator implements SerializerTypeSpecMutator {

    @Inject JavaDocSerializerTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, SerializerClassData serializerClassData) {
        typeSpec.addJavadoc("Utility class for converting types within {@link $T} into {@link $T}.\n",
                        serializerClassData.getDocument().asType(), attributeValue());
    }

}
