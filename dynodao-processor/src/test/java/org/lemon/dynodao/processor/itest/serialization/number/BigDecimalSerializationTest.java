package org.lemon.dynodao.processor.itest.serialization.number;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.junit.jupiter.api.Test;
import org.lemon.dynodao.processor.itest.AbstractSourceCompilingTest;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;

class BigDecimalSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializeBigDecimal_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeBigDecimal(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    void serializeBigDecimal_smallValue_returnsAttributeValueWithNumber() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeBigDecimal(new BigDecimal("1"));
        assertThat(value).isEqualTo(new AttributeValue().withN("1"));
    }

    @Test
    void serializeBigDecimal_largeValueWithExponent_returnsAttributeValueWithNumber() {
        BigDecimal largeDecimal = new BigDecimal(BigInteger.TEN, 10);
        AttributeValue value = SchemaAttributeValueSerializer.serializeBigDecimal(largeDecimal);
        assertThat(value).isEqualTo(new AttributeValue().withN(largeDecimal.toPlainString()));
    }

    @Test
    void deserializeBigDecimal_null_returnsNull() {
        BigDecimal value = SchemaAttributeValueSerializer.deserializeBigDecimal(null);
        assertThat(value).isNull();
    }

    @Test
    void deserializeBigDecimal_nullAttributeValue_returnsNull() {
        BigDecimal value = SchemaAttributeValueSerializer.deserializeBigDecimal(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    void deserializeBigDecimal_numberValueNull_returnsNull() {
        BigDecimal value = SchemaAttributeValueSerializer.deserializeBigDecimal(new AttributeValue().withS("not number"));
        assertThat(value).isNull();
    }

    @Test
    void deserializeBigDecimal_numberValue_returnsBigDecimalValue() {
        BigDecimal value = SchemaAttributeValueSerializer.deserializeBigDecimal(new AttributeValue().withN("1"));
        assertThat(value).isEqualTo(new BigDecimal("1"));
    }

}
