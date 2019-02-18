package org.lemon.dynodao.processor.schema.attribute;

import lombok.Builder;
import lombok.Value;
import org.lemon.dynodao.processor.schema.serialize.MappingMethod;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

/**
 * A binary set attribute.
 */
@Value
@Builder
public class BinarySetDynamoAttribute implements DynamoAttribute {

    private final String path;
    private final Element element;
    private final TypeMirror typeMirror;
    private final MappingMethod serializationMethod;
    private final MappingMethod deserializationMethod;
    private final BinaryDynamoAttribute setElement;

    @Override
    public DynamoAttributeType getAttributeType() {
        return DynamoAttributeType.BINARY_SET;
    }

    @Override
    public <R, P> R accept(DynamoAttributeVisitor<R, P> visitor, P arg) {
        return visitor.visitBinarySet(this, arg);
    }

}

