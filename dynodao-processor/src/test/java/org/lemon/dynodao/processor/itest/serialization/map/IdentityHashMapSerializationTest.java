package org.lemon.dynodao.processor.itest.serialization.map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.junit.jupiter.api.Test;
import org.lemon.dynodao.processor.itest.AbstractSourceCompilingTest;

import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class IdentityHashMapSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializeIdentityHashMapOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeIdentityHashMapOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    void serializeIdentityHashMapOfString_emptyMap_returnsAttributeValueWithEmptyMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeIdentityHashMapOfString(mapOf());
        assertThat(value).isEqualTo(new AttributeValue().withM(emptyMap()));
    }

    @Test
    void serializeIdentityHashMapOfString_singletonMapWithValue_returnsAttributeValueWithSingleValueSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeIdentityHashMapOfString(mapOf("key", "value"));
        assertThat(value).isEqualTo(new AttributeValue().withM(singletonMap("key", new AttributeValue("value"))));
    }

    @Test
    void serializeIdentityHashMapOfString_singletonMapWithNullValue_returnsAttributeValueWithSingleNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeIdentityHashMapOfString(mapOf("key", null));
        assertThat(value).isEqualTo(new AttributeValue().withM(singletonMap("key", new AttributeValue().withNULL(true))));
    }

    @Test
    void serializeIdentityHashMapOfString_mapWithMultipleValues_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeIdentityHashMapOfString(mapOf("key1", "value1", "key2", "value2"));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapWithProperEquals(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue("value2"))));
    }

    @Test
    void serializeIdentityHashMapOfString_mapWithMultipleValuesSomeNull_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeIdentityHashMapOfString(mapOf("key1", "value1", "key2", null));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapWithProperEquals(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue().withNULL(true))));
    }

    @Test
    void serializeIdentityHashMapOfString_mapWithMultipleValuesAllNull_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeIdentityHashMapOfString(mapOf("key1", null, "key2", null));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapWithProperEquals(
                "key1", new AttributeValue().withNULL(true),
                "key2", new AttributeValue().withNULL(true))));
    }

    @Test
    void deserializeIdentityHashMapOfString_null_returnsNull() {
        IdentityHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeIdentityHashMapOfString(null);
        assertThat(value).isNull();
    }

    @Test
    void deserializeIdentityHashMapOfString_nullAttributeValue_returnsNull() {
        IdentityHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeIdentityHashMapOfString(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    void deserializeIdentityHashMapOfString_mapValueNull_returnsNull() {
        IdentityHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeIdentityHashMapOfString(new AttributeValue().withS("string"));
        assertThat(value).isNull();
    }

    @Test
    void deserializeIdentityHashMapOfString_emptyMap_returnsEmptyIdentityHashMap() {
        IdentityHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeIdentityHashMapOfString(new AttributeValue().withM(emptyMap()));
        assertThat(value).isEmpty();
    }

    @Test
    void deserializeIdentityHashMapOfString_singletonMapWithValue_returnsSingletonIdentityHashMap() {
        IdentityHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeIdentityHashMapOfString(new AttributeValue().withM(singletonMap("key", new AttributeValue("value"))));
        assertThat(value).containsOnly(entry("key", "value"));
    }

    @Test
    void deserializeIdentityHashMapOfString_singletonMapWithNull_returnsSingletonIdentityHashMap() {
        IdentityHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeIdentityHashMapOfString(new AttributeValue().withM(singletonMap("key", null)));
        assertThat(value).containsOnly(entry("key", null));
    }

    @Test
    void deserializeIdentityHashMapOfString_singletonMapWithNullAttributeValue_returnsSingletonIdentityHashMap() {
        IdentityHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeIdentityHashMapOfString(new AttributeValue().withM(singletonMap("key", new AttributeValue().withNULL(true))));
        assertThat(value).containsOnly(entry("key", null));
    }

    @Test
    void deserializeIdentityHashMapOfString_mapWithMultipleValues_returnsIdentityHashMapWithValues() {
        IdentityHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeIdentityHashMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue("value2"))));
        assertThat(value).containsOnly(entry("key1", "value1"), entry("key2", "value2"));
    }

    @Test
    void deserializeIdentityHashMapOfString_mapWithMultipleValuesSomeNull_returnsIdentityHashMapWithValueAndNull() {
        IdentityHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeIdentityHashMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", null)));
        assertThat(value).containsOnly(entry("key1", "value1"), entry("key2", null));
    }

    @Test
    void deserializeIdentityHashMapOfString_mapWithMultipleValuesSomeNullAttributeValue_returnsIdentityHashMapWithValueAndNull() {
        IdentityHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeIdentityHashMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue().withNULL(true))));
        assertThat(value).containsOnly(entry("key1", "value1"), entry("key2", null));
    }

    @Test
    void deserializeIdentityHashMapOfString_mapWithMultipleValuesAllNull_returnsIdentityHashMapAllNull() {
        IdentityHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeIdentityHashMapOfString(new AttributeValue().withM(mapOf(
                "key1", null,
                "key2", null)));
        assertThat(value).containsOnly(entry("key1", null), entry("key2", null));
    }

    @Test
    void deserializeIdentityHashMapOfString_mapWithMultipleValuesAllNullAttributeValue_returnsIdentityHashMapAllNull() {
        IdentityHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeIdentityHashMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue().withNULL(true),
                "key2", new AttributeValue().withNULL(true))));
        assertThat(value).containsOnly(entry("key1", null), entry("key2", null));
    }

    @Test
    void deserializeIdentityHashMapOfString_mapWithMultipleValuesAllMixedNulls_returnsIdentityHashMapAllNull() {
        IdentityHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeIdentityHashMapOfString(new AttributeValue().withM(mapOf(
                "key1", null,
                "key2", new AttributeValue().withNULL(true))));
        assertThat(value).containsOnly(entry("key1", null), entry("key2", null));
    }

    private <K, V> IdentityHashMap<K, V> mapOf() {
        return new IdentityHashMap<>();
    }

    private <K, V> IdentityHashMap<K, V> mapOf(K key1, V value1) {
        IdentityHashMap<K, V> map = new IdentityHashMap<>();
        map.put(key1, value1);
        return map;
    }

    private <K, V> IdentityHashMap<K, V> mapOf(K key1, V value1, K key2, V value2) {
        IdentityHashMap<K, V> map = new IdentityHashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

    private <K, V> Map<K, V> mapWithProperEquals(K key1, V value1, K key2, V value2) {
        return new LinkedHashMap<>(mapOf(key1, value1, key2, value2));
    }

}
