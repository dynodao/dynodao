package org.dynodao.processor.itest.serialization.number;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractSourceCompilingTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PrimitiveIntSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializePrimitiveInt_onlyUseCase_returnsAttributeValueWithNumber() {
        AttributeValue value = SchemaAttributeValueSerializer.serializePrimitiveInt(1);
        assertThat(value).isEqualTo(new AttributeValue().withN("1"));
    }

    @Test
    void deserializePrimitiveInt_null_returnsZero() {
        int value = SchemaAttributeValueSerializer.deserializePrimitiveInt(null);
        assertThat(value).isZero();
    }

    @Test
    void deserializePrimitiveInt_nullAttributeValue_returnsZero() {
        int value = SchemaAttributeValueSerializer.deserializePrimitiveInt(new AttributeValue().withNULL(true));
        assertThat(value).isZero();
    }

    @Test
    void deserializePrimitiveInt_numberValueNull_returnsZero() {
        int value = SchemaAttributeValueSerializer.deserializePrimitiveInt(new AttributeValue().withS("not number"));
        assertThat(value).isZero();
    }

    @Test
    void deserializePrimitiveInt_numberValue_returnsIntValue() {
        int value = SchemaAttributeValueSerializer.deserializePrimitiveInt(new AttributeValue().withN("1"));
        assertThat(value).isEqualTo(1);
    }

}
