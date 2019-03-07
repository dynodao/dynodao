package org.lemon.dynodao.processor.schema.parse;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import org.lemon.dynodao.processor.context.Processors;
import org.lemon.dynodao.processor.schema.SchemaContext;
import org.lemon.dynodao.processor.schema.attribute.BooleanDynamoAttribute;
import org.lemon.dynodao.processor.schema.serialize.DeserializationMappingMethod;
import org.lemon.dynodao.processor.schema.serialize.SerializationMappingMethod;

import javax.inject.Inject;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import static org.lemon.dynodao.processor.schema.serialize.DeserializationMappingMethod.parameter;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.attributeValue;
import static org.lemon.dynodao.processor.util.StringUtil.capitalize;

/**
 * Parses {@link Boolean}s and primitive <tt>boolean</tt>s.
 */
class BooleanTypeSchemaParser implements SchemaParser {

    private final Processors processors;

    @Inject BooleanTypeSchemaParser(Processors processors) {
        this.processors = processors;
    }

    @Override
    public boolean isApplicableTo(Element element, TypeMirror typeMirror, SchemaContext schemaContext) {
        return isPrimitiveBoolean(typeMirror) || processors.isSameType(typeMirror, processors.getDeclaredType(Boolean.class));
    }

    private boolean isPrimitiveBoolean(TypeMirror typeMirror) {
        return typeMirror.getKind().equals(TypeKind.BOOLEAN);
    }

    @Override
    public BooleanDynamoAttribute parseAttribute(Element element, TypeMirror typeMirror, String path, SchemaContext schemaContext) {
        return BooleanDynamoAttribute.builder()
                .element(element)
                .typeMirror(typeMirror)
                .path(path)
                .serializationMethod(buildSerializationMethod(typeMirror))
                .deserializationMethod(buildDeserializationMethod(typeMirror))
                .build();
    }

    private SerializationMappingMethod buildSerializationMethod(TypeMirror typeMirror) {
        ParameterSpec parameter = ParameterSpec.builder(TypeName.get(typeMirror), "bool").build();
        CodeBlock body = CodeBlock.builder()
                .addStatement("return new $T().withBOOL($N)", attributeValue(), parameter)
                .build();

        return SerializationMappingMethod.builder()
                .methodName(methodName("serialize", typeMirror))
                .parameter(parameter)
                .coreMethodBody(body)
                .build();
    }

    private String methodName(String prefix, TypeMirror typeMirror) {
        if (isPrimitiveBoolean(typeMirror)) {
            return prefix + "Primitive" + capitalize(typeMirror.toString());
        } else {
            return prefix + processors.asElement(typeMirror).getSimpleName();
        }
    }

    private DeserializationMappingMethod buildDeserializationMethod(TypeMirror typeMirror) {
        CodeBlock body = CodeBlock.builder()
                .addStatement("return $N.getBOOL()", parameter())
                .build();

        return DeserializationMappingMethod.builder()
                .methodName(methodName("deserialize", typeMirror))
                .returnType(TypeName.get(typeMirror))
                .coreMethodBody(body)
                .build();
    }

}
