package org.lemon.dynodao.processor.itest.serialization.bool;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.junit.jupiter.api.Test;
import org.lemon.dynodao.processor.itest.AbstractSourceCompilingTest;

import static org.assertj.core.api.Assertions.assertThat;

class PrimitiveBooleanSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializePrimitiveBoolean_onlyUseCase_returnsAttributeValueWithBoolean() {
        AttributeValue value = SchemaAttributeValueSerializer.serializePrimitiveBoolean(true);
        assertThat(value).isEqualTo(new AttributeValue().withBOOL(true));
    }

    @Test
    void deserializePrimitiveBoolean_null_returnsFalse() {
        boolean value = SchemaAttributeValueSerializer.deserializePrimitiveBoolean(null);
        assertThat(value).isFalse();
    }

    @Test
    void deserializePrimitiveBoolean_nullAttributeValue_returnsFalse() {
        boolean value = SchemaAttributeValueSerializer.deserializePrimitiveBoolean(new AttributeValue().withNULL(true));
        assertThat(value).isFalse();
    }

    @Test
    void deserializePrimitiveBoolean_booleanValueNull_returnsFalse() {
        boolean value = SchemaAttributeValueSerializer.deserializePrimitiveBoolean(new AttributeValue().withS("not bool"));
        assertThat(value).isFalse();
    }

    @Test
    void deserializePrimitiveBoolean_booleanValue_returnsBooleanValue() {
        boolean value = SchemaAttributeValueSerializer.deserializePrimitiveBoolean(new AttributeValue().withBOOL(true));
        assertThat(value).isTrue();
    }

}
