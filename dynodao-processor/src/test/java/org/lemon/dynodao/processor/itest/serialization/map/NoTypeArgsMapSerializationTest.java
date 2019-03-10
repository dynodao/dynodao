package org.lemon.dynodao.processor.itest.serialization.map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.junit.jupiter.api.Test;
import org.lemon.dynodao.processor.itest.AbstractSourceCompilingTest;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class NoTypeArgsMapSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializeNoTypeArgsMap_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeNoTypeArgsMap(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    void serializeNoTypeArgsMap_emptyMap_returnsAttributeValueWithEmptyMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeNoTypeArgsMap(mapOf());
        assertThat(value).isEqualTo(new AttributeValue().withM(emptyMap()));
    }

    @Test
    void serializeNoTypeArgsMap_singletonMapWithValue_returnsAttributeValueWithSingleValueSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeNoTypeArgsMap(mapOf("key", "value"));
        assertThat(value).isEqualTo(new AttributeValue().withM(singletonMap("key", new AttributeValue("value"))));
    }

    @Test
    void serializeNoTypeArgsMap_singletonMapWithNullValue_returnsAttributeValueWithSingleNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeNoTypeArgsMap(mapOf("key", null));
        assertThat(value).isEqualTo(new AttributeValue().withM(singletonMap("key", new AttributeValue().withNULL(true))));
    }

    @Test
    void serializeNoTypeArgsMap_mapWithMultipleValues_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeNoTypeArgsMap(mapOf("key1", "value1", "key2", "value2"));
        assertThat(value).isEqualTo(new AttributeValue().withM(hashMapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue("value2"))));
    }

    @Test
    void serializeNoTypeArgsMap_mapWithMultipleValuesSomeNull_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeNoTypeArgsMap(mapOf("key1", "value1", "key2", null));
        assertThat(value).isEqualTo(new AttributeValue().withM(hashMapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue().withNULL(true))));
    }

    @Test
    void serializeNoTypeArgsMap_mapWithMultipleValuesAllNull_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeNoTypeArgsMap(mapOf("key1", null, "key2", null));
        assertThat(value).isEqualTo(new AttributeValue().withM(hashMapOf(
                "key1", new AttributeValue().withNULL(true),
                "key2", new AttributeValue().withNULL(true))));
    }

    @Test
    void deserializeNoTypeArgsMap_null_returnsNull() {
        NoTypeArgsMap value = SchemaAttributeValueSerializer.deserializeNoTypeArgsMap(null);
        assertThat(value).isNull();
    }

    @Test
    void deserializeNoTypeArgsMap_nullAttributeValue_returnsNull() {
        NoTypeArgsMap value = SchemaAttributeValueSerializer.deserializeNoTypeArgsMap(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    void deserializeNoTypeArgsMap_mapValueNull_returnsNull() {
        NoTypeArgsMap value = SchemaAttributeValueSerializer.deserializeNoTypeArgsMap(new AttributeValue().withS("string"));
        assertThat(value).isNull();
    }

    @Test
    void deserializeNoTypeArgsMap_emptyMap_returnsEmptyNoTypeArgsMap() {
        NoTypeArgsMap value = SchemaAttributeValueSerializer.deserializeNoTypeArgsMap(new AttributeValue().withM(emptyMap()));
        assertThat(value).isEmpty();
    }

    @Test
    void deserializeNoTypeArgsMap_singletonMapWithValue_returnsSingletonNoTypeArgsMap() {
        NoTypeArgsMap value = SchemaAttributeValueSerializer.deserializeNoTypeArgsMap(new AttributeValue().withM(singletonMap("key", new AttributeValue("value"))));
        assertThat(value).containsOnly(entry("key", "value"));
    }

    @Test
    void deserializeNoTypeArgsMap_singletonMapWithNull_returnsSingletonNoTypeArgsMap() {
        NoTypeArgsMap value = SchemaAttributeValueSerializer.deserializeNoTypeArgsMap(new AttributeValue().withM(singletonMap("key", null)));
        assertThat(value).containsOnly(entry("key", null));
    }

    @Test
    void deserializeNoTypeArgsMap_singletonMapWithNullAttributeValue_returnsSingletonNoTypeArgsMap() {
        NoTypeArgsMap value = SchemaAttributeValueSerializer.deserializeNoTypeArgsMap(new AttributeValue().withM(singletonMap("key", new AttributeValue().withNULL(true))));
        assertThat(value).containsOnly(entry("key", null));
    }

    @Test
    void deserializeNoTypeArgsMap_mapWithMultipleValues_returnsNoTypeArgsMapWithValues() {
        NoTypeArgsMap value = SchemaAttributeValueSerializer.deserializeNoTypeArgsMap(new AttributeValue().withM(hashMapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue("value2"))));
        assertThat(value).containsOnly(entry("key1", "value1"), entry("key2", "value2"));
    }

    @Test
    void deserializeNoTypeArgsMap_mapWithMultipleValuesSomeNull_returnsNoTypeArgsMapWithValueAndNull() {
        NoTypeArgsMap value = SchemaAttributeValueSerializer.deserializeNoTypeArgsMap(new AttributeValue().withM(hashMapOf(
                "key1", new AttributeValue("value1"),
                "key2", null)));
        assertThat(value).containsOnly(entry("key1", "value1"), entry("key2", null));
    }

    @Test
    void deserializeNoTypeArgsMap_mapWithMultipleValuesSomeNullAttributeValue_returnsNoTypeArgsMapWithValueAndNull() {
        NoTypeArgsMap value = SchemaAttributeValueSerializer.deserializeNoTypeArgsMap(new AttributeValue().withM(hashMapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue().withNULL(true))));
        assertThat(value).containsOnly(entry("key1", "value1"), entry("key2", null));
    }

    @Test
    void deserializeNoTypeArgsMap_mapWithMultipleValuesAllNull_returnsNoTypeArgsMapAllNull() {
        NoTypeArgsMap value = SchemaAttributeValueSerializer.deserializeNoTypeArgsMap(new AttributeValue().withM(hashMapOf(
                "key1", null,
                "key2", null)));
        assertThat(value).containsOnly(entry("key1", null), entry("key2", null));
    }

    @Test
    void deserializeNoTypeArgsMap_mapWithMultipleValuesAllNullAttributeValue_returnsNoTypeArgsMapAllNull() {
        NoTypeArgsMap value = SchemaAttributeValueSerializer.deserializeNoTypeArgsMap(new AttributeValue().withM(hashMapOf(
                "key1", new AttributeValue().withNULL(true),
                "key2", new AttributeValue().withNULL(true))));
        assertThat(value).containsOnly(entry("key1", null), entry("key2", null));
    }

    @Test
    void deserializeNoTypeArgsMap_mapWithMultipleValuesAllMixedNulls_returnsNoTypeArgsMapAllNull() {
        NoTypeArgsMap value = SchemaAttributeValueSerializer.deserializeNoTypeArgsMap(new AttributeValue().withM(hashMapOf(
                "key1", null,
                "key2", new AttributeValue().withNULL(true))));
        assertThat(value).containsOnly(entry("key1", null), entry("key2", null));
    }

    private NoTypeArgsMap mapOf() {
        return new NoTypeArgsMap();
    }

    private NoTypeArgsMap mapOf(String key1, String value1) {
        NoTypeArgsMap map = new NoTypeArgsMap();
        map.put(key1, value1);
        return map;
    }

    private NoTypeArgsMap mapOf(String key1, String value1, String key2, String value2) {
        NoTypeArgsMap map = new NoTypeArgsMap();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

    private <K, V> Map<K, V> hashMapOf(K key1, V value1, K key2, V value2) {
        Map<K, V> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

}
