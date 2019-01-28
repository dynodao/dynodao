package org.lemon.dynodao.processor.serialize.unmarshall;

import org.lemon.dynodao.processor.serialize.marshall.AttributeValueMarshaller;
import org.lemon.dynodao.processor.util.StreamUtil;

import javax.inject.Inject;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Stores all of the implementations of {@link AttributeValueUnmarshaller} in the appropriate order.
 */
public class AttributeValueUnmarshallers implements StreamUtil.Streamable<AttributeValueUnmarshaller> {

    private final List<AttributeValueUnmarshaller> attributeValueUnmarshallers = new ArrayList<>();

    @Inject AttributeValueUnmarshallers() { }

    /**
     * Populates the attributeValueUnmarshallers field. The first deserializer that matches {@link AttributeValueMarshaller#isApplicableTo(TypeMirror)}
     * will be the only deserializer to be invoked for a single type, so place the items in precedence order.
     * For example, any user-defined serialization should occur before any built-in ones.
     */
    @Inject void initAttributeValueUnmarshallers() {
    }

    @Override
    public Iterator<AttributeValueUnmarshaller> iterator() {
        return attributeValueUnmarshallers.iterator();
    }
}
