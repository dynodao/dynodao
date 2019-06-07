package org.dynodao.processor.itest.serialization.number;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractIntegrationTest;
import org.dynodao.processor.test.ParameterizedTestSources;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class PrimitiveFloatSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @ParameterizedTest
    @MethodSource("floatSources")
    void serializePrimitiveFloat_onlyUseCase_returnsAttributeValueWithNumber(float floatValue) {
        AttributeValue value = SchemaAttributeValueSerializer.serializePrimitiveFloat(floatValue);
        assertThat(value).isEqualTo(new AttributeValue().withN(String.valueOf(floatValue)));
    }

    @ParameterizedTest
    @NullSource
    @ParameterizedTestSources.AttributeValuesWithoutNumberSource
    void deserializePrimitiveFloat_nullCases_returnsZero(AttributeValue attributeValue) {
        float value = SchemaAttributeValueSerializer.deserializePrimitiveFloat(attributeValue);
        assertThat(value).isZero();
    }

    @ParameterizedTest
    @MethodSource("floatSources")
    void deserializePrimitiveFloat_numberValue_returnsFloatValue(float floatValue) {
        float value = SchemaAttributeValueSerializer.deserializePrimitiveFloat(new AttributeValue().withN(String.valueOf(floatValue)));
        assertThat(value).isEqualTo(floatValue);
    }

    @ParameterizedTest
    @MethodSource("floatSources")
    void putAndGet_symmetricCases_returnsItem(float floatValue) {
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

    private Schema schema(float floatValue) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setPrimitiveFloat(floatValue);
        return schema;
    }

}
