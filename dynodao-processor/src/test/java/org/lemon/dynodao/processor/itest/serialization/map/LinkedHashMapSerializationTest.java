package org.lemon.dynodao.processor.itest.serialization.map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.junit.jupiter.api.Test;
import org.lemon.dynodao.processor.itest.AbstractSourceCompilingTest;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class LinkedHashMapSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializeLinkedHashMapOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeLinkedHashMapOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    void serializeLinkedHashMapOfString_emptyMap_returnsAttributeValueWithEmptyMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeLinkedHashMapOfString(mapOf());
        assertThat(value).isEqualTo(new AttributeValue().withM(emptyMap()));
    }

    @Test
    void serializeLinkedHashMapOfString_singletonMapWithValue_returnsAttributeValueWithSingleValueSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeLinkedHashMapOfString(mapOf("key", "value"));
        assertThat(value).isEqualTo(new AttributeValue().withM(singletonMap("key", new AttributeValue("value"))));
    }

    @Test
    void serializeLinkedHashMapOfString_singletonMapWithNullValue_returnsAttributeValueWithSingleNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeLinkedHashMapOfString(mapOf("key", null));
        assertThat(value).isEqualTo(new AttributeValue().withM(singletonMap("key", new AttributeValue().withNULL(true))));
    }

    @Test
    void serializeLinkedHashMapOfString_mapWithMultipleValues_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeLinkedHashMapOfString(mapOf("key1", "value1", "key2", "value2"));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue("value2"))));
    }

    @Test
    void serializeLinkedHashMapOfString_mapWithMultipleValuesSomeNull_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeLinkedHashMapOfString(mapOf("key1", "value1", "key2", null));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue().withNULL(true))));
    }

    @Test
    void serializeLinkedHashMapOfString_mapWithMultipleValuesAllNull_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeLinkedHashMapOfString(mapOf("key1", null, "key2", null));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue().withNULL(true),
                "key2", new AttributeValue().withNULL(true))));
    }

    @Test
    void deserializeLinkedHashMapOfString_null_returnsNull() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeLinkedHashMapOfString(null);
        assertThat(value).isNull();
    }

    @Test
    void deserializeLinkedHashMapOfString_nullAttributeValue_returnsNull() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeLinkedHashMapOfString(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    void deserializeLinkedHashMapOfString_mapValueNull_returnsNull() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeLinkedHashMapOfString(new AttributeValue().withS("string"));
        assertThat(value).isNull();
    }

    @Test
    void deserializeLinkedHashMapOfString_emptyMap_returnsEmptyLinkedHashMap() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeLinkedHashMapOfString(new AttributeValue().withM(emptyMap()));
        assertThat(value)
                .isInstanceOf(LinkedHashMap.class)
                .isEmpty();
    }

    @Test
    void deserializeLinkedHashMapOfString_singletonMapWithValue_returnsSingletonLinkedHashMap() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeLinkedHashMapOfString(new AttributeValue().withM(singletonMap("key", new AttributeValue("value"))));
        assertThat(value)
                .isInstanceOf(LinkedHashMap.class)
                .containsExactly(entry("key", "value"));
    }

    @Test
    void deserializeLinkedHashMapOfString_singletonMapWithNull_returnsSingletonLinkedHashMap() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeLinkedHashMapOfString(new AttributeValue().withM(singletonMap("key", null)));
        assertThat(value)
                .isInstanceOf(LinkedHashMap.class)
                .containsExactly(entry("key", null));
    }

    @Test
    void deserializeLinkedHashMapOfString_singletonMapWithNullAttributeValue_returnsSingletonLinkedHashMap() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeLinkedHashMapOfString(new AttributeValue().withM(singletonMap("key", new AttributeValue().withNULL(true))));
        assertThat(value)
                .isInstanceOf(LinkedHashMap.class)
                .containsExactly(entry("key", null));
    }

    @Test
    void deserializeLinkedHashMapOfString_mapWithMultipleValues_returnsLinkedHashMapWithValues() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeLinkedHashMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue("value2"))));
        assertThat(value)
                .isInstanceOf(LinkedHashMap.class)
                .containsExactly(entry("key1", "value1"), entry("key2", "value2"));
    }

    @Test
    void deserializeLinkedHashMapOfString_mapWithMultipleValuesSomeNull_returnsLinkedHashMapWithValueAndNull() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeLinkedHashMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", null)));
        assertThat(value)
                .isInstanceOf(LinkedHashMap.class)
                .containsExactly(entry("key1", "value1"), entry("key2", null));
    }

    @Test
    void deserializeLinkedHashMapOfString_mapWithMultipleValuesSomeNullAttributeValue_returnsLinkedHashMapWithValueAndNull() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeLinkedHashMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue().withNULL(true))));
        assertThat(value)
                .isInstanceOf(LinkedHashMap.class)
                .containsExactly(entry("key1", "value1"), entry("key2", null));
    }

    @Test
    void deserializeLinkedHashMapOfString_mapWithMultipleValuesAllNull_returnsLinkedHashMapAllNull() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeLinkedHashMapOfString(new AttributeValue().withM(mapOf(
                "key1", null,
                "key2", null)));
        assertThat(value)
                .isInstanceOf(LinkedHashMap.class)
                .containsExactly(entry("key1", null), entry("key2", null));
    }

    @Test
    void deserializeLinkedHashMapOfString_mapWithMultipleValuesAllNullAttributeValue_returnsLinkedHashMapAllNull() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeLinkedHashMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue().withNULL(true),
                "key2", new AttributeValue().withNULL(true))));
        assertThat(value)
                .isInstanceOf(LinkedHashMap.class)
                .containsExactly(entry("key1", null), entry("key2", null));
    }

    @Test
    void deserializeLinkedHashMapOfString_mapWithMultipleValuesAllMixedNulls_returnsLinkedHashMapAllNull() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeLinkedHashMapOfString(new AttributeValue().withM(mapOf(
                "key1", null,
                "key2", new AttributeValue().withNULL(true))));
        assertThat(value)
                .isInstanceOf(LinkedHashMap.class)
                .containsExactly(entry("key1", null), entry("key2", null));
    }

    private <K, V> LinkedHashMap<K, V> mapOf() {
        return new LinkedHashMap<>();
    }

    private <K, V> LinkedHashMap<K, V> mapOf(K key1, V value1) {
        LinkedHashMap<K, V> map = new LinkedHashMap<>();
        map.put(key1, value1);
        return map;
    }

    private <K, V> LinkedHashMap<K, V> mapOf(K key1, V value1, K key2, V value2) {
        LinkedHashMap<K, V> map = new LinkedHashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

}
