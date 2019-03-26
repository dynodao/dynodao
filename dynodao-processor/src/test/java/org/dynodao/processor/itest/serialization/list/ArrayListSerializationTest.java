package org.dynodao.processor.itest.serialization.list;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractSourceCompilingTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class ArrayListSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializeArrayListOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeArrayListOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    void serializeArrayListOfString_emptyList_returnsAttributeValueWithEmptyMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeArrayListOfString(listOf());
        assertThat(value).isEqualTo(new AttributeValue().withL(emptyList()));
    }

    @Test
    void serializeArrayListOfString_singletonListWithValue_returnsAttributeValueWithSingleSerializedValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeArrayListOfString(listOf("value"));
        assertThat(value).isEqualTo(new AttributeValue().withL(new AttributeValue("value")));
    }

    @Test
    void serializeArrayListOfString_singletonListWithNull_returnsAttributeValueWithNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeArrayListOfString(listOf(null));
        assertThat(value).isEqualTo(new AttributeValue().withL(new AttributeValue().withNULL(true)));
    }

    @Test
    void serializeArrayListOfString_listWithMultipleValues_returnsAttributeValueWithSerializedList() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeArrayListOfString(listOf("value1", "value2"));
        assertThat(value).isEqualTo(new AttributeValue().withL(new AttributeValue("value1"), new AttributeValue("value2")));
    }

    @Test
    void serializeArrayListOfString_listWithSomeNullValues_returnsAttributeValueWithSerializeList() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeArrayListOfString(listOf("value1", null));
        assertThat(value).isEqualTo(new AttributeValue().withL(new AttributeValue("value1"), new AttributeValue().withNULL(true)));
    }

    @Test
    void serializeArrayListOfString_listWithAllNullValues_returnsAttributeValueWithSerializeList() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeArrayListOfString(listOf(null, null));
        assertThat(value).isEqualTo(new AttributeValue().withL(new AttributeValue().withNULL(true), new AttributeValue().withNULL(true)));
    }

    @Test
    void deserializeArrayListOfString_null_returnsNull() {
        ArrayList<String> value = SchemaAttributeValueSerializer.deserializeArrayListOfString(null);
        assertThat(value).isNull();
    }

    @Test
    void deserializeArrayListOfString_nullAttributeValue_returnsNull() {
        ArrayList<String> value = SchemaAttributeValueSerializer.deserializeArrayListOfString(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    void deserializeArrayListOfString_listValueNull_returnsNull() {
        ArrayList<String> value = SchemaAttributeValueSerializer.deserializeArrayListOfString(new AttributeValue().withS("not list"));
        assertThat(value).isNull();
    }

    @Test
    void deserializeArrayListOfString_emptyMap_returnsEmptyArrayList() {
        ArrayList<String> value = SchemaAttributeValueSerializer.deserializeArrayListOfString(new AttributeValue().withL(emptyList()));
        assertThat(value).isEmpty();
    }

    @Test
    void deserializeArrayListOfString_singletonListWithValue_returnsSingletonArrayList() {
        ArrayList<String> value = SchemaAttributeValueSerializer.deserializeArrayListOfString(new AttributeValue().withL(singletonList(new AttributeValue("value"))));
        assertThat(value).containsExactly("value");
    }

    @Test
    void deserializeArrayListOfString_singletonListWithNull_returnsSingletonArrayList() {
        ArrayList<String> value = SchemaAttributeValueSerializer.deserializeArrayListOfString(new AttributeValue().withL(singletonList(null)));
        assertThat(value).containsOnlyNulls().hasSize(1);
    }

    @Test
    void deserializeArrayListOfString_singletonListWithNullAttributeValue_returnsSingletonArrayList() {
        ArrayList<String> value = SchemaAttributeValueSerializer.deserializeArrayListOfString(new AttributeValue().withL(singletonList(new AttributeValue().withNULL(true))));
        assertThat(value).containsOnlyNulls().hasSize(1);
    }

    @Test
    void deserializeArrayListOfString_listWithMultipleValues_returnsArrayListWithValues() {
        ArrayList<String> value = SchemaAttributeValueSerializer.deserializeArrayListOfString(new AttributeValue().withL(listOf(new AttributeValue("value1"), new AttributeValue("value2"))));
        assertThat(value).containsExactly("value1", "value2");
    }

    @Test
    void deserializeArrayListOfString_listWithMultipleValuesSomeNull_returnsArrayListWithValueAndNull() {
        ArrayList<String> value = SchemaAttributeValueSerializer.deserializeArrayListOfString(new AttributeValue().withL(listOf(new AttributeValue("value1"), null)));
        assertThat(value).containsExactly("value1", null);
    }

    @Test
    void deserializeArrayListOfString_listWithMultipleValuesSomeNullAttributeValue_returnsArrayListWithValueAndNull() {
        ArrayList<String> value = SchemaAttributeValueSerializer.deserializeArrayListOfString(new AttributeValue().withL(listOf(new AttributeValue("value1"), new AttributeValue().withNULL(true))));
        assertThat(value).containsExactly("value1", null);
    }

    @Test
    void deserializeArrayListOfString_listWithMultipleValuesAllNull_returnsArrayListAllNull() {
        ArrayList<String> value = SchemaAttributeValueSerializer.deserializeArrayListOfString(new AttributeValue().withL(listOf(null, null)));
        assertThat(value).containsExactly(null, null);
    }

    @Test
    void deserializeArrayListOfString_listWithMultipleValuesAllNullAttributeValue_returnsArrayListAllNull() {
        ArrayList<String> value = SchemaAttributeValueSerializer.deserializeArrayListOfString(new AttributeValue().withL(listOf(new AttributeValue().withNULL(true), new AttributeValue().withNULL(true))));
        assertThat(value).containsExactly(null, null);
    }

    @Test
    void deserializeArrayListOfString_mapWithMultipleValuesAllMixedNulls_returnsArrayListAllNull() {
        ArrayList<String> value = SchemaAttributeValueSerializer.deserializeArrayListOfString(new AttributeValue().withL(listOf(null, new AttributeValue().withNULL(true))));
        assertThat(value).containsExactly(null, null);
    }

    private <T> ArrayList<T> listOf() {
        return new ArrayList<>();
    }

    private <T> ArrayList<T> listOf(T value) {
        return new ArrayList<>(singletonList(value));
    }

    private <T> ArrayList<T> listOf(T value1, T value2) {
        return new ArrayList<>(Arrays.asList(value1, value2));
    }

}
