package org.dynodao.processor.itest.serialization.character;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractSourceCompilingTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PrimitiveCharacterSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializePrimitiveChar_onlyUseCase_returnsAttributeValueWithChar() {
        AttributeValue value = SchemaAttributeValueSerializer.serializePrimitiveChar('a');
        assertThat(value).isEqualTo(new AttributeValue().withS("a"));
    }

    @Test
    void deserializePrimitiveChar_null_returnsNullCharacter() {
        char value = SchemaAttributeValueSerializer.deserializePrimitiveChar(null);
        assertThat(value).isEqualTo('\0');
    }

    @Test
    void deserializePrimitiveChar_nullAttributeValue_returnsNullCharacter() {
        char value = SchemaAttributeValueSerializer.deserializePrimitiveChar(new AttributeValue().withNULL(true));
        assertThat(value).isEqualTo('\0');
    }

    @Test
    void deserializePrimitiveChar_stringValueNull_returnsNullCharacter() {
        char value = SchemaAttributeValueSerializer.deserializePrimitiveChar(new AttributeValue().withSS("not string"));
        assertThat(value).isEqualTo('\0');
    }

    @Test
    void deserializePrimitiveChar_charValue_returnsCharValue() {
        char value = SchemaAttributeValueSerializer.deserializePrimitiveChar(new AttributeValue().withS("a"));
        assertThat(value).isEqualTo('a');
    }

    @Test
    void deserializePrimitiveChar_longStringStored_returnsFirstCharacter() {
        char value = SchemaAttributeValueSerializer.deserializePrimitiveChar(new AttributeValue().withS("abc"));
        assertThat(value).isEqualTo('a');
    }

    @Test
    void deserializePrimitiveChar_emptyString_throwsIndexOutOfBoundsException() {
        assertThatThrownBy(() -> SchemaAttributeValueSerializer.deserializePrimitiveChar(new AttributeValue().withS("")))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

}
