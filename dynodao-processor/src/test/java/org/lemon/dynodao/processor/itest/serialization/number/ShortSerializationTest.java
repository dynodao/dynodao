package org.lemon.dynodao.processor.itest.serialization.number;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.junit.Test;
import org.lemon.dynodao.processor.itest.AbstractSourceCompilingTest;

import static org.assertj.core.api.Assertions.assertThat;

public class ShortSerializationTest extends AbstractSourceCompilingTest {

    @Test
    public void serializeShort_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeShort(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    public void serializeShort_onlyUseCase_returnsAttributeValueWithNumber() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeShort(new Short("1"));
        assertThat(value).isEqualTo(new AttributeValue().withN("1"));
    }

    @Test
    public void deserializeShort_null_returnsNull() {
        Short value = SchemaAttributeValueSerializer.deserializeShort(null);
        assertThat(value).isNull();
    }

    @Test
    public void deserializeShort_nullAttributeValue_returnsNull() {
        Short value = SchemaAttributeValueSerializer.deserializeShort(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    public void deserializeShort_numberValueNull_returnsNull() {
        Short value = SchemaAttributeValueSerializer.deserializeShort(new AttributeValue().withS("not number"));
        assertThat(value).isNull();
    }

    @Test
    public void deserializeShort_numberValue_returnsShortValue() {
        Short value = SchemaAttributeValueSerializer.deserializeShort(new AttributeValue().withN("1"));
        assertThat(value).isEqualTo(new Short("1"));
    }

}
