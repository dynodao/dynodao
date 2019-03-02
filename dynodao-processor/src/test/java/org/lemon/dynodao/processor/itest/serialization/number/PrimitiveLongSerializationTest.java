package org.lemon.dynodao.processor.itest.serialization.number;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.junit.Test;
import org.lemon.dynodao.processor.itest.AbstractSourceCompilingTest;

import static org.assertj.core.api.Assertions.assertThat;

public class PrimitiveLongSerializationTest extends AbstractSourceCompilingTest {

    @Test
    public void serializePrimitiveLong_onlyUseCase_returnsAttributeValueWithNumber() {
        AttributeValue value = SchemaAttributeValueSerializer.serializePrimitiveLong(1L);
        assertThat(value).isEqualTo(new AttributeValue().withN("1"));
    }

    @Test
    public void deserializePrimitiveLong_null_returnsZero() {
        long value = SchemaAttributeValueSerializer.deserializePrimitiveLong(null);
        assertThat(value).isZero();
    }

    @Test
    public void deserializePrimitiveLong_nullAttributeValue_returnsZero() {
        long value = SchemaAttributeValueSerializer.deserializePrimitiveLong(new AttributeValue().withNULL(true));
        assertThat(value).isZero();
    }

    @Test
    public void deserializePrimitiveLong_numberValueNull_returnsZero() {
        long value = SchemaAttributeValueSerializer.deserializePrimitiveLong(new AttributeValue().withS("not number"));
        assertThat(value).isZero();
    }

    @Test
    public void deserializePrimitiveLong_numberValue_returnsLongValue() {
        long value = SchemaAttributeValueSerializer.deserializePrimitiveLong(new AttributeValue().withN("1"));
        assertThat(value).isEqualTo(1L);
    }

}
