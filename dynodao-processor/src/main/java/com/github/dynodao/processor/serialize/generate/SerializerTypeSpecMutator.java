package com.github.dynodao.processor.serialize.generate;

import com.squareup.javapoet.TypeSpec;
import com.github.dynodao.processor.schema.DynamoSchema;

/**
 * Builds a component of {@link TypeSpec} for the serializer utility class.
 */
public interface SerializerTypeSpecMutator {

    /**
     * Mutates the <tt>typeSpec</tt> to add necessary methods, fields, etc.
     * @param typeSpec the type the utility class is being built into
     * @param schema the schema for which the class is being built
     */
    void mutate(TypeSpec.Builder typeSpec, DynamoSchema schema);
}
