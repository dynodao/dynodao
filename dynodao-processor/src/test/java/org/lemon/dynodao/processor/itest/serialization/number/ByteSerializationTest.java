package org.lemon.dynodao.processor.itest.serialization.number;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.junit.Test;
import org.lemon.dynodao.processor.itest.AbstractSourceCompilingTest;

import static org.assertj.core.api.Assertions.assertThat;

public class ByteSerializationTest extends AbstractSourceCompilingTest {

    @Test
    public void serializeByte_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeByte(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    public void serializeByte_byte_returnsAttributeValueWithNumber() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeByte(new Byte("1"));
        assertThat(value).isEqualTo(new AttributeValue().withN("1"));
    }

    @Test
    public void deserializeByte_null_returnsNull() {
        Byte value = SchemaAttributeValueSerializer.deserializeByte(null);
        assertThat(value).isNull();
    }

    @Test
    public void deserializeByte_nullAttributeValue_returnsNull() {
        Byte value = SchemaAttributeValueSerializer.deserializeByte(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    public void deserializeByte_numberValueNull_returnsNull() {
        Byte value = SchemaAttributeValueSerializer.deserializeByte(new AttributeValue().withS("not number"));
        assertThat(value).isNull();
    }

    @Test
    public void deserializeByte_numberValue_returnsByteValue() {
        Byte value = SchemaAttributeValueSerializer.deserializeByte(new AttributeValue().withN("1"));
        assertThat(value).isEqualTo(new Byte("1"));
    }

}
