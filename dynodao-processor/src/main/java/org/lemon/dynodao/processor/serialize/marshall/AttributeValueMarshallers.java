package org.lemon.dynodao.processor.serialize.marshall;

import org.lemon.dynodao.processor.util.StreamUtil.Streamable;

import javax.inject.Inject;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Stores all of the implementations of {@link AttributeValueMarshaller} in the appropriate order.
 * TODO BooleanSerializer for boolean, Boolean
 * TODO BinarySerializer for ByteBuffer, byte[]
 * TODO LegacyDateSerializer for Date, Calendar
 * TODO JavaTimeSerializer(s) for LocalDateTime, ZonedDateTime, Duration, Instant, ZoneId, etc
 * TODO CurrencySerializer for Currency
 * TODO BinarySetSerializer for BS
 * TODO NumericSetSerializer for NS
 * TODO StringSetSerializer for SS
 */
public class AttributeValueMarshallers implements Streamable<AttributeValueMarshaller> {

    @Inject StringMarshaller stringMarshaller;
    @Inject NumericMarshaller numericMarshaller;
    @Inject MapMarshaller mapMarshaller;
    @Inject DocumentMarshaller documentMarshaller;

    private final List<AttributeValueMarshaller> attributeValueMarshallers = new ArrayList<>();

    @Inject AttributeValueMarshallers() { }

    /**
     * Populates the attributeValueMarshallers field. The first serializer that matches {@link AttributeValueMarshaller#isApplicableTo(TypeMirror)}
     * will be the only serializer to be invoked for a single type, so place the items in precedence order.
     * For example, any user-defined serialization should occur before any built-in ones.
     */
    @Inject void initAttributeValueMarshallers() {
        attributeValueMarshallers.add(stringMarshaller);
        attributeValueMarshallers.add(numericMarshaller);
        attributeValueMarshallers.add(mapMarshaller);
        attributeValueMarshallers.add(documentMarshaller);
    }

    @Override
    public Iterator<AttributeValueMarshaller> iterator() {
        return attributeValueMarshallers.iterator();
    }

}
