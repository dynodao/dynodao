package com.github.dynodao.processor.itest.serialization.string;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.github.dynodao.processor.itest.AbstractIntegrationTest;
import com.github.dynodao.processor.test.params.AttributeValueSource;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class StringSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";
    
    @ParameterizedTest
    @NullAndEmptySource
    void serializeString_nullCases_returnsNullAttributeValue(String stringValue) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeString(stringValue);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @ValueSource(strings = { "string", "\t", "\n", "\r", " " })
    void serializeString_string_returnsAttributeValueWithString(String string) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeString(string);
        assertThat(value).isEqualTo(new AttributeValue(string));
    }

    @ParameterizedTest
    @NullSource
    @AttributeValueSource.WithoutString
    void deserializeString_nullCases_returnsNull(AttributeValue attributeValue) {
        String value = SchemaAttributeValueSerializer.deserializeString(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = { "string", "\t", "\n", "\r", " " })
    void deserializeString_string_returnsString(String string) {
        String value = SchemaAttributeValueSerializer.deserializeString(new AttributeValue().withS(string));
        assertThat(value).isEqualTo(string);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "string", "\t", "\n", "\r", " " })
    void putAndGet_symmetricCases_returnsItem(String stringValue) {
        Schema schema = schema(stringValue);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));
        Assertions.assertThat(items).containsExactly(schema);
    }

    @ParameterizedTest
    @EmptySource
    void putAndGet_asymmetricStringToNull_returnsItemWithNull(String string) {
        Schema schema = schema(string);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));
        Assertions.assertThat(items).containsExactly(schema(null));
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
