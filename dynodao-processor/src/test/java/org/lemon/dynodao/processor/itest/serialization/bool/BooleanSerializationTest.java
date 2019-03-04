package org.lemon.dynodao.processor.itest.serialization.bool;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.junit.jupiter.api.Test;
import org.lemon.dynodao.processor.itest.AbstractSourceCompilingTest;

import static org.assertj.core.api.Assertions.assertThat;

class BooleanSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializeBoolean_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeBoolean(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    void serializeBoolean_boolean_returnsAttributeValueWithBoolean() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeBoolean(true);
        assertThat(value).isEqualTo(new AttributeValue().withBOOL(true));
    }

    @Test
    void deserializeBoolean_null_returnsNull() {
        Boolean value = SchemaAttributeValueSerializer.deserializeBoolean(null);
        assertThat(value).isNull();
    }

    @Test
    void deserializeBoolean_nullAttributeValue_returnsNull() {
        Boolean value = SchemaAttributeValueSerializer.deserializeBoolean(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    void deserializeBoolean_booleanValueNull_returnsNull() {
        Boolean value = SchemaAttributeValueSerializer.deserializeBoolean(new AttributeValue().withS("not bool"));
        assertThat(value).isNull();
    }

    @Test
    void deserializeBoolean_booleanValue_returnsBooleanValue() {
        Boolean value = SchemaAttributeValueSerializer.deserializeBoolean(new AttributeValue().withBOOL(true));
        assertThat(value).isTrue();
    }

}
