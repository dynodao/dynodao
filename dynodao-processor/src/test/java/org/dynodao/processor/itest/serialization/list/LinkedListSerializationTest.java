package org.dynodao.processor.itest.serialization.list;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractSourceCompilingTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class LinkedListSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializeLinkedListOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeLinkedListOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    void serializeLinkedListOfString_emptyList_returnsAttributeValueWithEmptyMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeLinkedListOfString(listOf());
        assertThat(value).isEqualTo(new AttributeValue().withL(emptyList()));
    }

    @Test
    void serializeLinkedListOfString_singletonListWithValue_returnsAttributeValueWithSingleSerializedValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeLinkedListOfString(listOf("value"));
        assertThat(value).isEqualTo(new AttributeValue().withL(new AttributeValue("value")));
    }

    @Test
    void serializeLinkedListOfString_singletonListWithNull_returnsAttributeValueWithNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeLinkedListOfString(listOf(null));
        assertThat(value).isEqualTo(new AttributeValue().withL(new AttributeValue().withNULL(true)));
    }

    @Test
    void serializeLinkedListOfString_listWithMultipleValues_returnsAttributeValueWithSerializedList() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeLinkedListOfString(listOf("value1", "value2"));
        assertThat(value).isEqualTo(new AttributeValue().withL(new AttributeValue("value1"), new AttributeValue("value2")));
    }

    @Test
    void serializeLinkedListOfString_listWithSomeNullValues_returnsAttributeValueWithSerializeList() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeLinkedListOfString(listOf("value1", null));
        assertThat(value).isEqualTo(new AttributeValue().withL(new AttributeValue("value1"), new AttributeValue().withNULL(true)));
    }

    @Test
    void serializeLinkedListOfString_listWithAllNullValues_returnsAttributeValueWithSerializeList() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeLinkedListOfString(listOf(null, null));
        assertThat(value).isEqualTo(new AttributeValue().withL(new AttributeValue().withNULL(true), new AttributeValue().withNULL(true)));
    }

    @Test
    void deserializeLinkedListOfString_null_returnsNull() {
        LinkedList<String> value = SchemaAttributeValueSerializer.deserializeLinkedListOfString(null);
        assertThat(value).isNull();
    }

    @Test
    void deserializeLinkedListOfString_nullAttributeValue_returnsNull() {
        LinkedList<String> value = SchemaAttributeValueSerializer.deserializeLinkedListOfString(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    void deserializeLinkedListOfString_listValueNull_returnsNull() {
        LinkedList<String> value = SchemaAttributeValueSerializer.deserializeLinkedListOfString(new AttributeValue().withS("not list"));
        assertThat(value).isNull();
    }

    @Test
    void deserializeLinkedListOfString_emptyMap_returnsEmptyLinkedList() {
        LinkedList<String> value = SchemaAttributeValueSerializer.deserializeLinkedListOfString(new AttributeValue().withL(emptyList()));
        assertThat(value).isEmpty();
    }

    @Test
    void deserializeLinkedListOfString_singletonListWithValue_returnsSingletonLinkedList() {
        LinkedList<String> value = SchemaAttributeValueSerializer.deserializeLinkedListOfString(new AttributeValue().withL(singletonList(new AttributeValue("value"))));
        assertThat(value).containsExactly("value");
    }

    @Test
    void deserializeLinkedListOfString_singletonListWithNull_returnsSingletonLinkedList() {
        LinkedList<String> value = SchemaAttributeValueSerializer.deserializeLinkedListOfString(new AttributeValue().withL(singletonList(null)));
        assertThat(value).containsOnlyNulls().hasSize(1);
    }

    @Test
    void deserializeLinkedListOfString_singletonListWithNullAttributeValue_returnsSingletonLinkedList() {
        LinkedList<String> value = SchemaAttributeValueSerializer.deserializeLinkedListOfString(new AttributeValue().withL(singletonList(new AttributeValue().withNULL(true))));
        assertThat(value).containsOnlyNulls().hasSize(1);
    }

    @Test
    void deserializeLinkedListOfString_listWithMultipleValues_returnsLinkedListWithValues() {
        LinkedList<String> value = SchemaAttributeValueSerializer.deserializeLinkedListOfString(new AttributeValue().withL(listOf(new AttributeValue("value1"), new AttributeValue("value2"))));
        assertThat(value).containsExactly("value1", "value2");
    }

    @Test
    void deserializeLinkedListOfString_listWithMultipleValuesSomeNull_returnsLinkedListWithValueAndNull() {
        LinkedList<String> value = SchemaAttributeValueSerializer.deserializeLinkedListOfString(new AttributeValue().withL(listOf(new AttributeValue("value1"), null)));
        assertThat(value).containsExactly("value1", null);
    }

    @Test
    void deserializeLinkedListOfString_listWithMultipleValuesSomeNullAttributeValue_returnsLinkedListWithValueAndNull() {
        LinkedList<String> value = SchemaAttributeValueSerializer.deserializeLinkedListOfString(new AttributeValue().withL(listOf(new AttributeValue("value1"), new AttributeValue().withNULL(true))));
        assertThat(value).containsExactly("value1", null);
    }

    @Test
    void deserializeLinkedListOfString_listWithMultipleValuesAllNull_returnsLinkedListAllNull() {
        LinkedList<String> value = SchemaAttributeValueSerializer.deserializeLinkedListOfString(new AttributeValue().withL(listOf(null, null)));
        assertThat(value).containsExactly(null, null);
    }

    @Test
    void deserializeLinkedListOfString_listWithMultipleValuesAllNullAttributeValue_returnsLinkedListAllNull() {
        LinkedList<String> value = SchemaAttributeValueSerializer.deserializeLinkedListOfString(new AttributeValue().withL(listOf(new AttributeValue().withNULL(true), new AttributeValue().withNULL(true))));
        assertThat(value).containsExactly(null, null);
    }

    @Test
    void deserializeLinkedListOfString_mapWithMultipleValuesAllMixedNulls_returnsLinkedListAllNull() {
        LinkedList<String> value = SchemaAttributeValueSerializer.deserializeLinkedListOfString(new AttributeValue().withL(listOf(null, new AttributeValue().withNULL(true))));
        assertThat(value).containsExactly(null, null);
    }

    private <T> LinkedList<T> listOf() {
        return new LinkedList<>();
    }

    private <T> LinkedList<T> listOf(T value) {
        return new LinkedList<>(singletonList(value));
    }

    private <T> LinkedList<T> listOf(T value1, T value2) {
        return new LinkedList<>(Arrays.asList(value1, value2));
    }

}
