package org.dynodao.processor.itest.serialization.number;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractSourceCompilingTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IntegerSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializeInteger_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeInteger(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    void serializeInteger_integer_returnsAttributeValueWithNumber() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeInteger(new Integer("1"));
        assertThat(value).isEqualTo(new AttributeValue().withN("1"));
    }

    @Test
    void deserializeInteger_null_returnsNull() {
        Integer value = SchemaAttributeValueSerializer.deserializeInteger(null);
        assertThat(value).isNull();
    }

    @Test
    void deserializeInteger_nullAttributeValue_returnsNull() {
        Integer value = SchemaAttributeValueSerializer.deserializeInteger(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    void deserializeInteger_numberValueNull_returnsNull() {
        Integer value = SchemaAttributeValueSerializer.deserializeInteger(new AttributeValue().withS("not number"));
        assertThat(value).isNull();
    }

    @Test
    void deserializeInteger_numberValue_returnsIntegerValue() {
        Integer value = SchemaAttributeValueSerializer.deserializeInteger(new AttributeValue().withN("1"));
        assertThat(value).isEqualTo(new Integer("1"));
    }

}
