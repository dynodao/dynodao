package org.lemon.dynodao.processor.serialize;

import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.context.ProcessorMessager;
import org.lemon.dynodao.processor.context.Processors;
import org.lemon.dynodao.processor.dynamo.DynamoStructuredSchema;
import org.lemon.dynodao.processor.serialize.generate.SerializerTypeSpecMutators;
import org.lemon.dynodao.processor.serialize.value.AttributeValueSerializer;
import org.lemon.dynodao.processor.serialize.value.AttributeValueSerializers;

import javax.inject.Inject;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

/**
 * Produces {@link SerializerTypeSpec} from a schema specification.
 */
public class SerializerTypeSpecFactory {

    private final Processors processors;
    private final ProcessorMessager processorMessager;
    private final SerializerTypeSpecMutators serializerTypeSpecMutators;
    private final AttributeValueSerializers attributeValueSerializers;

    @Inject SerializerTypeSpecFactory(Processors processors, ProcessorMessager processorMessager, SerializerTypeSpecMutators serializerTypeSpecMutators, AttributeValueSerializers attributeValueSerializers) {
        this.processors = processors;
        this.processorMessager = processorMessager;
        this.serializerTypeSpecMutators = serializerTypeSpecMutators;
        this.attributeValueSerializers = attributeValueSerializers;
    }

    /**
     * Builds the serialization class for the given document type and the schema it represents.
     * @param document the schema type document
     * @param schema the dynamo schema
     * @return the serialization class for the types in the document
     */
    public SerializerTypeSpec build(TypeElement document, DynamoStructuredSchema schema) {
        SerializationContext serializationContext = buildSerializationContext(document, schema);
        TypeSpec typeSpec = buildTypeSpec(document, serializationContext);
        return new SerializerTypeSpec(typeSpec, serializationContext);
    }

    private SerializationContext buildSerializationContext(TypeElement document, DynamoStructuredSchema schema) {
        SerializationContext serializationContext = new SerializationContext(document, processors);
        addSerializerForType(document.asType(), serializationContext);
        return serializationContext;
    }

    private void addSerializerForType(TypeMirror type, SerializationContext serializationContext) {
        if (!serializationContext.hasSerializerForType(type)) {
            boolean serializerCreated = false;
            for (AttributeValueSerializer serializer : attributeValueSerializers) {
                if (serializer.isApplicableTo(type)) {
                    serializerCreated = true;
                    serializer.getTypeDependencies(type).forEach(dependency -> addSerializerForType(dependency, serializationContext));
                    SerializeMethod method = serializer.serialize(type, serializationContext);
                    serializationContext.addSerializer(type, method);
                    break;
                }
            }
            if (!serializerCreated) {
                submitUnableToSerializeError(type);
            }
        }
    }

    private void submitUnableToSerializeError(TypeMirror type) {
        processorMessager.submitError("Unable to serialize %s to an AttributeValue", type)
                .atElement(processors.asElement(type));
    }

    private TypeSpec buildTypeSpec(TypeElement document, SerializationContext serializationContext) {
        SerializerClassData serializer = new SerializerClassData(document, serializationContext);
        TypeSpec.Builder typeSpec = TypeSpec.classBuilder(document.getSimpleName() + "AttributeValueSerializer");
        serializerTypeSpecMutators.forEach(serializerTypeSpecMutator -> serializerTypeSpecMutator.mutate(typeSpec, serializer));
        return typeSpec.build();
    }

}
