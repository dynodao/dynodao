package org.dynodao.processor.itest.serialization.number;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractSourceCompilingTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PrimitiveFloatSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializePrimitiveFloat_onlyUseCase_returnsAttributeValueWithNumber() {
        AttributeValue value = SchemaAttributeValueSerializer.serializePrimitiveFloat(0.125f);
        assertThat(value).isEqualTo(new AttributeValue().withN("0.125"));
    }

    @Test
    void deserializePrimitiveFloat_null_returnsZero() {
        float value = SchemaAttributeValueSerializer.deserializePrimitiveFloat(null);
        assertThat(value).isZero();
    }

    @Test
    void deserializePrimitiveFloat_nullAttributeValue_returnsZero() {
        float value = SchemaAttributeValueSerializer.deserializePrimitiveFloat(new AttributeValue().withNULL(true));
        assertThat(value).isZero();
    }

    @Test
    void deserializePrimitiveFloat_numberValueNull_returnsZero() {
        float value = SchemaAttributeValueSerializer.deserializePrimitiveFloat(new AttributeValue().withS("not number"));
        assertThat(value).isZero();
    }

    @Test
    void deserializePrimitiveFloat_numberValue_returnsFloatValue() {
        float value = SchemaAttributeValueSerializer.deserializePrimitiveFloat(new AttributeValue().withN("0.125"));
        assertThat(value).isEqualTo(0.125f);
    }

}
