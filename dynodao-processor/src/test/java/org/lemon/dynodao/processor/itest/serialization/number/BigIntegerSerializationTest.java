package org.lemon.dynodao.processor.itest.serialization.number;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.junit.Test;
import org.lemon.dynodao.processor.itest.AbstractSourceCompilingTest;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class BigIntegerSerializationTest extends AbstractSourceCompilingTest {

    @Test
    public void serializeBigInteger_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeBigInteger(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    public void serializeBigInteger_bigInteger_returnsAttributeValueWithNumber() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeBigInteger(new BigInteger("1"));
        assertThat(value).isEqualTo(new AttributeValue().withN("1"));
    }

    @Test
    public void deserializeBigInteger_null_returnsNull() {
        BigInteger value = SchemaAttributeValueSerializer.deserializeBigInteger(null);
        assertThat(value).isNull();
    }

    @Test
    public void deserializeBigInteger_nullAttributeValue_returnsNull() {
        BigInteger value = SchemaAttributeValueSerializer.deserializeBigInteger(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    public void deserializeBigInteger_numberValueNull_returnsNull() {
        BigInteger value = SchemaAttributeValueSerializer.deserializeBigInteger(new AttributeValue().withS("not number"));
        assertThat(value).isNull();
    }

    @Test
    public void deserializeBigInteger_numberValue_returnsBigIntegerValue() {
        BigInteger value = SchemaAttributeValueSerializer.deserializeBigInteger(new AttributeValue().withN("1"));
        assertThat(value).isEqualTo(new BigInteger("1"));
    }

}
