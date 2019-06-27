package com.github.dynodao.processor.schema.attribute;

import com.github.dynodao.processor.schema.serialize.MappingMethod;
import lombok.Builder;
import lombok.Value;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

/**
 * A map element. A map consists of String keys and a known value type, where the keys are not known.
 */
@Value
@Builder
public class MapDynamoAttribute implements DynamoAttribute {

    private final String path;
    private final Element element;
    private final TypeMirror typeMirror;
    private final MappingMethod serializationMethod;
    private final MappingMethod deserializationMethod;
    private final DynamoAttribute mapElement;

    @Override
    public DynamoAttributeType getAttributeType() {
        return DynamoAttributeType.MAP;
    }

    @Override
    public <R, P> R accept(DynamoAttributeVisitor<R, P> visitor, P arg) {
        return visitor.visitMap(this, arg);
    }

}
