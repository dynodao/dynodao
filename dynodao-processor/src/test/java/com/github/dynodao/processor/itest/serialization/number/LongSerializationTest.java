package com.github.dynodao.processor.itest.serialization.number;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.assertj.core.api.Assertions;
import com.github.dynodao.processor.itest.AbstractIntegrationTest;
import com.github.dynodao.processor.test.params.AttributeValueSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class LongSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @Test
    void serializeLong_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeLong(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @MethodSource("longSources")
    void serializeLong_long_returnsAttributeValueWithNumber(Long longValue) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeLong(longValue);
        assertThat(value).isEqualTo(new AttributeValue().withN(String.valueOf(longValue)));
    }

    @ParameterizedTest
    @NullSource
    @AttributeValueSource.WithoutNumber
    void deserializeLong_nullCases_returnsNull(AttributeValue attributeValue) {
        Long value = SchemaAttributeValueSerializer.deserializeLong(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @MethodSource("longSources")
    void deserializeLong_numberValue_returnsLongValue(Long longValue) {
        Long value = SchemaAttributeValueSerializer.deserializeLong(new AttributeValue().withN(String.valueOf(longValue)));
        assertThat(value).isEqualTo(longValue);
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("longSources")
    void putAndGet_symmetricCases_returnsItem(Long longValue) {
        Schema schema = schema(longValue);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));
        Assertions.assertThat(items).containsExactly(schema);
    }

    static Stream<Long> longSources() {
        // long can't store the max number value, so use max/min long instead
        return Stream.of(0, 1, -1, Long.MAX_VALUE, -Long.MAX_VALUE, Long.MIN_VALUE)
                .map(String::valueOf)
                .map(Long::new);
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(Long longValue) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setLongObject(longValue);
        return schema;
    }

}
