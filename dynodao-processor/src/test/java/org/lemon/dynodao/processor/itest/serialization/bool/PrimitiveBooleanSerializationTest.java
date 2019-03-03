package org.lemon.dynodao.processor.itest.serialization.bool;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.junit.Test;
import org.lemon.dynodao.processor.itest.AbstractSourceCompilingTest;

import static org.assertj.core.api.Assertions.assertThat;

public class PrimitiveBooleanSerializationTest extends AbstractSourceCompilingTest {

    @Test
    public void serializePrimitiveBoolean_onlyUseCase_returnsAttributeValueWithBoolean() {
        AttributeValue value = SchemaAttributeValueSerializer.serializePrimitiveBoolean(true);
        assertThat(value).isEqualTo(new AttributeValue().withBOOL(true));
    }

    @Test
    public void deserializePrimitiveBoolean_null_returnsFalse() {
        boolean value = SchemaAttributeValueSerializer.deserializePrimitiveBoolean(null);
        assertThat(value).isFalse();
    }

    @Test
    public void deserializePrimitiveBoolean_nullAttributeValue_returnsFalse() {
        boolean value = SchemaAttributeValueSerializer.deserializePrimitiveBoolean(new AttributeValue().withNULL(true));
        assertThat(value).isFalse();
    }

    @Test
    public void deserializePrimitiveBoolean_booleanValueNull_returnsFalse() {
        boolean value = SchemaAttributeValueSerializer.deserializePrimitiveBoolean(new AttributeValue().withS("not bool"));
        assertThat(value).isFalse();
    }

    @Test
    public void deserializePrimitiveBoolean_booleanValue_returnsBooleanValue() {
        boolean value = SchemaAttributeValueSerializer.deserializePrimitiveBoolean(new AttributeValue().withBOOL(true));
        assertThat(value).isTrue();
    }

}
