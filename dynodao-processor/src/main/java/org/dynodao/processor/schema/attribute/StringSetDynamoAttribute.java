package org.dynodao.processor.schema.attribute;

import lombok.Builder;
import lombok.Value;
import org.dynodao.processor.schema.serialize.MappingMethod;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

/**
 * A set of strings.
 */
@Value
@Builder
public class StringSetDynamoAttribute implements DynamoAttribute {

    private final String path;
    private final Element element;
    private final TypeMirror typeMirror;
    private final MappingMethod serializationMethod;
    private final MappingMethod deserializationMethod;
    private final StringDynamoAttribute setElement;

    @Override
    public DynamoAttributeType getAttributeType() {
        return DynamoAttributeType.STRING_SET;
    }

    @Override
    public <R, P> R accept(DynamoAttributeVisitor<R, P> visitor, P arg) {
        return visitor.visitStringSet(this, arg);
    }

}
