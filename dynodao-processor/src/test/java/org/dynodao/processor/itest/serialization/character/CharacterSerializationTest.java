package org.dynodao.processor.itest.serialization.character;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractSourceCompilingTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CharacterSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializeCharacter_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeCharacter(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    void serializeCharacter_char_returnsAttributeValueWithString() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeCharacter('a');
        assertThat(value).isEqualTo(new AttributeValue().withS("a"));
    }

    @Test
    void deserializeCharacter_null_returnsNull() {
        Character value = SchemaAttributeValueSerializer.deserializeCharacter(null);
        assertThat(value).isNull();
    }

    @Test
    void deserializeCharacter_nullAttributeValue_returnsNull() {
        Character value = SchemaAttributeValueSerializer.deserializeCharacter(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    void deserializeCharacter_stringValueNull_returnsNull() {
        Character value = SchemaAttributeValueSerializer.deserializeCharacter(new AttributeValue().withSS("not string"));
        assertThat(value).isNull();
    }

    @Test
    void deserializeCharacter_stringValue_returnsCharacterValue() {
        Character value = SchemaAttributeValueSerializer.deserializeCharacter(new AttributeValue().withS("a"));
        assertThat(value).isEqualTo('a');
    }

    @Test
    void deserializeCharacter_longStringStored_returnsFirstCharacter() {
        Character value = SchemaAttributeValueSerializer.deserializeCharacter(new AttributeValue().withS("abc"));
        assertThat(value).isEqualTo('a');
    }

    @Test
    void deserializeCharacter_emptyString_throwsIndexOutOfBoundsException() {
        assertThatThrownBy(() -> SchemaAttributeValueSerializer.deserializeCharacter(new AttributeValue().withS("")))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

}
