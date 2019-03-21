package org.dynodao.processor.itest.serialization.map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractSourceCompilingTest;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class MapSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializeMapOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeMapOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    void serializeMapOfString_emptyMap_returnsAttributeValueWithEmptyMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeMapOfString(emptyMap());
        assertThat(value).isEqualTo(new AttributeValue().withM(emptyMap()));
    }

    @Test
    void serializeMapOfString_singletonMapWithValue_returnsAttributeValueWithSingleValueSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeMapOfString(singletonMap("key", "value"));
        assertThat(value).isEqualTo(new AttributeValue().withM(singletonMap("key", new AttributeValue("value"))));
    }

    @Test
    void serializeMapOfString_singletonMapWithNullValue_returnsAttributeValueWithSingleNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeMapOfString(singletonMap("key", null));
        assertThat(value).isEqualTo(new AttributeValue().withM(singletonMap("key", new AttributeValue().withNULL(true))));
    }

    @Test
    void serializeMapOfString_mapWithMultipleValues_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeMapOfString(mapOf("key1", "value1", "key2", "value2"));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue("value2"))));
    }

    @Test
    void serializeMapOfString_mapWithMultipleValuesSomeNull_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeMapOfString(mapOf("key1", "value1", "key2", null));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue().withNULL(true))));
    }

    @Test
    void serializeMapOfString_mapWithMultipleValuesAllNull_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeMapOfString(mapOf("key1", null, "key2", null));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue().withNULL(true),
                "key2", new AttributeValue().withNULL(true))));
    }

    @Test
    void deserializeMapOfString_null_returnsNull() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeMapOfString(null);
        assertThat(value).isNull();
    }

    @Test
    void deserializeMapOfString_nullAttributeValue_returnsNull() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeMapOfString(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    void deserializeMapOfString_mapValueNull_returnsNull() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeMapOfString(new AttributeValue().withS("string"));
        assertThat(value).isNull();
    }

    @Test
    void deserializeMapOfString_emptyMap_returnsEmptyLinkedHashMap() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeMapOfString(new AttributeValue().withM(emptyMap()));
        assertThat(value)
                .isInstanceOf(LinkedHashMap.class)
                .isEmpty();
    }

    @Test
    void deserializeMapOfString_singletonMapWithValue_returnsSingletonLinkedHashMap() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeMapOfString(new AttributeValue().withM(singletonMap("key", new AttributeValue("value"))));
        assertThat(value)
                .isInstanceOf(LinkedHashMap.class)
                .containsExactly(entry("key", "value"));
    }

    @Test
    void deserializeMapOfString_singletonMapWithNull_returnsSingletonLinkedHashMap() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeMapOfString(new AttributeValue().withM(singletonMap("key", null)));
        assertThat(value)
                .isInstanceOf(LinkedHashMap.class)
                .containsExactly(entry("key", null));
    }

    @Test
    void deserializeMapOfString_singletonMapWithNullAttributeValue_returnsSingletonLinkedHashMap() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeMapOfString(new AttributeValue().withM(singletonMap("key", new AttributeValue().withNULL(true))));
        assertThat(value)
                .isInstanceOf(LinkedHashMap.class)
                .containsExactly(entry("key", null));
    }

    @Test
    void deserializeMapOfString_mapWithMultipleValues_returnsLinkedHashMapWithValues() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue("value2"))));
        assertThat(value)
                .isInstanceOf(LinkedHashMap.class)
                .containsExactly(entry("key1", "value1"), entry("key2", "value2"));
    }

    @Test
    void deserializeMapOfString_mapWithMultipleValuesSomeNull_returnsLinkedHashMapWithValueAndNull() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", null)));
        assertThat(value)
                .isInstanceOf(LinkedHashMap.class)
                .containsExactly(entry("key1", "value1"), entry("key2", null));
    }

    @Test
    void deserializeMapOfString_mapWithMultipleValuesSomeNullAttributeValue_returnsLinkedHashMapWithValueAndNull() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue().withNULL(true))));
        assertThat(value)
                .isInstanceOf(LinkedHashMap.class)
                .containsExactly(entry("key1", "value1"), entry("key2", null));
    }

    @Test
    void deserializeMapOfString_mapWithMultipleValuesAllNull_returnsLinkedHashMapAllNull() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeMapOfString(new AttributeValue().withM(mapOf(
                "key1", null,
                "key2", null)));
        assertThat(value)
                .isInstanceOf(LinkedHashMap.class)
                .containsExactly(entry("key1", null), entry("key2", null));
    }

    @Test
    void deserializeMapOfString_mapWithMultipleValuesAllNullAttributeValue_returnsLinkedHashMapAllNull() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue().withNULL(true),
                "key2", new AttributeValue().withNULL(true))));
        assertThat(value)
                .isInstanceOf(LinkedHashMap.class)
                .containsExactly(entry("key1", null), entry("key2", null));
    }

    @Test
    void deserializeMapOfString_mapWithMultipleValuesAllMixedNulls_returnsLinkedHashMapAllNull() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeMapOfString(new AttributeValue().withM(mapOf(
                "key1", null,
                "key2", new AttributeValue().withNULL(true))));
        assertThat(value)
                .isInstanceOf(LinkedHashMap.class)
                .containsExactly(entry("key1", null), entry("key2", null));
    }

    private <K, V> Map<K, V> mapOf(K key1, V value1, K key2, V value2) {
        Map<K, V> map = new LinkedHashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

}
