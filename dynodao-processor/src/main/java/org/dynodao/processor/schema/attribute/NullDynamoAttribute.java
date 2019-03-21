package org.dynodao.processor.schema.attribute;

import lombok.Builder;
import lombok.Value;
import org.dynodao.processor.schema.serialize.MappingMethod;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

/**
 * The null attribute type. Exists as a placeholder, as nothing should model this type.
 */
@Value
@Builder
public class NullDynamoAttribute implements DynamoAttribute {

    private final String path;
    private final Element element;
    private final TypeMirror typeMirror;
    private final MappingMethod serializationMethod;
    private final MappingMethod deserializationMethod;

    @Override
    public DynamoAttributeType getAttributeType() {
        return DynamoAttributeType.NULL;
    }

    @Override
    public <R, P> R accept(DynamoAttributeVisitor<R, P> visitor, P arg) {
        return visitor.visitNull(this, arg);
    }

}
