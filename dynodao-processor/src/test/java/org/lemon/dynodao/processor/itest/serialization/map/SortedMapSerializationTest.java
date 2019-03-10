package org.lemon.dynodao.processor.itest.serialization.map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.junit.jupiter.api.Test;
import org.lemon.dynodao.processor.itest.AbstractSourceCompilingTest;

import java.util.SortedMap;
import java.util.TreeMap;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class SortedMapSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializeSortedMapOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeSortedMapOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    void serializeSortedMapOfString_emptyMap_returnsAttributeValueWithEmptyMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeSortedMapOfString(mapOf());
        assertThat(value).isEqualTo(new AttributeValue().withM(emptyMap()));
    }

    @Test
    void serializeSortedMapOfString_singletonMapWithValue_returnsAttributeValueWithSingleValueSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeSortedMapOfString(mapOf("key", "value"));
        assertThat(value).isEqualTo(new AttributeValue().withM(singletonMap("key", new AttributeValue("value"))));
    }

    @Test
    void serializeSortedMapOfString_singletonMapWithNullValue_returnsAttributeValueWithSingleNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeSortedMapOfString(mapOf("key", null));
        assertThat(value).isEqualTo(new AttributeValue().withM(singletonMap("key", new AttributeValue().withNULL(true))));
    }

    @Test
    void serializeSortedMapOfString_mapWithMultipleValues_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeSortedMapOfString(mapOf("key1", "value1", "key2", "value2"));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue("value2"))));
    }

    @Test
    void serializeSortedMapOfString_mapWithMultipleValuesSomeNull_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeSortedMapOfString(mapOf("key1", "value1", "key2", null));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue().withNULL(true))));
    }

    @Test
    void serializeSortedMapOfString_mapWithMultipleValuesAllNull_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeSortedMapOfString(mapOf("key1", null, "key2", null));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue().withNULL(true),
                "key2", new AttributeValue().withNULL(true))));
    }

    @Test
    void deserializeSortedMapOfString_null_returnsNull() {
        SortedMap<String, String> value = SchemaAttributeValueSerializer.deserializeSortedMapOfString(null);
        assertThat(value).isNull();
    }

    @Test
    void deserializeSortedMapOfString_nullAttributeValue_returnsNull() {
        SortedMap<String, String> value = SchemaAttributeValueSerializer.deserializeSortedMapOfString(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    void deserializeSortedMapOfString_mapValueNull_returnsNull() {
        SortedMap<String, String> value = SchemaAttributeValueSerializer.deserializeSortedMapOfString(new AttributeValue().withS("string"));
        assertThat(value).isNull();
    }

    @Test
    void deserializeSortedMapOfString_emptyMap_returnsEmptySortedMap() {
        SortedMap<String, String> value = SchemaAttributeValueSerializer.deserializeSortedMapOfString(new AttributeValue().withM(emptyMap()));
        assertThat(value)
                .isInstanceOf(TreeMap.class)
                .isEmpty();
    }

    @Test
    void deserializeSortedMapOfString_singletonMapWithValue_returnsSingletonSortedMap() {
        SortedMap<String, String> value = SchemaAttributeValueSerializer.deserializeSortedMapOfString(new AttributeValue().withM(singletonMap("key", new AttributeValue("value"))));
        assertThat(value)
                .isInstanceOf(TreeMap.class)
                .containsExactly(entry("key", "value"));
    }

    @Test
    void deserializeSortedMapOfString_singletonMapWithNull_returnsSingletonSortedMap() {
        SortedMap<String, String> value = SchemaAttributeValueSerializer.deserializeSortedMapOfString(new AttributeValue().withM(singletonMap("key", null)));
        assertThat(value)
                .isInstanceOf(TreeMap.class)
                .containsExactly(entry("key", null));
    }

    @Test
    void deserializeSortedMapOfString_singletonMapWithNullAttributeValue_returnsSingletonSortedMap() {
        SortedMap<String, String> value = SchemaAttributeValueSerializer.deserializeSortedMapOfString(new AttributeValue().withM(singletonMap("key", new AttributeValue().withNULL(true))));
        assertThat(value)
                .isInstanceOf(TreeMap.class)
                .containsExactly(entry("key", null));
    }

    @Test
    void deserializeSortedMapOfString_mapWithMultipleValues_returnsSortedMapWithValues() {
        SortedMap<String, String> value = SchemaAttributeValueSerializer.deserializeSortedMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue("value2"))));
        assertThat(value)
                .isInstanceOf(TreeMap.class)
                .containsExactly(entry("key1", "value1"), entry("key2", "value2"));
    }

    @Test
    void deserializeSortedMapOfString_mapWithMultipleValuesSomeNull_returnsSortedMapWithValueAndNull() {
        SortedMap<String, String> value = SchemaAttributeValueSerializer.deserializeSortedMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", null)));
        assertThat(value)
                .isInstanceOf(TreeMap.class)
                .containsExactly(entry("key1", "value1"), entry("key2", null));
    }

    @Test
    void deserializeSortedMapOfString_mapWithMultipleValuesSomeNullAttributeValue_returnsSortedMapWithValueAndNull() {
        SortedMap<String, String> value = SchemaAttributeValueSerializer.deserializeSortedMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue().withNULL(true))));
        assertThat(value)
                .isInstanceOf(TreeMap.class)
                .containsExactly(entry("key1", "value1"), entry("key2", null));
    }

    @Test
    void deserializeSortedMapOfString_mapWithMultipleValuesAllNull_returnsSortedMapAllNull() {
        SortedMap<String, String> value = SchemaAttributeValueSerializer.deserializeSortedMapOfString(new AttributeValue().withM(mapOf(
                "key1", null,
                "key2", null)));
        assertThat(value)
                .isInstanceOf(TreeMap.class)
                .containsExactly(entry("key1", null), entry("key2", null));
    }

    @Test
    void deserializeSortedMapOfString_mapWithMultipleValuesAllNullAttributeValue_returnsSortedMapAllNull() {
        SortedMap<String, String> value = SchemaAttributeValueSerializer.deserializeSortedMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue().withNULL(true),
                "key2", new AttributeValue().withNULL(true))));
        assertThat(value)
                .isInstanceOf(TreeMap.class)
                .containsExactly(entry("key1", null), entry("key2", null));
    }

    @Test
    void deserializeSortedMapOfString_mapWithMultipleValuesAllMixedNulls_returnsSortedMapAllNull() {
        SortedMap<String, String> value = SchemaAttributeValueSerializer.deserializeSortedMapOfString(new AttributeValue().withM(mapOf(
                "key1", null,
                "key2", new AttributeValue().withNULL(true))));
        assertThat(value)
                .isInstanceOf(TreeMap.class)
                .containsExactly(entry("key1", null), entry("key2", null));
    }

    private <K extends Comparable<K>, V> SortedMap<K, V> mapOf() {
        return new TreeMap<>();
    }

    private <K extends Comparable<K>, V> SortedMap<K, V> mapOf(K key1, V value1) {
        SortedMap<K, V> map = new TreeMap<>();
        map.put(key1, value1);
        return map;
    }

    private <K extends Comparable<K>, V> SortedMap<K, V> mapOf(K key1, V value1, K key2, V value2) {
        SortedMap<K, V> map = new TreeMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

}
