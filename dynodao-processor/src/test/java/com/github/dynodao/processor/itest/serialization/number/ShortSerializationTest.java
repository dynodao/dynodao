package com.github.dynodao.processor.itest.serialization.number;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.github.dynodao.processor.itest.AbstractIntegrationTest;
import com.github.dynodao.processor.test.params.AttributeValueSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ShortSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @Test
    void serializeShort_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeShort(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @MethodSource("shortSources")
    void serializeShort_short_returnsAttributeValueWithNumber(Short shortValue) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeShort(shortValue);
        assertThat(value).isEqualTo(new AttributeValue().withN(String.valueOf(shortValue)));
    }

    @ParameterizedTest
    @NullSource
    @AttributeValueSource.WithoutNumber
    void deserializeShort_nullCases_returnsNull(AttributeValue attributeValue) {
        Short value = SchemaAttributeValueSerializer.deserializeShort(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @MethodSource("shortSources")
    void deserializeShort_numberValue_returnsShortValue(Short shortValue) {
        Short value = SchemaAttributeValueSerializer.deserializeShort(new AttributeValue().withN(String.valueOf(shortValue)));
        assertThat(value).isEqualTo(shortValue);
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("shortSources")
    void putAndGet_symmetricCases_returnsItem(Short shortValue) {
        Schema schema = schema(shortValue);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));
        assertThat(items).containsExactly(schema);
    }

    static Stream<Short> shortSources() {
        // short can't store the max number value, so use max/min short instead
        return Stream.of(0, 1, -1, Short.MAX_VALUE, -Short.MAX_VALUE, Short.MIN_VALUE)
                .map(String::valueOf)
                .map(Short::new);
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(Short shortValue) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setShortObject(shortValue);
        return schema;
    }

}
