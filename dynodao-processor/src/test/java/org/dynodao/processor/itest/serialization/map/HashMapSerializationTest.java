package org.dynodao.processor.itest.serialization.map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractSourceCompilingTest;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class HashMapSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializeHashMapOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeHashMapOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    void serializeHashMapOfString_emptyMap_returnsAttributeValueWithEmptyMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeHashMapOfString(mapOf());
        assertThat(value).isEqualTo(new AttributeValue().withM(emptyMap()));
    }

    @Test
    void serializeHashMapOfString_singletonMapWithValue_returnsAttributeValueWithSingleValueSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeHashMapOfString(mapOf("key", "value"));
        assertThat(value).isEqualTo(new AttributeValue().withM(singletonMap("key", new AttributeValue("value"))));
    }

    @Test
    void serializeHashMapOfString_singletonMapWithNullValue_returnsAttributeValueWithSingleNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeHashMapOfString(mapOf("key", null));
        assertThat(value).isEqualTo(new AttributeValue().withM(singletonMap("key", new AttributeValue().withNULL(true))));
    }

    @Test
    void serializeHashMapOfString_mapWithMultipleValues_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeHashMapOfString(mapOf("key1", "value1", "key2", "value2"));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue("value2"))));
    }

    @Test
    void serializeHashMapOfString_mapWithMultipleValuesSomeNull_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeHashMapOfString(mapOf("key1", "value1", "key2", null));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue().withNULL(true))));
    }

    @Test
    void serializeHashMapOfString_mapWithMultipleValuesAllNull_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeHashMapOfString(mapOf("key1", null, "key2", null));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue().withNULL(true),
                "key2", new AttributeValue().withNULL(true))));
    }

    @Test
    void deserializeHashMapOfString_null_returnsNull() {
        HashMap<String, String> value = SchemaAttributeValueSerializer.deserializeHashMapOfString(null);
        assertThat(value).isNull();
    }

    @Test
    void deserializeHashMapOfString_nullAttributeValue_returnsNull() {
        HashMap<String, String> value = SchemaAttributeValueSerializer.deserializeHashMapOfString(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    void deserializeHashMapOfString_mapValueNull_returnsNull() {
        HashMap<String, String> value = SchemaAttributeValueSerializer.deserializeHashMapOfString(new AttributeValue().withS("string"));
        assertThat(value).isNull();
    }

    @Test
    void deserializeHashMapOfString_emptyMap_returnsEmptyHashMap() {
        HashMap<String, String> value = SchemaAttributeValueSerializer.deserializeHashMapOfString(new AttributeValue().withM(emptyMap()));
        assertThat(value).isEmpty();
    }

    @Test
    void deserializeHashMapOfString_singletonMapWithValue_returnsSingletonHashMap() {
        HashMap<String, String> value = SchemaAttributeValueSerializer.deserializeHashMapOfString(new AttributeValue().withM(singletonMap("key", new AttributeValue("value"))));
        assertThat(value).containsOnly(entry("key", "value"));
    }

    @Test
    void deserializeHashMapOfString_singletonMapWithNull_returnsSingletonHashMap() {
        HashMap<String, String> value = SchemaAttributeValueSerializer.deserializeHashMapOfString(new AttributeValue().withM(singletonMap("key", null)));
        assertThat(value).containsOnly(entry("key", null));
    }

    @Test
    void deserializeHashMapOfString_singletonMapWithNullAttributeValue_returnsSingletonHashMap() {
        HashMap<String, String> value = SchemaAttributeValueSerializer.deserializeHashMapOfString(new AttributeValue().withM(singletonMap("key", new AttributeValue().withNULL(true))));
        assertThat(value).containsOnly(entry("key", null));
    }

    @Test
    void deserializeHashMapOfString_mapWithMultipleValues_returnsHashMapWithValues() {
        HashMap<String, String> value = SchemaAttributeValueSerializer.deserializeHashMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue("value2"))));
        assertThat(value).containsOnly(entry("key1", "value1"), entry("key2", "value2"));
    }

    @Test
    void deserializeHashMapOfString_mapWithMultipleValuesSomeNull_returnsHashMapWithValueAndNull() {
        HashMap<String, String> value = SchemaAttributeValueSerializer.deserializeHashMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", null)));
        assertThat(value).containsOnly(entry("key1", "value1"), entry("key2", null));
    }

    @Test
    void deserializeHashMapOfString_mapWithMultipleValuesSomeNullAttributeValue_returnsHashMapWithValueAndNull() {
        HashMap<String, String> value = SchemaAttributeValueSerializer.deserializeHashMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue().withNULL(true))));
        assertThat(value).containsOnly(entry("key1", "value1"), entry("key2", null));
    }

    @Test
    void deserializeHashMapOfString_mapWithMultipleValuesAllNull_returnsHashMapAllNull() {
        HashMap<String, String> value = SchemaAttributeValueSerializer.deserializeHashMapOfString(new AttributeValue().withM(mapOf(
                "key1", null,
                "key2", null)));
        assertThat(value).containsOnly(entry("key1", null), entry("key2", null));
    }

    @Test
    void deserializeHashMapOfString_mapWithMultipleValuesAllNullAttributeValue_returnsHashMapAllNull() {
        HashMap<String, String> value = SchemaAttributeValueSerializer.deserializeHashMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue().withNULL(true),
                "key2", new AttributeValue().withNULL(true))));
        assertThat(value).containsOnly(entry("key1", null), entry("key2", null));
    }

    @Test
    void deserializeHashMapOfString_mapWithMultipleValuesAllMixedNulls_returnsHashMapAllNull() {
        HashMap<String, String> value = SchemaAttributeValueSerializer.deserializeHashMapOfString(new AttributeValue().withM(mapOf(
                "key1", null,
                "key2", new AttributeValue().withNULL(true))));
        assertThat(value).containsOnly(entry("key1", null), entry("key2", null));
    }

    private <K, V> HashMap<K, V> mapOf() {
        return new HashMap<>();
    }

    private <K, V> HashMap<K, V> mapOf(K key1, V value1) {
        HashMap<K, V> map = new HashMap<>();
        map.put(key1, value1);
        return map;
    }

    private <K, V> HashMap<K, V> mapOf(K key1, V value1, K key2, V value2) {
        HashMap<K, V> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

}
