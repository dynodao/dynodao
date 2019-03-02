package org.lemon.dynodao.processor.itest.serialization.number;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.junit.Test;
import org.lemon.dynodao.processor.itest.AbstractSourceCompilingTest;

import static org.assertj.core.api.Assertions.assertThat;

public class PrimitiveDoubleSerializationTest extends AbstractSourceCompilingTest {

    @Test
    public void serializePrimitiveDouble_onlyUseCase_returnsAttributeValueWithNumber() {
        AttributeValue value = SchemaAttributeValueSerializer.serializePrimitiveDouble(0.125);
        assertThat(value).isEqualTo(new AttributeValue().withN("0.125"));
    }

    @Test
    public void deserializePrimitiveDouble_null_returnsZero() {
        double value = SchemaAttributeValueSerializer.deserializePrimitiveDouble(null);
        assertThat(value).isZero();
    }

    @Test
    public void deserializePrimitiveDouble_nullAttributeValue_returnsZero() {
        double value = SchemaAttributeValueSerializer.deserializePrimitiveDouble(new AttributeValue().withNULL(true));
        assertThat(value).isZero();
    }

    @Test
    public void deserializePrimitiveDouble_numberValueNull_returnsZero() {
        double value = SchemaAttributeValueSerializer.deserializePrimitiveDouble(new AttributeValue().withS("not number"));
        assertThat(value).isZero();
    }

    @Test
    public void deserializePrimitiveDouble_numberValue_returnsDoubleValue() {
        double value = SchemaAttributeValueSerializer.deserializePrimitiveDouble(new AttributeValue().withN("0.125"));
        assertThat(value).isEqualTo(0.125);
    }
    
}
