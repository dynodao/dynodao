package org.lemon.dynodao.processor.serialize.marshall;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.sun.tools.javac.jvm.Code;
import org.lemon.dynodao.processor.context.Processors;
import org.lemon.dynodao.processor.serialize.MarshallMethod;
import org.lemon.dynodao.processor.serialize.SerializationContext;
import org.lemon.dynodao.processor.serialize.UnmarshallMethod;

import javax.inject.Inject;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.lemon.dynodao.processor.serialize.UnmarshallMethod.parameter;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.attributeValue;

/**
 * Handles {@code Map<String, T>} serialization. Only {@link String} keys are allowed, since
 * {@link com.amazonaws.services.dynamodbv2.model.AttributeValue#getM()} is also keyed by String.
 * The value type is delegated to the {@link SerializationContext}, the method created therein.
 */
class MapMarshaller implements AttributeValueMarshaller {

    private static final TypeName MAP_OF_ATTRIBUTE_VALUE = ParameterizedTypeName.get(ClassName.get(Map.class), TypeName.get(String.class), attributeValue());
    private static final TypeName MAP_ENTRY_OF_ATTRIBUTE_VALUE = ParameterizedTypeName.get(ClassName.get(Map.Entry.class), TypeName.get(String.class), attributeValue());
    private static final TypeName MAP_OF_ATTRIBUTE_VALUE_ITERATOR = ParameterizedTypeName.get(ClassName.get(Iterator.class), MAP_ENTRY_OF_ATTRIBUTE_VALUE);

    private final Processors processors;

    @Inject MapMarshaller(Processors processors) {
        this.processors = processors;
    }

    @Override
    public boolean isApplicableTo(TypeMirror type) {
        TypeElement map = processors.getTypeElement(Map.class);
        TypeMirror string = processors.getDeclaredType(String.class);
        TypeMirror wildcard = processors.getWildcardType(null, null);
        TypeMirror mapOfStringToAnything = processors.getDeclaredType(map, string, wildcard);
        return processors.isAssignable(type, mapOfStringToAnything);
    }

    @Override
    public Collection<? extends TypeMirror> getTypeDependencies(TypeMirror type) {
        return ((DeclaredType) type).getTypeArguments();
    }

    @Override
    public MarshallMethod serialize(TypeMirror type, SerializationContext serializationContext) {
        return serialize((DeclaredType) type, serializationContext);
    }

    private MarshallMethod serialize(DeclaredType type, SerializationContext serializationContext) {
        ParameterSpec param = getParameter(type);
        CodeBlock body = CodeBlock.builder()
                .addStatement("$T attrValueMap = new $T<>()", MAP_OF_ATTRIBUTE_VALUE, HashMap.class)
                .addStatement("$T it = $N.entrySet().iterator()", getIteratorOf(type), param)
                .beginControlFlow("while (it.hasNext())")
                .addStatement("$T entry = it.next()", getMapEntryOf(type))
                .addStatement("attrValueMap.put(entry.getKey(), $L(entry.getValue()))", getValueSerializeMethodName(type, serializationContext))
                .endControlFlow()
                .addStatement("return new $T().withM(attrValueMap)", attributeValue())
                .build();
        return MarshallMethod.builder()
                .methodName(getMethodName("serialize", type))
                .parameter(param)
                .body(body)
                .build();
    }

    private ParameterSpec getParameter(DeclaredType type) {
        return ParameterSpec.builder(TypeName.get(type), "map").build();
    }

    /**
     * TODO handle nested template type names
     */
    private String getMethodName(String prefix, DeclaredType type) {
        TypeMirror ofArg = type.getTypeArguments().get(1);
        return prefix + "MapOf" + processors.asElement(ofArg).getSimpleName();
    }

    private TypeName getIteratorOf(DeclaredType type) {
        return ParameterizedTypeName.get(ClassName.get(Iterator.class), getMapEntryOf(type));
    }

    private TypeName getMapEntryOf(DeclaredType type) {
        return ParameterizedTypeName.get(ClassName.get(Map.Entry.class), type.getTypeArguments().stream()
                .map(TypeName::get)
                .toArray(TypeName[]::new));
    }

    private String getValueSerializeMethodName(DeclaredType type, SerializationContext serializationContext) {
        TypeMirror ofArg = type.getTypeArguments().get(1);
        return serializationContext.getMarshallMethodForType(ofArg).getMethodName();
    }

    @Override
    public UnmarshallMethod deserialize(TypeMirror type, SerializationContext serializationContext) {
        return deserialize((DeclaredType) type, serializationContext);
    }

    private UnmarshallMethod deserialize(DeclaredType type, SerializationContext serializationContext) {
        CodeBlock body = CodeBlock.builder()
                .addStatement("$T map = new $T<>()", type, HashMap.class)
                .addStatement("$T it = $N.getM().entrySet().iterator()", MAP_OF_ATTRIBUTE_VALUE_ITERATOR, parameter())
                .beginControlFlow("while (it.hasNext())")
                .addStatement("$T entry = it.next()", MAP_ENTRY_OF_ATTRIBUTE_VALUE)
                .addStatement("map.put(entry.getKey(), $L(entry.getValue()))", getValueDeserializeMethodName(type, serializationContext))
                .endControlFlow()
                .addStatement("return map")
                .build();
        return UnmarshallMethod.builder()
                .methodName(getMethodName("deserialize", type))
                .body(body)
                .returnType(TypeName.get(type))
                .build();
    }

    private String getValueDeserializeMethodName(DeclaredType type, SerializationContext serializationContext) {
        TypeMirror ofArg = type.getTypeArguments().get(1);
        return serializationContext.getUnmarshallMethodForType(ofArg).getMethodName();
    }

}
