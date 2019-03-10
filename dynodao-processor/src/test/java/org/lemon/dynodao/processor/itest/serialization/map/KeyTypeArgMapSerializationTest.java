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

class KeyTypeArgMapSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializeKeyTypeArgMapOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeKeyTypeArgMapOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    void serializeKeyTypeArgMapOfString_emptyMap_returnsAttributeValueWithEmptyMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeKeyTypeArgMapOfString(mapOf());
        assertThat(value).isEqualTo(new AttributeValue().withM(emptyMap()));
    }

    @Test
    void serializeKeyTypeArgMapOfString_singletonMapWithValue_returnsAttributeValueWithSingleValueSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeKeyTypeArgMapOfString(mapOf("key", "value"));
        assertThat(value).isEqualTo(new AttributeValue().withM(singletonMap("key", new AttributeValue("value"))));
    }

    @Test
    void serializeKeyTypeArgMapOfString_singletonMapWithNullValue_returnsAttributeValueWithSingleNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeKeyTypeArgMapOfString(mapOf("key", null));
        assertThat(value).isEqualTo(new AttributeValue().withM(singletonMap("key", new AttributeValue().withNULL(true))));
    }

    @Test
    void serializeKeyTypeArgMapOfString_mapWithMultipleValues_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeKeyTypeArgMapOfString(mapOf("key1", "value1", "key2", "value2"));
        assertThat(value).isEqualTo(new AttributeValue().withM(hashMapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue("value2"))));
    }

    @Test
    void serializeKeyTypeArgMapOfString_mapWithMultipleValuesSomeNull_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeKeyTypeArgMapOfString(mapOf("key1", "value1", "key2", null));
        assertThat(value).isEqualTo(new AttributeValue().withM(hashMapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue().withNULL(true))));
    }

    @Test
    void serializeKeyTypeArgMapOfString_mapWithMultipleValuesAllNull_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeKeyTypeArgMapOfString(mapOf("key1", null, "key2", null));
        assertThat(value).isEqualTo(new AttributeValue().withM(hashMapOf(
                "key1", new AttributeValue().withNULL(true),
                "key2", new AttributeValue().withNULL(true))));
    }

    @Test
    void deserializeKeyTypeArgMapOfString_null_returnsNull() {
        KeyTypeArgMap<String> value = SchemaAttributeValueSerializer.deserializeKeyTypeArgMapOfString(null);
        assertThat(value).isNull();
    }

    @Test
    void deserializeKeyTypeArgMapOfString_nullAttributeValue_returnsNull() {
        KeyTypeArgMap<String> value = SchemaAttributeValueSerializer.deserializeKeyTypeArgMapOfString(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    void deserializeKeyTypeArgMapOfString_mapValueNull_returnsNull() {
        KeyTypeArgMap<String> value = SchemaAttributeValueSerializer.deserializeKeyTypeArgMapOfString(new AttributeValue().withS("string"));
        assertThat(value).isNull();
    }

    @Test
    void deserializeKeyTypeArgMapOfString_emptyMap_returnsEmptyKeyTypeArgMap() {
        KeyTypeArgMap<String> value = SchemaAttributeValueSerializer.deserializeKeyTypeArgMapOfString(new AttributeValue().withM(emptyMap()));
        assertThat(value).isEmpty();
    }

    @Test
    void deserializeKeyTypeArgMapOfString_singletonMapWithValue_returnsSingletonKeyTypeArgMap() {
        KeyTypeArgMap<String> value = SchemaAttributeValueSerializer.deserializeKeyTypeArgMapOfString(new AttributeValue().withM(singletonMap("key", new AttributeValue("value"))));
        assertThat(value).containsOnly(entry("key", "value"));
    }

    @Test
    void deserializeKeyTypeArgMapOfString_singletonMapWithNull_returnsSingletonKeyTypeArgMap() {
        KeyTypeArgMap<String> value = SchemaAttributeValueSerializer.deserializeKeyTypeArgMapOfString(new AttributeValue().withM(singletonMap("key", null)));
        assertThat(value).containsOnly(entry("key", null));
    }

    @Test
    void deserializeKeyTypeArgMapOfString_singletonMapWithNullAttributeValue_returnsSingletonKeyTypeArgMap() {
        KeyTypeArgMap<String> value = SchemaAttributeValueSerializer.deserializeKeyTypeArgMapOfString(new AttributeValue().withM(singletonMap("key", new AttributeValue().withNULL(true))));
        assertThat(value).containsOnly(entry("key", null));
    }

    @Test
    void deserializeKeyTypeArgMapOfString_mapWithMultipleValues_returnsKeyTypeArgMapWithValues() {
        KeyTypeArgMap<String> value = SchemaAttributeValueSerializer.deserializeKeyTypeArgMapOfString(new AttributeValue().withM(hashMapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue("value2"))));
        assertThat(value).containsOnly(entry("key1", "value1"), entry("key2", "value2"));
    }

    @Test
    void deserializeKeyTypeArgMapOfString_mapWithMultipleValuesSomeNull_returnsKeyTypeArgMapWithValueAndNull() {
        KeyTypeArgMap<String> value = SchemaAttributeValueSerializer.deserializeKeyTypeArgMapOfString(new AttributeValue().withM(hashMapOf(
                "key1", new AttributeValue("value1"),
                "key2", null)));
        assertThat(value).containsOnly(entry("key1", "value1"), entry("key2", null));
    }

    @Test
    void deserializeKeyTypeArgMapOfString_mapWithMultipleValuesSomeNullAttributeValue_returnsKeyTypeArgMapWithValueAndNull() {
        KeyTypeArgMap<String> value = SchemaAttributeValueSerializer.deserializeKeyTypeArgMapOfString(new AttributeValue().withM(hashMapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue().withNULL(true))));
        assertThat(value).containsOnly(entry("key1", "value1"), entry("key2", null));
    }

    @Test
    void deserializeKeyTypeArgMapOfString_mapWithMultipleValuesAllNull_returnsKeyTypeArgMapAllNull() {
        KeyTypeArgMap<String> value = SchemaAttributeValueSerializer.deserializeKeyTypeArgMapOfString(new AttributeValue().withM(hashMapOf(
                "key1", null,
                "key2", null)));
        assertThat(value).containsOnly(entry("key1", null), entry("key2", null));
    }

    @Test
    void deserializeKeyTypeArgMapOfString_mapWithMultipleValuesAllNullAttributeValue_returnsKeyTypeArgMapAllNull() {
        KeyTypeArgMap<String> value = SchemaAttributeValueSerializer.deserializeKeyTypeArgMapOfString(new AttributeValue().withM(hashMapOf(
                "key1", new AttributeValue().withNULL(true),
                "key2", new AttributeValue().withNULL(true))));
        assertThat(value).containsOnly(entry("key1", null), entry("key2", null));
    }

    @Test
    void deserializeKeyTypeArgMapOfString_mapWithMultipleValuesAllMixedNulls_returnsKeyTypeArgMapAllNull() {
        KeyTypeArgMap<String> value = SchemaAttributeValueSerializer.deserializeKeyTypeArgMapOfString(new AttributeValue().withM(hashMapOf(
                "key1", null,
                "key2", new AttributeValue().withNULL(true))));
        assertThat(value).containsOnly(entry("key1", null), entry("key2", null));
    }

    private <K> KeyTypeArgMap<K> mapOf() {
        return new KeyTypeArgMap<>();
    }

    private <K> KeyTypeArgMap<K> mapOf(K key1, String value1) {
        KeyTypeArgMap<K> map = new KeyTypeArgMap<>();
        map.put(key1, value1);
        return map;
    }

    private <K> KeyTypeArgMap<K> mapOf(K key1, String value1, K key2, String value2) {
        KeyTypeArgMap<K> map = new KeyTypeArgMap<>();
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
