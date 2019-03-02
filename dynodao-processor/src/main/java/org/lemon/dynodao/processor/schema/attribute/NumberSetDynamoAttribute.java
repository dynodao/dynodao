package org.lemon.dynodao.processor.schema.attribute;

import lombok.Builder;
import lombok.Value;
import org.lemon.dynodao.processor.schema.serialize.MappingMethod;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

/**
 * A set of numbers.
 */
@Value
@Builder
public class NumberSetDynamoAttribute implements DynamoAttribute {

    private final String path;
    private final Element element;
    private final TypeMirror typeMirror;
    private final MappingMethod serializationMethod;
    private final MappingMethod deserializationMethod;
    private final NumberDynamoAttribute setElement;

    @Override
    public DynamoAttributeType getAttributeType() {
        return DynamoAttributeType.NUMBER_SET;
    }

    @Override
    public <R, P> R accept(DynamoAttributeVisitor<R, P> visitor, P arg) {
        return visitor.visitNumberSet(this, arg);
    }

}
