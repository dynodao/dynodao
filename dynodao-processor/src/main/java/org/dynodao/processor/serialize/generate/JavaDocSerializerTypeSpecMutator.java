package org.dynodao.processor.serialize.generate;

import com.squareup.javapoet.TypeSpec;
import org.dynodao.processor.schema.DynamoSchema;

import javax.inject.Inject;

import static org.dynodao.processor.util.DynamoDbUtil.attributeValue;

/**
 * Adds the class javadoc to the serializer.
 */
class JavaDocSerializerTypeSpecMutator implements SerializerTypeSpecMutator {

    @Inject JavaDocSerializerTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, DynamoSchema schema) {
        typeSpec.addJavadoc("Utility class for converting types within {@link $T} into {@link $T}.\n",
                schema.getDocument().getTypeMirror(), attributeValue());
    }

}
