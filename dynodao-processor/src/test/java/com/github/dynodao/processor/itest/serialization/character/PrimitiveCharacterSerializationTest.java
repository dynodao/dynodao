package com.github.dynodao.processor.itest.serialization.character;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.github.dynodao.processor.itest.AbstractIntegrationTest;
import com.github.dynodao.processor.test.params.AttributeValueSource;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PrimitiveCharacterSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @ParameterizedTest
    @ValueSource(chars = { ' ', 'a', '\0', Character.MIN_VALUE, Character.MAX_VALUE })
    void serializePrimitiveChar_charValue_returnsAttributeValueWithChar(char ch) {
        AttributeValue value = SchemaAttributeValueSerializer.serializePrimitiveChar(ch);
        assertThat(value).isEqualTo(new AttributeValue().withS(String.valueOf(ch)));
    }

    @ParameterizedTest
    @NullSource
    @AttributeValueSource.WithoutString
    void deserializePrimitiveChar_nullCases_returnsNullCharacter(AttributeValue attributeValue) {
        char value = SchemaAttributeValueSerializer.deserializePrimitiveChar(attributeValue);
        assertThat(value).isEqualTo('\0');
    }

    @ParameterizedTest
    @ValueSource(strings = { " ", "a", "\0", "" + Character.MIN_VALUE, "" + Character.MAX_VALUE, " a", "aa", "\0\n" })
    void deserializePrimitiveChar_stringValue_returnsFirstCharacterOfString(String string) {
        char value = SchemaAttributeValueSerializer.deserializePrimitiveChar(new AttributeValue().withS(string));
        assertThat(value).isEqualTo(string.charAt(0));
    }

    @Test
    void deserializePrimitiveChar_emptyString_throwsIndexOutOfBoundsException() {
        assertThatThrownBy(() -> SchemaAttributeValueSerializer.deserializePrimitiveChar(new AttributeValue().withS("")))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @ParameterizedTest
    @ValueSource(chars = { ' ', 'a', '\0', Character.MIN_VALUE, Character.MAX_VALUE })
    void putAndGet_symmetricCases_returnsItem(char ch) {
        Schema schema = schema(ch);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));
        Assertions.assertThat(items).containsExactly(schema);
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(char ch) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setPrimitiveCharacter(ch);
        return schema;
    }

}
