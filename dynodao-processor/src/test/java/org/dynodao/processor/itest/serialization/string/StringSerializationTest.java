package org.dynodao.processor.itest.serialization.string;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class StringSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @Test
    void serializeString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    void serializeString_emptyString_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeString("");
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    void serializeString_string_returnsAttributeValueWithString() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeString("string");
        assertThat(value).isEqualTo(new AttributeValue("string"));
    }

    @Test
    void deserializeString_null_returnsNull() {
        String value = SchemaAttributeValueSerializer.deserializeString(null);
        assertThat(value).isNull();
    }

    @Test
    void deserializeString_nullAttributeValue_returnsNull() {
        String value = SchemaAttributeValueSerializer.deserializeString(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    void deserializeString_stringValueNull_returnsNull() {
        String value = SchemaAttributeValueSerializer.deserializeString(new AttributeValue().withN("not string"));
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @MethodSource("attributeValuesThatDeserializeToNull")
    void deserializeString_nullCases_returnsNull(AttributeValue attributeValue) {
        String value = SchemaAttributeValueSerializer.deserializeString(attributeValue);
        assertThat(value).isNull();
    }

    static Stream<AttributeValue> attributeValuesThatDeserializeToNull() {
        return Stream.of(null, new AttributeValue().withNULL(true), new AttributeValue().withN("not string"));
    }

    @Test
    void deserializeString_stringValue_returnsString() {
        String value = SchemaAttributeValueSerializer.deserializeString(new AttributeValue().withS("string"));
        assertThat(value).isEqualTo("string");
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "string" })
    void putAndGet_typicalUseCases_returnsItem(String stringValue) {
        Schema schema = schema(stringValue);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));
        assertThat(items).containsExactly(schema);
    }

    @Test
    void putAndGet_nullString_returnsItem() {
        putAndGet_typicalUseCases_returnsItem(null);
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(String stringValue) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setString(stringValue);
        return schema;
    }

}
