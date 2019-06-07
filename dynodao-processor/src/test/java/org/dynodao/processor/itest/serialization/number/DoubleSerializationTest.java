package org.dynodao.processor.itest.serialization.number;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractIntegrationTest;
import org.dynodao.processor.test.ParameterizedTestSources;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class DoubleSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @Test
    void serializeDouble_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeDouble(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @MethodSource("doubleSources")
    void serializeDouble_double_returnsAttributeValueWithNumber(Double doubleValue) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeDouble(doubleValue);
        assertThat(value).isEqualTo(new AttributeValue().withN(String.valueOf(doubleValue)));
    }

    @ParameterizedTest
    @NullSource
    @ParameterizedTestSources.AttributeValuesWithoutNumberSource
    void deserializeDouble_nullCases_returnsNull(AttributeValue attributeValue) {
        Double value = SchemaAttributeValueSerializer.deserializeDouble(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @MethodSource("doubleSources")
    void deserializeDouble_numberValue_returnsDoubleValue(Double doubleValue) {
        Double value = SchemaAttributeValueSerializer.deserializeDouble(new AttributeValue().withN(String.valueOf(doubleValue)));
        assertThat(value).isEqualTo(doubleValue);
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("doubleSources")
    void putAndGet_symmetricCases_returnsItem(Double doubleValue) {
        Schema schema = schema(doubleValue);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));
        assertThat(items).containsExactly(schema);
    }

    static Stream<Double> doubleSources() {
        // double precision can't store the max number value, so remove some of the 9s
        return Stream.of(0, 1, -1, "1E-130", "9.9999999E+125", "-9.9999999E+125", "-1E-130")
                .map(String::valueOf)
                .map(Double::new);
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(Double doubleValue) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setDoubleObject(doubleValue);
        return schema;
    }

}
