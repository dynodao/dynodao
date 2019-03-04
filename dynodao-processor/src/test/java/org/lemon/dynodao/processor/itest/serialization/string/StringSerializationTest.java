package org.lemon.dynodao.processor.itest.serialization.string;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.junit.jupiter.api.Test;
import org.lemon.dynodao.processor.itest.AbstractSourceCompilingTest;

import static org.assertj.core.api.Assertions.assertThat;

class StringSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializeString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    void serializeString_string_returnsAttributeValueWithString() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeString("string");
        assertThat(value).isEqualTo(new AttributeValue("string"));
    }

    @Test
    void deserializeString_null_returnsNull() {
        String value = SchemaAttributeValueSerializer.deserializeString(null);
        assertThat(value).isNull();
    }

    @Test
    void deserializeString_nullAttributeValue_returnsNull() {
        String value = SchemaAttributeValueSerializer.deserializeString(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    void deserializeString_stringValueNull_returnsNull() {
        String value = SchemaAttributeValueSerializer.deserializeString(new AttributeValue().withN("not string"));
        assertThat(value).isNull();
    }

    @Test
    void deserializeString_stringValue_returnsString() {
        String value = SchemaAttributeValueSerializer.deserializeString(new AttributeValue().withS("string"));
        assertThat(value).isEqualTo("string");
    }

}
