package org.dynodao.processor.itest.serialization.number;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractIntegrationTest;
import org.dynodao.processor.test.ParameterizedTestSources;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class PrimitiveByteSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @ParameterizedTest
    @MethodSource("byteSources")
    void serializePrimitiveByte_onlyUseCase_returnsAttributeValueWithNumber(byte byteValue) {
        AttributeValue value = SchemaAttributeValueSerializer.serializePrimitiveByte(byteValue);
        assertThat(value).isEqualTo(new AttributeValue().withN(String.valueOf(byteValue)));
    }

    @ParameterizedTest
    @NullSource
    @MethodSource(ParameterizedTestSources.ATTRIBUTE_VALUES_WITHOUT_NUMBER_SOURCE)
    void deserializePrimitiveByte_nullCases_returnsZero(AttributeValue attributeValue) {
        byte value = SchemaAttributeValueSerializer.deserializePrimitiveByte(attributeValue);
        assertThat(value).isZero();
    }

    @ParameterizedTest
    @MethodSource("byteSources")
    void deserializePrimitiveByte_numberValue_returnsByteValue(byte byteValue) {
        byte value = SchemaAttributeValueSerializer.deserializePrimitiveByte(new AttributeValue().withN(String.valueOf(byteValue)));
        assertThat(value).isEqualTo(byteValue);
    }

    @ParameterizedTest
    @MethodSource("byteSources")
    void putAndGet_symmetricCases_returnsItem(byte byteValue) {
        Schema schema = schema(byteValue);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));
        assertThat(items).containsExactly(schema);
    }

    static Stream<Byte> byteSources() {
        return Stream.of(0, 1, -1, Byte.MIN_VALUE, Byte.MAX_VALUE)
                .map(String::valueOf)
                .map(Byte::new);
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(byte byteValue) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setPrimitiveByte(byteValue);
        return schema;
    }

}
