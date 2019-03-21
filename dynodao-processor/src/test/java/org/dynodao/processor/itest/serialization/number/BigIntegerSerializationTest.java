package org.dynodao.processor.itest.serialization.number;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractSourceCompilingTest;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;

class BigIntegerSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializeBigInteger_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeBigInteger(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    void serializeBigInteger_bigInteger_returnsAttributeValueWithNumber() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeBigInteger(new BigInteger("1"));
        assertThat(value).isEqualTo(new AttributeValue().withN("1"));
    }

    @Test
    void deserializeBigInteger_null_returnsNull() {
        BigInteger value = SchemaAttributeValueSerializer.deserializeBigInteger(null);
        assertThat(value).isNull();
    }

    @Test
    void deserializeBigInteger_nullAttributeValue_returnsNull() {
        BigInteger value = SchemaAttributeValueSerializer.deserializeBigInteger(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    void deserializeBigInteger_numberValueNull_returnsNull() {
        BigInteger value = SchemaAttributeValueSerializer.deserializeBigInteger(new AttributeValue().withS("not number"));
        assertThat(value).isNull();
    }

    @Test
    void deserializeBigInteger_numberValue_returnsBigIntegerValue() {
        BigInteger value = SchemaAttributeValueSerializer.deserializeBigInteger(new AttributeValue().withN("1"));
        assertThat(value).isEqualTo(new BigInteger("1"));
    }

}
