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
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class BigDecimalSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @Test
    void serializeBigDecimal_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeBigDecimal(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @MethodSource("bigDecimalSources")
    void serializeBigDecimal_numberValues_returnsNumberAttributeValueUsingPlainString(BigDecimal bigDecimal) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeBigDecimal(bigDecimal);
        assertThat(value).isEqualTo(new AttributeValue().withN(bigDecimal.toPlainString()));
    }

    @ParameterizedTest
    @NullSource
    @AttributeValueSource.WithoutNumber
    void deserializeBigDecimal_nullCases_returnsNull(AttributeValue attributeValue) {
        BigDecimal value = SchemaAttributeValueSerializer.deserializeBigDecimal(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @MethodSource("bigDecimalSources")
    void deserializeBigDecimal_numberValue_returnsBigDecimalValue(BigDecimal bigDecimal) {
        BigDecimal value = SchemaAttributeValueSerializer.deserializeBigDecimal(new AttributeValue().withN(bigDecimal.toPlainString()));
        assertThat(value).isEqualTo(new BigDecimal(bigDecimal.toPlainString()));
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("bigDecimalSources")
    void putAndGet_symmetricCases_returnsItem(BigDecimal bigDecimal) {
        Schema schema = schema(bigDecimal == null ? null : new BigDecimal(bigDecimal.toPlainString()));
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));
        Assertions.assertThat(items).containsExactly(schema);
    }

    static Stream<BigDecimal> bigDecimalSources() {
        return Stream.of(0, 1, -1, Integer.MAX_VALUE, Integer.MIN_VALUE, Long.MAX_VALUE, Long.MAX_VALUE, "1e10", "-1e10", "1e-10", "-1e-10",
                "1E-130", "9.9999999999999999999999999999999999999E+125", "-9.9999999999999999999999999999999999999E+125", "-1E-130")
                .map(String::valueOf)
                .map(BigDecimal::new);
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(BigDecimal bigDecimal) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setBigDecimal(bigDecimal);
        return schema;
    }

}
