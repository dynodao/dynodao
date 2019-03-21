package org.dynodao.processor.schema.parse;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import org.dynodao.processor.context.Processors;
import org.dynodao.processor.schema.SchemaContext;
import org.dynodao.processor.schema.attribute.NumberDynamoAttribute;
import org.dynodao.processor.schema.serialize.DeserializationMappingMethod;
import org.dynodao.processor.schema.serialize.SerializationMappingMethod;

import javax.inject.Inject;
import javax.lang.model.element.Element;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;

import static org.dynodao.processor.schema.serialize.DeserializationMappingMethod.parameter;
import static org.dynodao.processor.util.DynamoDbUtil.attributeValue;
import static org.dynodao.processor.util.StringUtil.capitalize;

/**
 * Parses primitive numeric types, their boxed versions, as well as {@link BigInteger} and {@link BigDecimal}.
 */
class NumericTypeSchemaParser implements SchemaParser {

    private static final Collection<TypeKind> NUMERIC_PRIMITIVES = Arrays.asList(TypeKind.BYTE, TypeKind.SHORT, TypeKind.INT,
            TypeKind.LONG, TypeKind.FLOAT, TypeKind.DOUBLE);

    private final Processors processors;

    @Inject NumericTypeSchemaParser(Processors processors) {
        this.processors = processors;
    }

    @Override
    public boolean isApplicableTo(Element element, TypeMirror typeMirror, SchemaContext schemaContext) {
        return isNumericPrimitive(typeMirror) || isBoxedNumericPrimitive(typeMirror)
                || isBigInteger(typeMirror) || isBigDecimal(typeMirror);
    }

    private boolean isNumericPrimitive(TypeMirror typeMirror) {
        return NUMERIC_PRIMITIVES.contains(typeMirror.getKind());
    }

    private boolean isBoxedNumericPrimitive(TypeMirror typeMirror) {
        return NUMERIC_PRIMITIVES.stream()
                .map(typeKind -> processors.boxedClass(processors.getPrimitiveType(typeKind)).asType())
                .anyMatch(boxed -> processors.isSameType(typeMirror, boxed));
    }

    private boolean isBigInteger(TypeMirror typeMirror) {
        return processors.isSameType(typeMirror, BigInteger.class);
    }

    private boolean isBigDecimal(TypeMirror typeMirror) {
        return processors.isSameType(typeMirror, BigDecimal.class);
    }

    @Override
    public NumberDynamoAttribute parseAttribute(Element element, TypeMirror typeMirror, String path, SchemaContext schemaContext) {
        return NumberDynamoAttribute.builder()
                .element(element)
                .typeMirror(typeMirror)
                .path(path)
                .serializationMethod(buildSerializationMethod(typeMirror))
                .deserializationMethod(buildDeserializationMethod(typeMirror))
                .build();
    }

    private SerializationMappingMethod buildSerializationMethod(TypeMirror typeMirror) {
        ParameterSpec parameter = ParameterSpec.builder(TypeName.get(typeMirror), "n").build();
        CodeBlock.Builder body = CodeBlock.builder();

        if (isBigDecimal(typeMirror)) {
            body.addStatement("return new $T().withN($N.toPlainString())", attributeValue(), parameter);
        } else {
            body.addStatement("return new $T().withN($T.valueOf($N))", attributeValue(), String.class, parameter);
        }
        return SerializationMappingMethod.builder()
                .methodName(methodName("serialize", typeMirror))
                .parameter(parameter)
                .coreMethodBody(body.build())
                .build();
    }

    private String methodName(String prefix, TypeMirror typeMirror) {
        if (typeMirror.getKind().isPrimitive()) {
            return prefix + "Primitive" + capitalize(typeMirror.toString());
        } else {
            return prefix + processors.asElement(typeMirror).getSimpleName();
        }
    }

    private DeserializationMappingMethod buildDeserializationMethod(TypeMirror typeMirror) {
        CodeBlock.Builder body = CodeBlock.builder();

        if (isNumericPrimitive(typeMirror)) {
            TypeMirror boxed = processors.boxedClass((PrimitiveType) typeMirror).asType();
            body.addStatement("return $T.$L($N.getN())", boxed, "parse" + capitalize(typeMirror.toString()), parameter());
        } else if (isBoxedNumericPrimitive(typeMirror)) {
            body.addStatement("return $T.valueOf($N.getN())", typeMirror, parameter());
        } else if (isBigInteger(typeMirror) || isBigDecimal(typeMirror)) {
            body.addStatement("return new $T($N.getN())", typeMirror, parameter());
        }

        return DeserializationMappingMethod.builder()
                .methodName(methodName("deserialize", typeMirror))
                .returnType(TypeName.get(typeMirror))
                .coreMethodBody(body.build())
                .build();
    }

}
