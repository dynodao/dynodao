package org.lemon.dynodao.processor.schema.attribute;

import lombok.Builder;
import lombok.Value;
import org.lemon.dynodao.annotation.DynoDaoDocument;
import org.lemon.dynodao.annotation.DynoDaoSchema;
import org.lemon.dynodao.processor.schema.serialize.MappingMethod;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import java.util.List;

/**
 * A document attribute, stored as a map in dynamo. A document is a map with a known set of keys, where each key
 * can be a different attribute type. Models any class annotated with {@link DynoDaoSchema} or {@link DynoDaoDocument}.
 */
@Value
@Builder
public class DocumentDynamoAttribute implements DynamoAttribute {

    private final String path;
    private final Element element;
    private final TypeMirror typeMirror;
    private final MappingMethod serializationMethod;
    private final MappingMethod deserializationMethod;
    private final List<DynamoAttribute> attributes;

    @Override
    public DynamoAttributeType getAttributeType() {
        return DynamoAttributeType.MAP;
    }

    @Override
    public <R, P> R accept(DynamoAttributeVisitor<R, P> visitor, P arg) {
        return visitor.visitDocument(this, arg);
    }

}
