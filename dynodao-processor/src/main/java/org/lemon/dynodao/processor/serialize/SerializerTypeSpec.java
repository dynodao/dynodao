package org.lemon.dynodao.processor.serialize;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Value;
import org.lemon.dynodao.processor.BuiltTypeSpec;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

/**
 * Represents the serialization class, which converts types into {@link com.amazonaws.services.dynamodbv2.model.AttributeValue}.
 */
@Value
public class SerializerTypeSpec implements BuiltTypeSpec {

    private final TypeSpec typeSpec;
    @Getter(AccessLevel.NONE) private final SerializationContext serializationContext;

    @Override
    public TypeElement getDocument() {
        return serializationContext.getDocument();
    }

    /**
     * Returns the method which can serialize the type, or <tt>null</tt> if the type is not able to be serialized.
     * @param type the type
     * @return the method which serializes the type, or <tt>null</tt> if no such method exists
     */
    public SerializeMethod getSerializationMethodForType(TypeMirror type) {
        return serializationContext.getSerializationMethodForType(type);
    }

    public MethodSpec getSerializationMethodSpecForType(TypeMirror type) {
        SerializeMethod method = getSerializationMethodForType(type);
        return typeSpec.methodSpecs.stream()
                .filter(spec -> spec.name.equals(method.getMethodName()))
                .findAny().orElse(null);
    }

}
