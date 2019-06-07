package org.dynodao.processor.itest.serialization.character;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractIntegrationTest;
import org.dynodao.processor.test.params.AttributeValueSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CharacterSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @ParameterizedTest
    @NullSource
    void serializeCharacter_nullCases_returnsNullAttributeValue(Character ch) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeCharacter(ch);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @ValueSource(chars = { ' ', 'a', '\0', Character.MIN_VALUE, Character.MAX_VALUE })
    void serializeCharacter_char_returnsAttributeValueWithString(Character ch) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeCharacter(ch);
        assertThat(value).isEqualTo(new AttributeValue().withS(ch.toString()));
    }

    @ParameterizedTest
    @NullSource
    @AttributeValueSource.WithoutString
    void deserializeCharacter_nullCases_returnsNull(AttributeValue attributeValue) {
        Character value = SchemaAttributeValueSerializer.deserializeCharacter(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = { " ", "a", "\0", "" + Character.MIN_VALUE, "" + Character.MAX_VALUE, " a", "aa", "\0\n" })
    void deserializeCharacter_stringValue_returnsFirstCharacterOfString(String string) {
        Character value = SchemaAttributeValueSerializer.deserializeCharacter(new AttributeValue().withS(string));
        assertThat(value).isEqualTo(string.charAt(0));
    }

    @Test
    void deserializeCharacter_emptyString_throwsIndexOutOfBoundsException() {
        assertThatThrownBy(() -> SchemaAttributeValueSerializer.deserializeCharacter(new AttributeValue().withS("")))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(chars = { ' ', 'a', '\0', Character.MIN_VALUE, Character.MAX_VALUE })
    void putAndGet_symmetricCases_returnsItem(Character ch) {
        Schema schema = schema(ch);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));
        assertThat(items).containsExactly(schema);
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(Character ch) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setCharacterObject(ch);
        return schema;
    }

}
