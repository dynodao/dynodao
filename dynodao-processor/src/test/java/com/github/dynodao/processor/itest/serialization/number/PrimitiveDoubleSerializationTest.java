package com.github.dynodao.processor.itest.serialization.number;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.github.dynodao.processor.itest.AbstractIntegrationTest;
import com.github.dynodao.processor.test.params.AttributeValueSource;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class PrimitiveDoubleSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @ParameterizedTest
    @MethodSource("doubleSources")
    void serializePrimitiveDouble_onlyUseCase_returnsAttributeValueWithNumber(double doubleValue) {
        AttributeValue value = SchemaAttributeValueSerializer.serializePrimitiveDouble(doubleValue);
        assertThat(value).isEqualTo(new AttributeValue().withN(String.valueOf(doubleValue)));
    }

    @ParameterizedTest
    @NullSource
    @AttributeValueSource.WithoutNumber
    void deserializePrimitiveDouble_nullCases_returnsZero(AttributeValue attributeValue) {
        double value = SchemaAttributeValueSerializer.deserializePrimitiveDouble(attributeValue);
        assertThat(value).isZero();
    }

    @ParameterizedTest
    @MethodSource("doubleSources")
    void deserializePrimitiveDouble_numberValue_returnsDoubleValue(double doubleValue) {
        double value = SchemaAttributeValueSerializer.deserializePrimitiveDouble(new AttributeValue().withN(String.valueOf(doubleValue)));
        assertThat(value).isEqualTo(doubleValue);
    }

    @ParameterizedTest
    @MethodSource("doubleSources")
    void putAndGet_symmetricCases_returnsItem(double doubleValue) {
        Schema schema = schema(doubleValue);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));
        assertThat(items).containsExactly(schema);
    }

    static DoubleStream doubleSources() {
        // double precision can't store the max number value, so remove some of the 9s
        return Stream.of(0, 1, -1, "1E-130", "9.9999999E+125", "-9.9999999E+125", "-1E-130")
                .map(String::valueOf)
                .mapToDouble(Double::new);
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(double doubleValue) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setPrimitiveDouble(doubleValue);
        return schema;
    }

}
