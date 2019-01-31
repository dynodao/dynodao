package org.lemon.dynodao.processor.serialize.marshall;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import org.lemon.dynodao.processor.context.Processors;
import org.lemon.dynodao.processor.serialize.MarshallMethod;
import org.lemon.dynodao.processor.serialize.SerializationContext;
import org.lemon.dynodao.processor.serialize.UnmarshallMethod;

import javax.inject.Inject;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.Collection;

import static java.util.Collections.emptySet;
import static org.lemon.dynodao.processor.serialize.UnmarshallMethod.parameter;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.attributeValue;
import static org.lemon.dynodao.processor.util.StringUtil.capitalize;

/**
 * Handles numeric serialization to {@link com.amazonaws.services.dynamodbv2.model.AttributeValue}.
 * Accepts primitive and boxed types; anything assignable to {@link Number}.
 */
class NumericMarshaller implements AttributeValueMarshaller {

    private final Processors processors;

    @Inject NumericMarshaller(Processors processors) {
        this.processors = processors;
    }

    @Override
    public boolean isApplicableTo(TypeMirror type) {
        return processors.isAssignable(toBoxedType(type), processors.getDeclaredType(Number.class));
    }

    private TypeMirror toBoxedType(TypeMirror type) {
        if (type.getKind().isPrimitive()) {
            return processors.boxedClass((PrimitiveType) type).asType();
        } else {
            return type;
        }
    }

    @Override
    public Collection<? extends TypeMirror> getTypeDependencies(TypeMirror type) {
        return emptySet();
    }

    @Override
    public MarshallMethod serialize(TypeMirror type, SerializationContext serializationContext) {
        ParameterSpec numberParam = getParameter(type);
        CodeBlock body = CodeBlock.builder()
                .addStatement("return new $T().withN($T.valueOf($N))", attributeValue(), String.class, numberParam)
                .build();
        return MarshallMethod.builder()
                .methodName(methodName("serialize", type))
                .parameter(numberParam)
                .body(body)
                .build();
    }

    private ParameterSpec getParameter(TypeMirror type) {
        return ParameterSpec.builder(TypeName.get(type), "n").build();
    }

    private String methodName(String prefix, TypeMirror type) {
        if (type.getKind().isPrimitive()) {
            return prefix + "Primitive" + capitalize(type.toString());
        } else {
            return prefix + processors.asElement(type).getSimpleName();
        }
    }

    @Override
    public UnmarshallMethod deserialize(TypeMirror type, SerializationContext serializationContext) {
        return UnmarshallMethod.builder()
                .methodName(methodName("deserialize", type))
                .body(deserializeBody(type))
                .returnType(TypeName.get(type))
                .expectedPresentAttribute("N")
                .build();
    }

    private CodeBlock deserializeBody(TypeMirror type) {
        if (type.getKind().isPrimitive()) {
            TypeMirror boxed = processors.boxedClass((PrimitiveType) type).asType();
            return CodeBlock.builder()
                    .addStatement("return $T.$L($N.getN())", boxed, "parse" + capitalize(type.toString()), parameter())
                    .build();
        } else if (isBoxed(type)) {
            return CodeBlock.builder()
                    .addStatement("return $T.valueOf($N.getN())", type, parameter())
                    .build();
        } else { // BigInteger, BigDecimal
            return CodeBlock.builder()
                    .addStatement("return new $T($N.getN())", type, parameter())
                    .build();
        }
    }

    private boolean isBoxed(TypeMirror type) {
        return processors.isSameType(type, box(TypeKind.BYTE))
                || processors.isSameType(type, box(TypeKind.SHORT))
                || processors.isSameType(type, box(TypeKind.INT))
                || processors.isSameType(type, box(TypeKind.LONG))
                || processors.isSameType(type, box(TypeKind.FLOAT))
                || processors.isSameType(type, box(TypeKind.DOUBLE));
    }

    private TypeMirror box(TypeKind typeKind) {
        return processors.boxedClass(processors.getPrimitiveType(typeKind)).asType();
    }

}
