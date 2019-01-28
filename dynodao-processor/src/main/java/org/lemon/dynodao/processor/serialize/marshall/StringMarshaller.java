package org.lemon.dynodao.processor.serialize.marshall;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import org.lemon.dynodao.processor.context.Processors;
import org.lemon.dynodao.processor.serialize.MarshallMethod;
import org.lemon.dynodao.processor.serialize.SerializationContext;
import org.lemon.dynodao.processor.serialize.UnmarshallMethod;

import javax.inject.Inject;
import javax.lang.model.type.TypeMirror;
import java.util.Collection;

import static java.util.Collections.emptySet;
import static org.lemon.dynodao.processor.serialize.UnmarshallMethod.parameter;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.attributeValue;

/**
 * Handles serialization of {@link String}.
 * TODO handle {@link CharSequence} instead of String?
 */
class StringMarshaller implements AttributeValueMarshaller {

    private static final ParameterSpec STRING_PARAM = ParameterSpec.builder(String.class, "string").build();
    private static final CodeBlock SERIALIZE_BODY = CodeBlock.builder()
            .addStatement("return new $T().withS($N)", attributeValue(), STRING_PARAM)
            .build();
    private static final MarshallMethod MARSHALL_METHOD = MarshallMethod.builder()
            .methodName("serializeString")
            .parameter(STRING_PARAM)
            .body(SERIALIZE_BODY)
            .build();

    private static final CodeBlock DESERIALIZE_METHOD_BODY = CodeBlock.builder()
            .addStatement("return $N.getS()", parameter())
            .build();
    private static final UnmarshallMethod UNMARSHALL_METHOD = UnmarshallMethod.builder()
            .methodName("deserializeString")
            .body(DESERIALIZE_METHOD_BODY)
            .build();

    private final Processors processors;

    @Inject StringMarshaller(Processors processors) {
        this.processors = processors;
    }

    @Override
    public boolean isApplicableTo(TypeMirror type) {
        return processors.isSameType(type, processors.getDeclaredType(String.class));
    }

    @Override
    public Collection<? extends TypeMirror> getTypeDependencies(TypeMirror type) {
        return emptySet();
    }

    @Override
    public MarshallMethod serialize(TypeMirror type, SerializationContext serializationContext) {
        return MARSHALL_METHOD;
    }

    @Override
    public UnmarshallMethod deserialize(TypeMirror type, SerializationContext serializationContext) {
        return UNMARSHALL_METHOD;
    }

}
