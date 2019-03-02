package org.lemon.dynodao.processor.itest.serialization.string;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.junit.Test;
import org.lemon.dynodao.processor.itest.AbstractSourceCompilingTest;

import static org.assertj.core.api.Assertions.assertThat;

public class StringSerializationTest extends AbstractSourceCompilingTest {

    @Test
    public void serializeString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    public void serializeString_string_returnsAttributeValueWithString() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeString("string");
        assertThat(value).isEqualTo(new AttributeValue("string"));
    }

    @Test
    public void deserializeString_null_returnsNull() {
        String value = SchemaAttributeValueSerializer.deserializeString(null);
        assertThat(value).isNull();
    }

    @Test
    public void deserializeString_nullAttributeValue_returnsNull() {
        String value = SchemaAttributeValueSerializer.deserializeString(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    public void deserializeString_stringValueNull_returnsNull() {
        String value = SchemaAttributeValueSerializer.deserializeString(new AttributeValue().withN("not string"));
        assertThat(value).isNull();
    }

    @Test
    public void deserializeString_stringValue_returnsString() {
        String value = SchemaAttributeValueSerializer.deserializeString(new AttributeValue().withS("string"));
        assertThat(value).isEqualTo("string");
    }

}
