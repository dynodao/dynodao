package com.github.dynodao.processor.schema.attribute;

import com.github.dynodao.processor.schema.serialize.MappingMethod;
import lombok.Builder;
import lombok.Value;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

/**
 * A boolean attribute.
 */
@Value
@Builder
public class BooleanDynamoAttribute implements DynamoAttribute {

    private final String path;
    private final Element element;
    private final TypeMirror typeMirror;
    private final MappingMethod serializationMethod;
    private final MappingMethod deserializationMethod;

    @Override
    public DynamoAttributeType getAttributeType() {
        return DynamoAttributeType.BOOLEAN;
    }

    @Override
    public <R, P> R accept(DynamoAttributeVisitor<R, P> visitor, P arg) {
        return visitor.visitBoolean(this, arg);
    }

}
