package org.lemon.dynodao.processor.schema.attribute;

import lombok.Builder;
import lombok.Value;
import org.lemon.dynodao.processor.schema.serialize.MappingMethod;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

/**
 * A list attribute containing a single known attribute type within, of an unknown quantity.
 */
@Value
@Builder
public class ListDynamoAttribute implements DynamoAttribute {

    private final String path;
    private final Element element;
    private final TypeMirror typeMirror;
    private final MappingMethod serializationMethod;
    private final MappingMethod deserializationMethod;
    private final DynamoAttribute listElement;

    @Override
    public DynamoAttributeType getAttributeType() {
        return DynamoAttributeType.LIST;
    }

    @Override
    public <R, P> R accept(DynamoAttributeVisitor<R, P> visitor, P arg) {
        return visitor.visitList(this, arg);
    }

}
