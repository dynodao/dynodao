package org.lemon.dynodao.processor.schema.attribute;

import lombok.Builder;
import lombok.Value;
import org.lemon.dynodao.processor.schema.serialize.MappingMethod;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

/**
 * A binary attribute.
 */
@Value
@Builder
public class BinaryDynamoAttribute implements DynamoAttribute {

    private final String path;
    private final Element element;
    private final TypeMirror typeMirror;
    private final MappingMethod serializationMethod;
    private final MappingMethod deserializationMethod;

    @Override
    public DynamoAttributeType getAttributeType() {
        return DynamoAttributeType.BINARY;
    }

    @Override
    public <R, P> R accept(DynamoAttributeVisitor<R, P> visitor, P arg) {
        return visitor.visitBinary(this, arg);
    }

}

