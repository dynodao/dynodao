package org.lemon.dynodao.processor.itest.serialization.number;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.junit.Test;
import org.lemon.dynodao.processor.itest.AbstractSourceCompilingTest;

import static org.assertj.core.api.Assertions.assertThat;

public class PrimitiveFloatSerializationTest extends AbstractSourceCompilingTest {

    @Test
    public void serializePrimitiveFloat_onlyUseCase_returnsAttributeValueWithNumber() {
        AttributeValue value = SchemaAttributeValueSerializer.serializePrimitiveFloat(0.125f);
        assertThat(value).isEqualTo(new AttributeValue().withN("0.125"));
    }

    @Test
    public void deserializePrimitiveFloat_null_returnsZero() {
        float value = SchemaAttributeValueSerializer.deserializePrimitiveFloat(null);
        assertThat(value).isZero();
    }

    @Test
    public void deserializePrimitiveFloat_nullAttributeValue_returnsZero() {
        float value = SchemaAttributeValueSerializer.deserializePrimitiveFloat(new AttributeValue().withNULL(true));
        assertThat(value).isZero();
    }

    @Test
    public void deserializePrimitiveFloat_numberValueNull_returnsZero() {
        float value = SchemaAttributeValueSerializer.deserializePrimitiveFloat(new AttributeValue().withS("not number"));
        assertThat(value).isZero();
    }

    @Test
    public void deserializePrimitiveFloat_numberValue_returnsFloatValue() {
        float value = SchemaAttributeValueSerializer.deserializePrimitiveFloat(new AttributeValue().withN("0.125"));
        assertThat(value).isEqualTo(0.125f);
    }
    
}
