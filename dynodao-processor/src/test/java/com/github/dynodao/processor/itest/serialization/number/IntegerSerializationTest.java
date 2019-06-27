package com.github.dynodao.processor.itest.serialization.number;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.github.dynodao.processor.itest.AbstractIntegrationTest;
import com.github.dynodao.processor.test.params.AttributeValueSource;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class IntegerSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @Test
    void serializeInteger_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeInteger(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @MethodSource("integerSources")
    void serializeInteger_integer_returnsAttributeValueWithNumber(Integer integerValue) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeInteger(integerValue);
        assertThat(value).isEqualTo(new AttributeValue().withN(String.valueOf(integerValue)));
    }

    @ParameterizedTest
    @NullSource
    @AttributeValueSource.WithoutNumber
    void deserializeInteger_nullCases_returnsNull(AttributeValue attributeValue) {
        Integer value = SchemaAttributeValueSerializer.deserializeInteger(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @MethodSource("integerSources")
    void deserializeInteger_numberValue_returnsIntegerValue(Integer integerValue) {
        Integer value = SchemaAttributeValueSerializer.deserializeInteger(new AttributeValue().withN(String.valueOf(integerValue)));
        assertThat(value).isEqualTo(integerValue);
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("integerSources")
    void putAndGet_symmetricCases_returnsItem(Integer integerValue) {
        Schema schema = schema(integerValue);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));
        Assertions.assertThat(items).containsExactly(schema);
    }

    static Stream<Integer> integerSources() {
        // integer can't store the max number value, so use max/min integer instead
        return Stream.of(0, 1, -1, Integer.MAX_VALUE, -Integer.MAX_VALUE, Integer.MIN_VALUE)
                .map(String::valueOf)
                .map(Integer::new);
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(Integer integerValue) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setIntegerObject(integerValue);
        return schema;
    }

}
