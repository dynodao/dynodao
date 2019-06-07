package org.dynodao.processor.itest.serialization.number;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractIntegrationTest;
import org.dynodao.processor.test.ParameterizedTestSources;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class PrimitiveIntSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @ParameterizedTest
    @MethodSource("intSources")
    void serializePrimitiveInt_onlyUseCase_returnsAttributeValueWithNumber(int intValue) {
        AttributeValue value = SchemaAttributeValueSerializer.serializePrimitiveInt(intValue);
        assertThat(value).isEqualTo(new AttributeValue().withN(String.valueOf(intValue)));
    }

    @ParameterizedTest
    @NullSource
    @ParameterizedTestSources.AttributeValuesWithoutNumberSource
    void deserializePrimitiveInt_nullCases_returnsZero(AttributeValue attributeValue) {
        int value = SchemaAttributeValueSerializer.deserializePrimitiveInt(attributeValue);
        assertThat(value).isZero();
    }

    @ParameterizedTest
    @MethodSource("intSources")
    void deserializePrimitiveInt_numberValue_returnsIntValue(int intValue) {
        int value = SchemaAttributeValueSerializer.deserializePrimitiveInt(new AttributeValue().withN(String.valueOf(intValue)));
        assertThat(value).isEqualTo(intValue);
    }

    @ParameterizedTest
    @MethodSource("intSources")
    void putAndGet_symmetricCases_returnsItem(int intValue) {
        Schema schema = schema(intValue);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));
        assertThat(items).containsExactly(schema);
    }

    static IntStream intSources() {
        // integer can't store the max number value, so use max/min integer instead
        return Stream.of(0, 1, -1, Integer.MAX_VALUE, -Integer.MAX_VALUE, Integer.MIN_VALUE)
                .map(String::valueOf)
                .mapToInt(Integer::new);
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(int intValue) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setPrimitiveInt(intValue);
        return schema;
    }
    
}
