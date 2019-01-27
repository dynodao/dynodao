package org.lemon.dynodao.processor.serialize.value;

import org.lemon.dynodao.processor.util.StreamUtil.Streamable;

import javax.inject.Inject;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Stores all of the implementations of {@link AttributeValueSerializer} in the appropriate order.
 * TODO BooleanSerializer for boolean, Boolean
 * TODO BinarySerializer for ByteBuffer, byte[]
 * TODO LegacyDateSerializer for Date, Calendar
 * TODO JavaTimeSerializer(s) for LocalDateTime, ZonedDateTime, Duration, Instant, ZoneId, etc
 * TODO CurrencySerializer for Currency
 * TODO BinarySetSerializer for BS
 * TODO NumericSetSerializer for NS
 * TODO StringSetSerializer for SS
 */
public class AttributeValueSerializers implements Streamable<AttributeValueSerializer> {

    @Inject StringSerializer stringSerializer;
    @Inject NumericSerializer numericSerializer;
    @Inject MapSerializer mapSerializer;
    @Inject DocumentSerializer documentSerializer;

    private final List<AttributeValueSerializer> attributeValueSerializers = new ArrayList<>();

    @Inject AttributeValueSerializers() { }

    /**
     * Populates the attributeValueSerializers field. The first serializer that matches {@link AttributeValueSerializer#isApplicableTo(TypeMirror)}
     * will be the only serializer to be invoked for a single type, so place the items in precedence order.
     * For example, any user-defined serialization should occur before any built-in ones.
     */
    @Inject void initAttributeValueSerializers() {
        attributeValueSerializers.add(stringSerializer);
        attributeValueSerializers.add(numericSerializer);
        attributeValueSerializers.add(mapSerializer);
        attributeValueSerializers.add(documentSerializer);
    }

    @Override
    public Iterator<AttributeValueSerializer> iterator() {
        return attributeValueSerializers.iterator();
    }

}
