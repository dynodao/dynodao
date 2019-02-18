package org.lemon.dynodao.processor.serialize;

import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.schema.DynamoSchema;
import org.lemon.dynodao.processor.serialize.generate.SerializerTypeSpecMutators;

import javax.inject.Inject;

/**
 * Produces {@link SerializerTypeSpec} from a schema specification.
 */
public class SerializerTypeSpecFactory {

    private final SerializerTypeSpecMutators serializerTypeSpecMutators;

    @Inject SerializerTypeSpecFactory(SerializerTypeSpecMutators serializerTypeSpecMutators) {
        this.serializerTypeSpecMutators = serializerTypeSpecMutators;
    }

    /**
     * Builds the serialization class for the given schema.
     * @param schema the dynamo schema
     * @return the serialization class for the types in the document
     */
    public SerializerTypeSpec build(DynamoSchema schema) {
        TypeSpec.Builder typeSpec = TypeSpec.classBuilder(schema.getDocumentElement().getSimpleName() + "AttributeValueSerializer");
        serializerTypeSpecMutators.forEach(serializerTypeSpecMutator -> serializerTypeSpecMutator.mutate(typeSpec, schema));
        return new SerializerTypeSpec(typeSpec.build(), schema.getDocumentElement());
    }

}
