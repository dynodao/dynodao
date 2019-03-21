package org.dynodao.processor.itest.serialization.map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractSourceCompilingTest;
import org.junit.jupiter.api.Test;

import java.util.WeakHashMap;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class WeakHashMapSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializeWeakHashMapOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeWeakHashMapOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    void serializeWeakHashMapOfString_emptyMap_returnsAttributeValueWithEmptyMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeWeakHashMapOfString(mapOf());
        assertThat(value).isEqualTo(new AttributeValue().withM(emptyMap()));
    }

    @Test
    void serializeWeakHashMapOfString_singletonMapWithValue_returnsAttributeValueWithSingleValueSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeWeakHashMapOfString(mapOf("key", "value"));
        assertThat(value).isEqualTo(new AttributeValue().withM(singletonMap("key", new AttributeValue("value"))));
    }

    @Test
    void serializeWeakHashMapOfString_singletonMapWithNullValue_returnsAttributeValueWithSingleNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeWeakHashMapOfString(mapOf("key", null));
        assertThat(value).isEqualTo(new AttributeValue().withM(singletonMap("key", new AttributeValue().withNULL(true))));
    }

    @Test
    void serializeWeakHashMapOfString_mapWithMultipleValues_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeWeakHashMapOfString(mapOf("key1", "value1", "key2", "value2"));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue("value2"))));
    }

    @Test
    void serializeWeakHashMapOfString_mapWithMultipleValuesSomeNull_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeWeakHashMapOfString(mapOf("key1", "value1", "key2", null));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue().withNULL(true))));
    }

    @Test
    void serializeWeakHashMapOfString_mapWithMultipleValuesAllNull_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeWeakHashMapOfString(mapOf("key1", null, "key2", null));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue().withNULL(true),
                "key2", new AttributeValue().withNULL(true))));
    }

    @Test
    void deserializeWeakHashMapOfString_null_returnsNull() {
        WeakHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeWeakHashMapOfString(null);
        assertThat(value).isNull();
    }

    @Test
    void deserializeWeakHashMapOfString_nullAttributeValue_returnsNull() {
        WeakHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeWeakHashMapOfString(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    void deserializeWeakHashMapOfString_mapValueNull_returnsNull() {
        WeakHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeWeakHashMapOfString(new AttributeValue().withS("string"));
        assertThat(value).isNull();
    }

    @Test
    void deserializeWeakHashMapOfString_emptyMap_returnsEmptyWeakHashMap() {
        WeakHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeWeakHashMapOfString(new AttributeValue().withM(emptyMap()));
        assertThat(value).isEmpty();
    }

    @Test
    void deserializeWeakHashMapOfString_singletonMapWithValue_returnsSingletonWeakHashMap() {
        WeakHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeWeakHashMapOfString(new AttributeValue().withM(singletonMap("key", new AttributeValue("value"))));
        assertThat(value).containsOnly(entry("key", "value"));
    }

    @Test
    void deserializeWeakHashMapOfString_singletonMapWithNull_returnsSingletonWeakHashMap() {
        WeakHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeWeakHashMapOfString(new AttributeValue().withM(singletonMap("key", null)));
        assertThat(value).containsOnly(entry("key", null));
    }

    @Test
    void deserializeWeakHashMapOfString_singletonMapWithNullAttributeValue_returnsSingletonWeakHashMap() {
        WeakHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeWeakHashMapOfString(new AttributeValue().withM(singletonMap("key", new AttributeValue().withNULL(true))));
        assertThat(value).containsOnly(entry("key", null));
    }

    @Test
    void deserializeWeakHashMapOfString_mapWithMultipleValues_returnsWeakHashMapWithValues() {
        WeakHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeWeakHashMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue("value2"))));
        assertThat(value).containsOnly(entry("key1", "value1"), entry("key2", "value2"));
    }

    @Test
    void deserializeWeakHashMapOfString_mapWithMultipleValuesSomeNull_returnsWeakHashMapWithValueAndNull() {
        WeakHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeWeakHashMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", null)));
        assertThat(value).containsOnly(entry("key1", "value1"), entry("key2", null));
    }

    @Test
    void deserializeWeakHashMapOfString_mapWithMultipleValuesSomeNullAttributeValue_returnsWeakHashMapWithValueAndNull() {
        WeakHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeWeakHashMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue().withNULL(true))));
        assertThat(value).containsOnly(entry("key1", "value1"), entry("key2", null));
    }

    @Test
    void deserializeWeakHashMapOfString_mapWithMultipleValuesAllNull_returnsWeakHashMapAllNull() {
        WeakHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeWeakHashMapOfString(new AttributeValue().withM(mapOf(
                "key1", null,
                "key2", null)));
        assertThat(value).containsOnly(entry("key1", null), entry("key2", null));
    }

    @Test
    void deserializeWeakHashMapOfString_mapWithMultipleValuesAllNullAttributeValue_returnsWeakHashMapAllNull() {
        WeakHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeWeakHashMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue().withNULL(true),
                "key2", new AttributeValue().withNULL(true))));
        assertThat(value).containsOnly(entry("key1", null), entry("key2", null));
    }

    @Test
    void deserializeWeakHashMapOfString_mapWithMultipleValuesAllMixedNulls_returnsWeakHashMapAllNull() {
        WeakHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeWeakHashMapOfString(new AttributeValue().withM(mapOf(
                "key1", null,
                "key2", new AttributeValue().withNULL(true))));
        assertThat(value).containsOnly(entry("key1", null), entry("key2", null));
    }

    private <K, V> WeakHashMap<K, V> mapOf() {
        return new WeakHashMap<>();
    }

    private <K, V> WeakHashMap<K, V> mapOf(K key1, V value1) {
        WeakHashMap<K, V> map = new WeakHashMap<>();
        map.put(key1, value1);
        return map;
    }

    private <K, V> WeakHashMap<K, V> mapOf(K key1, V value1, K key2, V value2) {
        WeakHashMap<K, V> map = new WeakHashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

}
