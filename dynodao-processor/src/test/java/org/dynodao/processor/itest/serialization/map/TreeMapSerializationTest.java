package org.dynodao.processor.itest.serialization.map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractSourceCompilingTest;
import org.junit.jupiter.api.Test;

import java.util.TreeMap;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class TreeMapSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializeTreeMapOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeTreeMapOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    void serializeTreeMapOfString_emptyMap_returnsAttributeValueWithEmptyMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeTreeMapOfString(mapOf());
        assertThat(value).isEqualTo(new AttributeValue().withM(emptyMap()));
    }

    @Test
    void serializeTreeMapOfString_singletonMapWithValue_returnsAttributeValueWithSingleValueSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeTreeMapOfString(mapOf("key", "value"));
        assertThat(value).isEqualTo(new AttributeValue().withM(singletonMap("key", new AttributeValue("value"))));
    }

    @Test
    void serializeTreeMapOfString_singletonMapWithNullValue_returnsAttributeValueWithSingleNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeTreeMapOfString(mapOf("key", null));
        assertThat(value).isEqualTo(new AttributeValue().withM(singletonMap("key", new AttributeValue().withNULL(true))));
    }

    @Test
    void serializeTreeMapOfString_mapWithMultipleValues_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeTreeMapOfString(mapOf("key1", "value1", "key2", "value2"));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue("value2"))));
    }

    @Test
    void serializeTreeMapOfString_mapWithMultipleValuesSomeNull_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeTreeMapOfString(mapOf("key1", "value1", "key2", null));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue().withNULL(true))));
    }

    @Test
    void serializeTreeMapOfString_mapWithMultipleValuesAllNull_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeTreeMapOfString(mapOf("key1", null, "key2", null));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue().withNULL(true),
                "key2", new AttributeValue().withNULL(true))));
    }

    @Test
    void deserializeTreeMapOfString_null_returnsNull() {
        TreeMap<String, String> value = SchemaAttributeValueSerializer.deserializeTreeMapOfString(null);
        assertThat(value).isNull();
    }

    @Test
    void deserializeTreeMapOfString_nullAttributeValue_returnsNull() {
        TreeMap<String, String> value = SchemaAttributeValueSerializer.deserializeTreeMapOfString(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    void deserializeTreeMapOfString_mapValueNull_returnsNull() {
        TreeMap<String, String> value = SchemaAttributeValueSerializer.deserializeTreeMapOfString(new AttributeValue().withS("string"));
        assertThat(value).isNull();
    }

    @Test
    void deserializeTreeMapOfString_emptyMap_returnsEmptyTreeMap() {
        TreeMap<String, String> value = SchemaAttributeValueSerializer.deserializeTreeMapOfString(new AttributeValue().withM(emptyMap()));
        assertThat(value).isEmpty();
    }

    @Test
    void deserializeTreeMapOfString_singletonMapWithValue_returnsSingletonTreeMap() {
        TreeMap<String, String> value = SchemaAttributeValueSerializer.deserializeTreeMapOfString(new AttributeValue().withM(singletonMap("key", new AttributeValue("value"))));
        assertThat(value).containsExactly(entry("key", "value"));
    }

    @Test
    void deserializeTreeMapOfString_singletonMapWithNull_returnsSingletonTreeMap() {
        TreeMap<String, String> value = SchemaAttributeValueSerializer.deserializeTreeMapOfString(new AttributeValue().withM(singletonMap("key", null)));
        assertThat(value).containsExactly(entry("key", null));
    }

    @Test
    void deserializeTreeMapOfString_singletonMapWithNullAttributeValue_returnsSingletonTreeMap() {
        TreeMap<String, String> value = SchemaAttributeValueSerializer.deserializeTreeMapOfString(new AttributeValue().withM(singletonMap("key", new AttributeValue().withNULL(true))));
        assertThat(value).containsExactly(entry("key", null));
    }

    @Test
    void deserializeTreeMapOfString_mapWithMultipleValues_returnsTreeMapWithValues() {
        TreeMap<String, String> value = SchemaAttributeValueSerializer.deserializeTreeMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue("value2"))));
        assertThat(value).containsExactly(entry("key1", "value1"), entry("key2", "value2"));
    }

    @Test
    void deserializeTreeMapOfString_mapWithMultipleValuesSomeNull_returnsTreeMapWithValueAndNull() {
        TreeMap<String, String> value = SchemaAttributeValueSerializer.deserializeTreeMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", null)));
        assertThat(value).containsExactly(entry("key1", "value1"), entry("key2", null));
    }

    @Test
    void deserializeTreeMapOfString_mapWithMultipleValuesSomeNullAttributeValue_returnsTreeMapWithValueAndNull() {
        TreeMap<String, String> value = SchemaAttributeValueSerializer.deserializeTreeMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue().withNULL(true))));
        assertThat(value).containsExactly(entry("key1", "value1"), entry("key2", null));
    }

    @Test
    void deserializeTreeMapOfString_mapWithMultipleValuesAllNull_returnsTreeMapAllNull() {
        TreeMap<String, String> value = SchemaAttributeValueSerializer.deserializeTreeMapOfString(new AttributeValue().withM(mapOf(
                "key1", null,
                "key2", null)));
        assertThat(value).containsExactly(entry("key1", null), entry("key2", null));
    }

    @Test
    void deserializeTreeMapOfString_mapWithMultipleValuesAllNullAttributeValue_returnsTreeMapAllNull() {
        TreeMap<String, String> value = SchemaAttributeValueSerializer.deserializeTreeMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue().withNULL(true),
                "key2", new AttributeValue().withNULL(true))));
        assertThat(value).containsExactly(entry("key1", null), entry("key2", null));
    }

    @Test
    void deserializeTreeMapOfString_mapWithMultipleValuesAllMixedNulls_returnsTreeMapAllNull() {
        TreeMap<String, String> value = SchemaAttributeValueSerializer.deserializeTreeMapOfString(new AttributeValue().withM(mapOf(
                "key1", null,
                "key2", new AttributeValue().withNULL(true))));
        assertThat(value).containsExactly(entry("key1", null), entry("key2", null));
    }

    private <K extends Comparable<K>, V> TreeMap<K, V> mapOf() {
        return new TreeMap<>();
    }

    private <K extends Comparable<K>, V> TreeMap<K, V> mapOf(K key1, V value1) {
        TreeMap<K, V> map = new TreeMap<>();
        map.put(key1, value1);
        return map;
    }

    private <K extends Comparable<K>, V> TreeMap<K, V> mapOf(K key1, V value1, K key2, V value2) {
        TreeMap<K, V> map = new TreeMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

}
