package org.dynodao.processor.itest.serialization.list;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractSourceCompilingTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class NoTypeArgsListSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializeNoTypeArgsList_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeNoTypeArgsList(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    void serializeNoTypeArgsList_emptyList_returnsAttributeValueWithEmptyMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeNoTypeArgsList(listOf());
        assertThat(value).isEqualTo(new AttributeValue().withL(emptyList()));
    }

    @Test
    void serializeNoTypeArgsList_singletonListWithValue_returnsAttributeValueWithSingleSerializedValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeNoTypeArgsList(listOf("value"));
        assertThat(value).isEqualTo(new AttributeValue().withL(new AttributeValue("value")));
    }

    @Test
    void serializeNoTypeArgsList_singletonListWithNull_returnsAttributeValueWithNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeNoTypeArgsList(listOf(null));
        assertThat(value).isEqualTo(new AttributeValue().withL(new AttributeValue().withNULL(true)));
    }

    @Test
    void serializeNoTypeArgsList_listWithMultipleValues_returnsAttributeValueWithSerializedList() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeNoTypeArgsList(listOf("value1", "value2"));
        assertThat(value).isEqualTo(new AttributeValue().withL(new AttributeValue("value1"), new AttributeValue("value2")));
    }

    @Test
    void serializeNoTypeArgsList_listWithSomeNullValues_returnsAttributeValueWithSerializeList() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeNoTypeArgsList(listOf("value1", null));
        assertThat(value).isEqualTo(new AttributeValue().withL(new AttributeValue("value1"), new AttributeValue().withNULL(true)));
    }

    @Test
    void serializeNoTypeArgsList_listWithAllNullValues_returnsAttributeValueWithSerializeList() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeNoTypeArgsList(listOf(null, null));
        assertThat(value).isEqualTo(new AttributeValue().withL(new AttributeValue().withNULL(true), new AttributeValue().withNULL(true)));
    }

    @Test
    void deserializeNoTypeArgsList_null_returnsNull() {
        NoTypeArgsList value = SchemaAttributeValueSerializer.deserializeNoTypeArgsList(null);
        assertThat(value).isNull();
    }

    @Test
    void deserializeNoTypeArgsList_nullAttributeValue_returnsNull() {
        NoTypeArgsList value = SchemaAttributeValueSerializer.deserializeNoTypeArgsList(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    void deserializeNoTypeArgsList_listValueNull_returnsNull() {
        NoTypeArgsList value = SchemaAttributeValueSerializer.deserializeNoTypeArgsList(new AttributeValue().withS("not list"));
        assertThat(value).isNull();
    }

    @Test
    void deserializeNoTypeArgsList_emptyMap_returnsEmptyNoTypeArgsList() {
        NoTypeArgsList value = SchemaAttributeValueSerializer.deserializeNoTypeArgsList(new AttributeValue().withL(emptyList()));
        assertThat(value).isEmpty();
    }

    @Test
    void deserializeNoTypeArgsList_singletonListWithValue_returnsSingletonNoTypeArgsList() {
        NoTypeArgsList value = SchemaAttributeValueSerializer.deserializeNoTypeArgsList(new AttributeValue().withL(singletonList(new AttributeValue("value"))));
        assertThat(value).containsExactly("value");
    }

    @Test
    void deserializeNoTypeArgsList_singletonListWithNull_returnsSingletonNoTypeArgsList() {
        NoTypeArgsList value = SchemaAttributeValueSerializer.deserializeNoTypeArgsList(new AttributeValue().withL(singletonList(null)));
        assertThat(value).containsOnlyNulls().hasSize(1);
    }

    @Test
    void deserializeNoTypeArgsList_singletonListWithNullAttributeValue_returnsSingletonNoTypeArgsList() {
        NoTypeArgsList value = SchemaAttributeValueSerializer.deserializeNoTypeArgsList(new AttributeValue().withL(singletonList(new AttributeValue().withNULL(true))));
        assertThat(value).containsOnlyNulls().hasSize(1);
    }

    @Test
    void deserializeNoTypeArgsList_listWithMultipleValues_returnsNoTypeArgsListWithValues() {
        NoTypeArgsList value = SchemaAttributeValueSerializer.deserializeNoTypeArgsList(new AttributeValue().withL(arrayListOf(new AttributeValue("value1"), new AttributeValue("value2"))));
        assertThat(value).containsExactly("value1", "value2");
    }

    @Test
    void deserializeNoTypeArgsList_listWithMultipleValuesSomeNull_returnsNoTypeArgsListWithValueAndNull() {
        NoTypeArgsList value = SchemaAttributeValueSerializer.deserializeNoTypeArgsList(new AttributeValue().withL(arrayListOf(new AttributeValue("value1"), null)));
        assertThat(value).containsExactly("value1", null);
    }

    @Test
    void deserializeNoTypeArgsList_listWithMultipleValuesSomeNullAttributeValue_returnsNoTypeArgsListWithValueAndNull() {
        NoTypeArgsList value = SchemaAttributeValueSerializer.deserializeNoTypeArgsList(new AttributeValue().withL(arrayListOf(new AttributeValue("value1"), new AttributeValue().withNULL(true))));
        assertThat(value).containsExactly("value1", null);
    }

    @Test
    void deserializeNoTypeArgsList_listWithMultipleValuesAllNull_returnsNoTypeArgsListAllNull() {
        NoTypeArgsList value = SchemaAttributeValueSerializer.deserializeNoTypeArgsList(new AttributeValue().withL(arrayListOf(null, null)));
        assertThat(value).containsExactly(null, null);
    }

    @Test
    void deserializeNoTypeArgsList_listWithMultipleValuesAllNullAttributeValue_returnsNoTypeArgsListAllNull() {
        NoTypeArgsList value = SchemaAttributeValueSerializer.deserializeNoTypeArgsList(new AttributeValue().withL(arrayListOf(new AttributeValue().withNULL(true), new AttributeValue().withNULL(true))));
        assertThat(value).containsExactly(null, null);
    }

    @Test
    void deserializeNoTypeArgsList_mapWithMultipleValuesAllMixedNulls_returnsNoTypeArgsListAllNull() {
        NoTypeArgsList value = SchemaAttributeValueSerializer.deserializeNoTypeArgsList(new AttributeValue().withL(arrayListOf(null, new AttributeValue().withNULL(true))));
        assertThat(value).containsExactly(null, null);
    }

    private NoTypeArgsList listOf() {
        return new NoTypeArgsList();
    }

    private NoTypeArgsList listOf(String value) {
        NoTypeArgsList list = new NoTypeArgsList();
        list.add(value);
        return list;
    }

    private NoTypeArgsList listOf(String value1, String value2) {
        NoTypeArgsList list = new NoTypeArgsList();
        list.add(value1);
        list.add(value2);
        return list;
    }

    private <T> List<T> arrayListOf(T value1, T value2) {
        return new ArrayList<>(Arrays.asList(value1, value2));
    }

}
