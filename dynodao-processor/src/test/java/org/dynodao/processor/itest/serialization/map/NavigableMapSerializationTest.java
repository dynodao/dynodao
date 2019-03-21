package org.dynodao.processor.itest.serialization.map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractSourceCompilingTest;
import org.junit.jupiter.api.Test;

import java.util.NavigableMap;
import java.util.TreeMap;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class NavigableMapSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializeNavigableMapOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeNavigableMapOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    void serializeNavigableMapOfString_emptyMap_returnsAttributeValueWithEmptyMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeNavigableMapOfString(mapOf());
        assertThat(value).isEqualTo(new AttributeValue().withM(emptyMap()));
    }

    @Test
    void serializeNavigableMapOfString_singletonMapWithValue_returnsAttributeValueWithSingleValueSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeNavigableMapOfString(mapOf("key", "value"));
        assertThat(value).isEqualTo(new AttributeValue().withM(singletonMap("key", new AttributeValue("value"))));
    }

    @Test
    void serializeNavigableMapOfString_singletonMapWithNullValue_returnsAttributeValueWithSingleNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeNavigableMapOfString(mapOf("key", null));
        assertThat(value).isEqualTo(new AttributeValue().withM(singletonMap("key", new AttributeValue().withNULL(true))));
    }

    @Test
    void serializeNavigableMapOfString_mapWithMultipleValues_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeNavigableMapOfString(mapOf("key1", "value1", "key2", "value2"));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue("value2"))));
    }

    @Test
    void serializeNavigableMapOfString_mapWithMultipleValuesSomeNull_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeNavigableMapOfString(mapOf("key1", "value1", "key2", null));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue().withNULL(true))));
    }

    @Test
    void serializeNavigableMapOfString_mapWithMultipleValuesAllNull_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeNavigableMapOfString(mapOf("key1", null, "key2", null));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue().withNULL(true),
                "key2", new AttributeValue().withNULL(true))));
    }

    @Test
    void deserializeNavigableMapOfString_null_returnsNull() {
        NavigableMap<String, String> value = SchemaAttributeValueSerializer.deserializeNavigableMapOfString(null);
        assertThat(value).isNull();
    }

    @Test
    void deserializeNavigableMapOfString_nullAttributeValue_returnsNull() {
        NavigableMap<String, String> value = SchemaAttributeValueSerializer.deserializeNavigableMapOfString(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    void deserializeNavigableMapOfString_mapValueNull_returnsNull() {
        NavigableMap<String, String> value = SchemaAttributeValueSerializer.deserializeNavigableMapOfString(new AttributeValue().withS("string"));
        assertThat(value).isNull();
    }

    @Test
    void deserializeNavigableMapOfString_emptyMap_returnsEmptyNavigableMap() {
        NavigableMap<String, String> value = SchemaAttributeValueSerializer.deserializeNavigableMapOfString(new AttributeValue().withM(emptyMap()));
        assertThat(value)
                .isInstanceOf(TreeMap.class)
                .isEmpty();
    }

    @Test
    void deserializeNavigableMapOfString_singletonMapWithValue_returnsSingletonNavigableMap() {
        NavigableMap<String, String> value = SchemaAttributeValueSerializer.deserializeNavigableMapOfString(new AttributeValue().withM(singletonMap("key", new AttributeValue("value"))));
        assertThat(value)
                .isInstanceOf(TreeMap.class)
                .containsExactly(entry("key", "value"));
    }

    @Test
    void deserializeNavigableMapOfString_singletonMapWithNull_returnsSingletonNavigableMap() {
        NavigableMap<String, String> value = SchemaAttributeValueSerializer.deserializeNavigableMapOfString(new AttributeValue().withM(singletonMap("key", null)));
        assertThat(value)
                .isInstanceOf(TreeMap.class)
                .containsExactly(entry("key", null));
    }

    @Test
    void deserializeNavigableMapOfString_singletonMapWithNullAttributeValue_returnsSingletonNavigableMap() {
        NavigableMap<String, String> value = SchemaAttributeValueSerializer.deserializeNavigableMapOfString(new AttributeValue().withM(singletonMap("key", new AttributeValue().withNULL(true))));
        assertThat(value)
                .isInstanceOf(TreeMap.class)
                .containsExactly(entry("key", null));
    }

    @Test
    void deserializeNavigableMapOfString_mapWithMultipleValues_returnsNavigableMapWithValues() {
        NavigableMap<String, String> value = SchemaAttributeValueSerializer.deserializeNavigableMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue("value2"))));
        assertThat(value)
                .isInstanceOf(TreeMap.class)
                .containsExactly(entry("key1", "value1"), entry("key2", "value2"));
    }

    @Test
    void deserializeNavigableMapOfString_mapWithMultipleValuesSomeNull_returnsNavigableMapWithValueAndNull() {
        NavigableMap<String, String> value = SchemaAttributeValueSerializer.deserializeNavigableMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", null)));
        assertThat(value)
                .isInstanceOf(TreeMap.class)
                .containsExactly(entry("key1", "value1"), entry("key2", null));
    }

    @Test
    void deserializeNavigableMapOfString_mapWithMultipleValuesSomeNullAttributeValue_returnsNavigableMapWithValueAndNull() {
        NavigableMap<String, String> value = SchemaAttributeValueSerializer.deserializeNavigableMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue().withNULL(true))));
        assertThat(value)
                .isInstanceOf(TreeMap.class)
                .containsExactly(entry("key1", "value1"), entry("key2", null));
    }

    @Test
    void deserializeNavigableMapOfString_mapWithMultipleValuesAllNull_returnsNavigableMapAllNull() {
        NavigableMap<String, String> value = SchemaAttributeValueSerializer.deserializeNavigableMapOfString(new AttributeValue().withM(mapOf(
                "key1", null,
                "key2", null)));
        assertThat(value)
                .isInstanceOf(TreeMap.class)
                .containsExactly(entry("key1", null), entry("key2", null));
    }

    @Test
    void deserializeNavigableMapOfString_mapWithMultipleValuesAllNullAttributeValue_returnsNavigableMapAllNull() {
        NavigableMap<String, String> value = SchemaAttributeValueSerializer.deserializeNavigableMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue().withNULL(true),
                "key2", new AttributeValue().withNULL(true))));
        assertThat(value)
                .isInstanceOf(TreeMap.class)
                .containsExactly(entry("key1", null), entry("key2", null));
    }

    @Test
    void deserializeNavigableMapOfString_mapWithMultipleValuesAllMixedNulls_returnsNavigableMapAllNull() {
        NavigableMap<String, String> value = SchemaAttributeValueSerializer.deserializeNavigableMapOfString(new AttributeValue().withM(mapOf(
                "key1", null,
                "key2", new AttributeValue().withNULL(true))));
        assertThat(value)
                .isInstanceOf(TreeMap.class)
                .containsExactly(entry("key1", null), entry("key2", null));
    }

    private <K extends Comparable<K>, V> NavigableMap<K, V> mapOf() {
        return new TreeMap<>();
    }

    private <K extends Comparable<K>, V> NavigableMap<K, V> mapOf(K key1, V value1) {
        NavigableMap<K, V> map = new TreeMap<>();
        map.put(key1, value1);
        return map;
    }

    private <K extends Comparable<K>, V> NavigableMap<K, V> mapOf(K key1, V value1, K key2, V value2) {
        NavigableMap<K, V> map = new TreeMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

}
