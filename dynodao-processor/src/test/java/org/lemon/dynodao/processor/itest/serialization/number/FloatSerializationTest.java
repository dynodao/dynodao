package org.lemon.dynodao.processor.itest.serialization.number;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.junit.Test;
import org.lemon.dynodao.processor.itest.AbstractSourceCompilingTest;

import static org.assertj.core.api.Assertions.assertThat;

public class FloatSerializationTest extends AbstractSourceCompilingTest {

    @Test
    public void serializeFloat_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeFloat(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    public void serializeFloat_onlyUseCase_returnsAttributeValueWithNumber() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeFloat(new Float("0.125"));
        assertThat(value).isEqualTo(new AttributeValue().withN("0.125"));
    }

    @Test
    public void deserializeFloat_null_returnsNull() {
        Float value = SchemaAttributeValueSerializer.deserializeFloat(null);
        assertThat(value).isNull();
    }

    @Test
    public void deserializeFloat_nullAttributeValue_returnsNull() {
        Float value = SchemaAttributeValueSerializer.deserializeFloat(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    public void deserializeFloat_numberValueNull_returnsNull() {
        Float value = SchemaAttributeValueSerializer.deserializeFloat(new AttributeValue().withS("not number"));
        assertThat(value).isNull();
    }

    @Test
    public void deserializeFloat_numberValue_returnsFloatValue() {
        Float value = SchemaAttributeValueSerializer.deserializeFloat(new AttributeValue().withN("0.125"));
        assertThat(value).isEqualTo(new Float("0.125"));
    }

}
