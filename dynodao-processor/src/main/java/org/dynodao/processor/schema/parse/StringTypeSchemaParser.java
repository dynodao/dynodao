package org.dynodao.processor.schema.parse;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import org.dynodao.processor.context.Processors;
import org.dynodao.processor.schema.SchemaContext;
import org.dynodao.processor.schema.attribute.StringDynamoAttribute;
import org.dynodao.processor.schema.serialize.DeserializationMappingMethod;
import org.dynodao.processor.schema.serialize.SerializationMappingMethod;

import javax.inject.Inject;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import static org.dynodao.processor.schema.serialize.DeserializationMappingMethod.parameter;
import static org.dynodao.processor.util.DynamoDbUtil.attributeValue;

/**
 * Parses {@link String}s.
 */
class StringTypeSchemaParser implements SchemaParser {

    private final Processors processors;

    @Inject StringTypeSchemaParser(Processors processors) {
        this.processors = processors;
    }

    @Override
    public boolean isApplicableTo(Element element, TypeMirror typeMirror, SchemaContext schemaContext) {
        return processors.isSameType(typeMirror, processors.getDeclaredType(String.class));
    }

    @Override
    public StringDynamoAttribute parseAttribute(Element element, TypeMirror typeMirror, String path, SchemaContext schemaContext) {
        return StringDynamoAttribute.builder()
                .element(element)
                .typeMirror(typeMirror)
                .path(path)
                .serializationMethod(buildSerializationMethod())
                .deserializationMethod(buildDeserializationMethod(typeMirror))
                .build();
    }

    private SerializationMappingMethod buildSerializationMethod() {
        ParameterSpec parameter = ParameterSpec.builder(String.class, "string").build();
        CodeBlock.Builder body = CodeBlock.builder()
                .addStatement("return new $T().withS($N)", attributeValue(), parameter);
        return SerializationMappingMethod.builder()
                .methodName("serializeString")
                .parameter(parameter)
                .coreMethodBody(body.build())
                .build();
    }

    private DeserializationMappingMethod buildDeserializationMethod(TypeMirror typeMirror) {
        CodeBlock.Builder body = CodeBlock.builder()
                .addStatement("return $N.getS()", parameter());
        return DeserializationMappingMethod.builder()
                .methodName("deserializeString")
                .returnType(TypeName.get(typeMirror))
                .coreMethodBody(body.build())
                .build();
    }

}
