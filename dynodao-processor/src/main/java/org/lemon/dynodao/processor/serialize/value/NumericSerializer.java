package org.lemon.dynodao.processor.serialize.value;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import org.lemon.dynodao.processor.context.Processors;
import org.lemon.dynodao.processor.serialize.SerializationContext;
import org.lemon.dynodao.processor.serialize.SerializeMethod;

import javax.inject.Inject;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import java.util.Collection;

import static java.util.Collections.emptySet;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.attributeValue;
import static org.lemon.dynodao.processor.util.StringUtil.toClassCase;

/**
 * Handles numeric serialization to {@link com.amazonaws.services.dynamodbv2.model.AttributeValue}.
 * Accepts primitive and boxed types; anything assignable to {@link Number}.
 */
class NumericSerializer implements AttributeValueSerializer {

    private final Processors processors;

    @Inject NumericSerializer(Processors processors) {
        this.processors = processors;
    }

    @Override
    public boolean isApplicableTo(TypeMirror type) {
        return processors.isAssignable(toBoxedType(type), processors.getDeclaredType(Number.class));
    }

    @Override
    public Collection<? extends TypeMirror> getTypeDependencies(TypeMirror type) {
        return emptySet();
    }

    private TypeMirror toBoxedType(TypeMirror type) {
        if (type.getKind().isPrimitive()) {
            return processors.boxedClass((PrimitiveType) type).asType();
        } else {
            return type;
        }
    }

    @Override
    public SerializeMethod serialize(TypeMirror type, SerializationContext serializationContext) {
        ParameterSpec numberParam = getParameter(type);
        CodeBlock body = CodeBlock.builder()
                .addStatement("return new $T().withN($T.valueOf($N))", attributeValue(), String.class, numberParam)
                .build();
        return SerializeMethod.builder()
                .methodName(getMethodName(type))
                .parameter(numberParam)
                .body(body)
                .build();
    }

    private ParameterSpec getParameter(TypeMirror type) {
        return ParameterSpec.builder(TypeName.get(type), "n").build();
    }

    private String getMethodName(TypeMirror type) {
        if (type.getKind().isPrimitive()) {
            return "serializePrimitive" + toClassCase(type.toString());
        } else {
            return "serialize" + toClassCase(processors.asElement(type).getSimpleName());
        }
    }

}
