package org.lemon.dynodao.processor.itest.serialization.number;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.junit.Test;
import org.lemon.dynodao.processor.itest.AbstractSourceCompilingTest;

import static org.assertj.core.api.Assertions.assertThat;

public class DoubleSerializationTest extends AbstractSourceCompilingTest {

    @Test
    public void serializeDouble_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeDouble(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    public void serializeDouble_double_returnsAttributeValueWithNumber() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeDouble(new Double("0.125"));
        assertThat(value).isEqualTo(new AttributeValue().withN("0.125"));
    }

    @Test
    public void deserializeDouble_null_returnsNull() {
        Double value = SchemaAttributeValueSerializer.deserializeDouble(null);
        assertThat(value).isNull();
    }

    @Test
    public void deserializeDouble_nullAttributeValue_returnsNull() {
        Double value = SchemaAttributeValueSerializer.deserializeDouble(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    public void deserializeDouble_numberValueNull_returnsNull() {
        Double value = SchemaAttributeValueSerializer.deserializeDouble(new AttributeValue().withS("not number"));
        assertThat(value).isNull();
    }

    @Test
    public void deserializeDouble_numberValue_returnsDoubleValue() {
        Double value = SchemaAttributeValueSerializer.deserializeDouble(new AttributeValue().withN("0.125"));
        assertThat(value).isEqualTo(new Double("0.125"));
    }

}
