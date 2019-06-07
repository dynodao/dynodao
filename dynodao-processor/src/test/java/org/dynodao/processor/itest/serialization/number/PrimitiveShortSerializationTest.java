package org.dynodao.processor.itest.serialization.number;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractIntegrationTest;
import org.dynodao.processor.test.ParameterizedTestSources;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class PrimitiveShortSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @ParameterizedTest
    @MethodSource("shortSources")
    void serializePrimitiveShort_onlyUseCase_returnsAttributeValueWithNumber(short shortValue) {
        AttributeValue value = SchemaAttributeValueSerializer.serializePrimitiveShort(shortValue);
        assertThat(value).isEqualTo(new AttributeValue().withN(String.valueOf(shortValue)));
    }

    @ParameterizedTest
    @NullSource
    @ParameterizedTestSources.AttributeValuesWithoutNumberSource
    void deserializePrimitiveShort_nullCases_returnsZero(AttributeValue attributeValue) {
        short value = SchemaAttributeValueSerializer.deserializePrimitiveShort(attributeValue);
        assertThat(value).isZero();
    }

    @ParameterizedTest
    @MethodSource("shortSources")
    void deserializePrimitiveShort_numberValue_returnsShortValue(short shortValue) {
        short value = SchemaAttributeValueSerializer.deserializePrimitiveShort(new AttributeValue().withN(String.valueOf(shortValue)));
        assertThat(value).isEqualTo(shortValue);
    }

    @ParameterizedTest
    @MethodSource("shortSources")
    void putAndGet_symmetricCases_returnsItem(short shortValue) {
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

    private Schema schema(short shortValue) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setPrimitiveShort(shortValue);
        return schema;
    }

}
