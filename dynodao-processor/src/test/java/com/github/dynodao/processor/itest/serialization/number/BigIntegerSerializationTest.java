package com.github.dynodao.processor.itest.serialization.number;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.assertj.core.api.Assertions;
import com.github.dynodao.processor.itest.AbstractIntegrationTest;
import com.github.dynodao.processor.test.params.AttributeValueSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class BigIntegerSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @Test
    void serializeBigDecimal_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeBigDecimal(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @MethodSource("bigIntegerSources")
    void serializeBigInteger_numberValues_returnsNumberAttributeValue(BigInteger bigInteger) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeBigInteger(bigInteger);
        assertThat(value).isEqualTo(new AttributeValue().withN(bigInteger.toString()));
    }

    @ParameterizedTest
    @NullSource
    @AttributeValueSource.WithoutNumber
    void deserializeBigInteger_nullCases_returnsNull(AttributeValue attributeValue) {
        BigInteger value = SchemaAttributeValueSerializer.deserializeBigInteger(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @MethodSource("bigIntegerSources")
    void deserializeBigInteger_numberValue_returnsBigIntegerValue(BigInteger bigInteger) {
        BigInteger value = SchemaAttributeValueSerializer.deserializeBigInteger(new AttributeValue().withN(bigInteger.toString()));
        assertThat(value).isEqualTo(bigInteger);
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("bigIntegerSources")
    void putAndGet_symmetricCases_returnsItem(BigInteger bigInteger) {
        Schema schema = schema(bigInteger);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));
        Assertions.assertThat(items).containsExactly(schema);
    }

    static Stream<BigInteger> bigIntegerSources() {
        return Stream.of(0, 1, -1, Integer.MAX_VALUE, Integer.MIN_VALUE, Long.MAX_VALUE, Long.MAX_VALUE,
                "9.9999999999999999999999999999999999999E+125", "-9.9999999999999999999999999999999999999E+125")
                .map(String::valueOf)
                .map(BigDecimal::new)
                .map(BigDecimal::toBigInteger);
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(BigInteger bigInteger) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setBigInteger(bigInteger);
        return schema;
    }

}
