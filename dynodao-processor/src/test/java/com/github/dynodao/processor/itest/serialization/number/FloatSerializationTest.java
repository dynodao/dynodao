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

class FloatSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @Test
    void serializeFloat_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeFloat(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @MethodSource("floatSources")
    void serializeFloat_float_returnsAttributeValueWithNumber(Float floatValue) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeFloat(floatValue);
        assertThat(value).isEqualTo(new AttributeValue().withN(String.valueOf(floatValue)));
    }

    @ParameterizedTest
    @NullSource
    @AttributeValueSource.WithoutNumber
    void deserializeFloat_nullCases_returnsNull(AttributeValue attributeValue) {
        Float value = SchemaAttributeValueSerializer.deserializeFloat(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @MethodSource("floatSources")
    void deserializeFloat_numberValue_returnsFloatValue(Float floatValue) {
        Float value = SchemaAttributeValueSerializer.deserializeFloat(new AttributeValue().withN(String.valueOf(floatValue)));
        assertThat(value).isEqualTo(floatValue);
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("floatSources")
    void putAndGet_symmetricCases_returnsItem(Float floatValue) {
        Schema schema = schema(floatValue);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));
        assertThat(items).containsExactly(schema);
    }

    static Stream<Float> floatSources() {
        // float can't store the max number value, so use max/min float instead
        return Stream.of(0, 1, -1, Float.MAX_VALUE, -Float.MAX_VALUE, Float.MIN_VALUE, -Float.MIN_VALUE)
                .map(String::valueOf)
                .map(Float::new);
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(Float floatValue) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setFloatObject(floatValue);
        return schema;
    }

}
