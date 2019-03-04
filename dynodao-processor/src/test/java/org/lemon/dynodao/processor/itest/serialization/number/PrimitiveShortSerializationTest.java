package org.lemon.dynodao.processor.itest.serialization.number;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.junit.jupiter.api.Test;
import org.lemon.dynodao.processor.itest.AbstractSourceCompilingTest;

import static org.assertj.core.api.Assertions.assertThat;

class PrimitiveShortSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializePrimitiveShort_onlyUseCase_returnsAttributeValueWithNumber() {
        AttributeValue value = SchemaAttributeValueSerializer.serializePrimitiveShort((short) 1);
        assertThat(value).isEqualTo(new AttributeValue().withN("1"));
    }

    @Test
    void deserializePrimitiveShort_null_returnsZero() {
        short value = SchemaAttributeValueSerializer.deserializePrimitiveShort(null);
        assertThat(value).isZero();
    }

    @Test
    void deserializePrimitiveShort_nullAttributeValue_returnsZero() {
        short value = SchemaAttributeValueSerializer.deserializePrimitiveShort(new AttributeValue().withNULL(true));
        assertThat(value).isZero();
    }

    @Test
    void deserializePrimitiveShort_numberValueNull_returnsZero() {
        short value = SchemaAttributeValueSerializer.deserializePrimitiveShort(new AttributeValue().withS("not number"));
        assertThat(value).isZero();
    }

    @Test
    void deserializePrimitiveShort_numberValue_returnsShortValue() {
        short value = SchemaAttributeValueSerializer.deserializePrimitiveShort(new AttributeValue().withN("1"));
        assertThat(value).isEqualTo((short) 1);
    }

}
