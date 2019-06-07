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

class ByteSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @Test
    void serializeByte_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeByte(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @MethodSource("byteSources")
    void serializeByte_byte_returnsAttributeValueWithNumber(Byte byteValue) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeByte(byteValue);
        assertThat(value).isEqualTo(new AttributeValue().withN(String.valueOf(byteValue)));
    }

    @ParameterizedTest
    @NullSource
    @ParameterizedTestSources.AttributeValuesWithoutNumberSource
    void deserializeByte_nullCases_returnsNull(AttributeValue attributeValue) {
        Byte value = SchemaAttributeValueSerializer.deserializeByte(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @MethodSource("byteSources")
    void deserializeByte_numberValue_returnsByteValue(Byte byteValue) {
        Byte value = SchemaAttributeValueSerializer.deserializeByte(new AttributeValue().withN(String.valueOf(byteValue)));
        assertThat(value).isEqualTo(byteValue);
    }

    @ParameterizedTest
    @NullSource
    @MethodSource("byteSources")
    void putAndGet_symmetricCases_returnsItem(Byte byteValue) {
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

    private Schema schema(Byte byteValue) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setByteObject(byteValue);
        return schema;
    }

}
