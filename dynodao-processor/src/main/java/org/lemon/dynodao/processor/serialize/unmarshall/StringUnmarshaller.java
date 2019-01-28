package org.lemon.dynodao.processor.serialize.unmarshall;

import com.squareup.javapoet.CodeBlock;
import org.lemon.dynodao.processor.context.Processors;
import org.lemon.dynodao.processor.serialize.SerializationContext;
import org.lemon.dynodao.processor.serialize.UnmarshallMethod;

import javax.inject.Inject;
import javax.lang.model.type.TypeMirror;
import java.util.Collection;

import static java.util.Collections.emptySet;
import static org.lemon.dynodao.processor.serialize.UnmarshallMethod.parameter;

/**
 * Handles unmarshalling of {@link String}.
 */
class StringUnmarshaller implements AttributeValueUnmarshaller {

    private static final String METHOD_NAME = "deserializeString";
    private static final CodeBlock METHOD_BODY = CodeBlock.builder()
            .addStatement("return $N.getS()", parameter())
            .build();
    private static final UnmarshallMethod UNMARSHALL_METHOD = UnmarshallMethod.builder()
            .methodName(METHOD_NAME)
            .body(METHOD_BODY)
            .build();

    private final Processors processors;

    @Inject StringUnmarshaller(Processors processors) {
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
    public UnmarshallMethod deserialize(TypeMirror type, SerializationContext serializationContext) {
        return UNMARSHALL_METHOD;
    }
}
