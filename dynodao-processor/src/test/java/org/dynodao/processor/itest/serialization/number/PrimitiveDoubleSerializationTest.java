package org.dynodao.processor.itest.serialization.number;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractSourceCompilingTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PrimitiveDoubleSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializePrimitiveDouble_onlyUseCase_returnsAttributeValueWithNumber() {
        AttributeValue value = SchemaAttributeValueSerializer.serializePrimitiveDouble(0.125);
        assertThat(value).isEqualTo(new AttributeValue().withN("0.125"));
    }

    @Test
    void deserializePrimitiveDouble_null_returnsZero() {
        double value = SchemaAttributeValueSerializer.deserializePrimitiveDouble(null);
        assertThat(value).isZero();
    }

    @Test
    void deserializePrimitiveDouble_nullAttributeValue_returnsZero() {
        double value = SchemaAttributeValueSerializer.deserializePrimitiveDouble(new AttributeValue().withNULL(true));
        assertThat(value).isZero();
    }

    @Test
    void deserializePrimitiveDouble_numberValueNull_returnsZero() {
        double value = SchemaAttributeValueSerializer.deserializePrimitiveDouble(new AttributeValue().withS("not number"));
        assertThat(value).isZero();
    }

    @Test
    void deserializePrimitiveDouble_numberValue_returnsDoubleValue() {
        double value = SchemaAttributeValueSerializer.deserializePrimitiveDouble(new AttributeValue().withN("0.125"));
        assertThat(value).isEqualTo(0.125);
    }

}
