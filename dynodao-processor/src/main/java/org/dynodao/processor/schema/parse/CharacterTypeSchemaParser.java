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
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import static org.dynodao.processor.schema.serialize.DeserializationMappingMethod.parameter;
import static org.dynodao.processor.util.DynamoDbUtil.attributeValue;
import static org.dynodao.processor.util.StringUtil.capitalize;

/**
 * Parses {@link Character}s and primitive <tt>char</tt>s to string attributes.
 */
class CharacterTypeSchemaParser implements SchemaParser {

    private final Processors processors;

    @Inject CharacterTypeSchemaParser(Processors processors) {
        this.processors = processors;
    }

    @Override
    public boolean isApplicableTo(Element element, TypeMirror typeMirror, SchemaContext schemaContext) {
        return isPrimitiveCharacter(typeMirror) || processors.isSameType(typeMirror, processors.getDeclaredType(Character.class));
    }

    private boolean isPrimitiveCharacter(TypeMirror typeMirror) {
        return typeMirror.getKind().equals(TypeKind.CHAR);
    }

    @Override
    public StringDynamoAttribute parseAttribute(Element element, TypeMirror typeMirror, String path, SchemaContext schemaContext) {
        return StringDynamoAttribute.builder()
                .element(element)
                .typeMirror(typeMirror)
                .path(path)
                .serializationMethod(buildSerializationMethod(typeMirror))
                .deserializationMethod(buildDeserializationMethod(typeMirror))
                .build();
    }

    private SerializationMappingMethod buildSerializationMethod(TypeMirror typeMirror) {
        ParameterSpec parameter = ParameterSpec.builder(TypeName.get(typeMirror), "c").build();
        CodeBlock body = CodeBlock.builder()
                .addStatement("return new $T().withS($T.valueOf($N))", attributeValue(), String.class, parameter)
                .build();

        return SerializationMappingMethod.builder()
                .methodName(methodName("serialize", typeMirror))
                .parameter(parameter)
                .coreMethodBody(body)
                .build();
    }

    private String methodName(String prefix, TypeMirror typeMirror) {
        if (isPrimitiveCharacter(typeMirror)) {
            return prefix + "Primitive" + capitalize(typeMirror.toString());
        } else {
            return prefix + processors.asElement(typeMirror).getSimpleName();
        }
    }

    private DeserializationMappingMethod buildDeserializationMethod(TypeMirror typeMirror) {
        CodeBlock body = CodeBlock.builder()
                .addStatement("return $N.getS().charAt(0)", parameter())
                .build();

        return DeserializationMappingMethod.builder()
                .methodName(methodName("deserialize", typeMirror))
                .returnType(TypeName.get(typeMirror))
                .coreMethodBody(body)
                .build();
    }

}
