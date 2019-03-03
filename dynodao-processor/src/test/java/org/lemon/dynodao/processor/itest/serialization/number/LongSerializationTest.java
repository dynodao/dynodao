package org.lemon.dynodao.processor.itest.serialization.number;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.junit.Test;
import org.lemon.dynodao.processor.itest.AbstractSourceCompilingTest;

import static org.assertj.core.api.Assertions.assertThat;

public class LongSerializationTest extends AbstractSourceCompilingTest {

    @Test
    public void serializeLong_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeLong(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    public void serializeLong_long_returnsAttributeValueWithNumber() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeLong(new Long("1"));
        assertThat(value).isEqualTo(new AttributeValue().withN("1"));
    }

    @Test
    public void deserializeLong_null_returnsNull() {
        Long value = SchemaAttributeValueSerializer.deserializeLong(null);
        assertThat(value).isNull();
    }

    @Test
    public void deserializeLong_nullAttributeValue_returnsNull() {
        Long value = SchemaAttributeValueSerializer.deserializeLong(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    public void deserializeLong_numberValueNull_returnsNull() {
        Long value = SchemaAttributeValueSerializer.deserializeLong(new AttributeValue().withS("not number"));
        assertThat(value).isNull();
    }

    @Test
    public void deserializeLong_numberValue_returnsLongValue() {
        Long value = SchemaAttributeValueSerializer.deserializeLong(new AttributeValue().withN("1"));
        assertThat(value).isEqualTo(new Long("1"));
    }

}
