package org.lemon.dynodao.processor.serialize.value;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import org.lemon.dynodao.processor.context.Processors;
import org.lemon.dynodao.processor.serialize.SerializationContext;
import org.lemon.dynodao.processor.serialize.SerializeMethod;

import javax.inject.Inject;
import javax.lang.model.type.TypeMirror;

import java.util.Collection;

import static java.util.Collections.emptySet;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.attributeValue;

/**
 * Handles serialization of {@link String}.
 * TODO handle {@link CharSequence} instead of String?
 */
class StringSerializer implements AttributeValueSerializer {

    private static final String METHOD_NAME = "serializeString";
    private static final ParameterSpec STRING_PARAM = ParameterSpec.builder(String.class, "string").build();
    private static final CodeBlock METHOD_BODY = CodeBlock.builder()
            .addStatement("return new $T().withS($N)", attributeValue(), STRING_PARAM)
            .build();
    private static final SerializeMethod SERIALIZE_METHOD = SerializeMethod.builder()
            .methodName(METHOD_NAME)
            .parameter(STRING_PARAM)
            .body(METHOD_BODY)
            .build();

    @Inject Processors processors;

    @Inject StringSerializer() { }

    @Override
    public boolean isApplicableTo(TypeMirror type) {
        return processors.isSameType(type, processors.getDeclaredType(String.class));
    }

    @Override
    public Collection<? extends TypeMirror> getTypeDependencies(TypeMirror type) {
        return emptySet();
    }

    @Override
    public SerializeMethod serialize(TypeMirror type, SerializationContext serializationContext) {
        return SERIALIZE_METHOD;
    }

}
