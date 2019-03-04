package org.lemon.dynodao.processor.itest.serialization.number;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.junit.jupiter.api.Test;
import org.lemon.dynodao.processor.itest.AbstractSourceCompilingTest;

import static org.assertj.core.api.Assertions.assertThat;

class PrimitiveByteSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializePrimitiveByte_onlyUseCase_returnsAttributeValueWithNumber() {
        AttributeValue value = SchemaAttributeValueSerializer.serializePrimitiveByte((byte) 1);
        assertThat(value).isEqualTo(new AttributeValue().withN("1"));
    }

    @Test
    void deserializePrimitiveByte_null_returnsZero() {
        byte value = SchemaAttributeValueSerializer.deserializePrimitiveByte(null);
        assertThat(value).isZero();
    }

    @Test
    void deserializePrimitiveByte_nullAttributeValue_returnsZero() {
        byte value = SchemaAttributeValueSerializer.deserializePrimitiveByte(new AttributeValue().withNULL(true));
        assertThat(value).isZero();
    }

    @Test
    void deserializePrimitiveByte_numberValueNull_returnsZero() {
        byte value = SchemaAttributeValueSerializer.deserializePrimitiveByte(new AttributeValue().withS("not number"));
        assertThat(value).isZero();
    }

    @Test
    void deserializePrimitiveByte_numberValue_returnsByteValue() {
        byte value = SchemaAttributeValueSerializer.deserializePrimitiveByte(new AttributeValue().withN("1"));
        assertThat(value).isEqualTo((byte) 1);
    }

}
