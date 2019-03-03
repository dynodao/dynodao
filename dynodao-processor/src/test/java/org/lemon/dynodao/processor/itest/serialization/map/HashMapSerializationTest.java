package org.lemon.dynodao.processor.itest.serialization.map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.junit.Test;
import org.lemon.dynodao.processor.itest.AbstractSourceCompilingTest;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class HashMapSerializationTest extends AbstractSourceCompilingTest {

    @Test
    public void serializeHashMapOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeHashMapOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    public void serializeHashMapOfString_emptyMap_returnsAttributeValueWithEmptyMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeHashMapOfString(mapOf());
        assertThat(value).isEqualTo(new AttributeValue().withM(emptyMap()));
    }

    @Test
    public void serializeHashMapOfString_singletonMapWithValue_returnsAttributeValueWithSingleValueSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeHashMapOfString(mapOf("key", "value"));
        assertThat(value).isEqualTo(new AttributeValue().withM(singletonMap("key", new AttributeValue("value"))));
    }

    @Test
    public void serializeHashMapOfString_singletonMapWithNullValue_returnsAttributeValueWithSingleNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeHashMapOfString(mapOf("key", null));
        assertThat(value).isEqualTo(new AttributeValue().withM(singletonMap("key", new AttributeValue().withNULL(true))));
    }

    @Test
    public void serializeHashMapOfString_mapWithMultipleValues_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeHashMapOfString(mapOf("key1", "value1", "key2", "value2"));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue("value2"))));
    }

    @Test
    public void serializeHashMapOfString_mapWithMultipleValuesSomeNull_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeHashMapOfString(mapOf("key1", "value1", "key2", null));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue().withNULL(true))));
    }

    @Test
    public void serializeHashMapOfString_mapWithMultipleValuesAllNull_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeHashMapOfString(mapOf("key1", null, "key2", null));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue().withNULL(true),
                "key2", new AttributeValue().withNULL(true))));
    }

    @Test
    public void deserializeHashMapOfString_null_returnsNull() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeHashMapOfString(null);
        assertThat(value).isNull();
    }

    @Test
    public void deserializeHashMapOfString_nullAttributeValue_returnsNull() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeHashMapOfString(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    public void deserializeHashMapOfString_mapValueNull_returnsNull() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeHashMapOfString(new AttributeValue().withS("string"));
        assertThat(value).isNull();
    }

    @Test
    public void deserializeHashMapOfString_emptyMap_returnsEmptyHashMap() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeHashMapOfString(new AttributeValue().withM(emptyMap()));
        assertThat(value)
                .isInstanceOf(HashMap.class)
                .isEmpty();
    }

    @Test
    public void deserializeHashMapOfString_singletonMapWithValue_returnsSingletonHashMap() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeHashMapOfString(new AttributeValue().withM(singletonMap("key", new AttributeValue("value"))));
        assertThat(value)
                .isInstanceOf(HashMap.class)
                .containsOnly(entry("key", "value"));
    }

    @Test
    public void deserializeHashMapOfString_singletonMapWithNull_returnsSingletonHashMap() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeHashMapOfString(new AttributeValue().withM(singletonMap("key", null)));
        assertThat(value)
                .isInstanceOf(HashMap.class)
                .containsOnly(entry("key", null));
    }

    @Test
    public void deserializeHashMapOfString_singletonMapWithNullAttributeValue_returnsSingletonHashMap() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeHashMapOfString(new AttributeValue().withM(singletonMap("key", new AttributeValue().withNULL(true))));
        assertThat(value)
                .isInstanceOf(HashMap.class)
                .containsOnly(entry("key", null));
    }

    @Test
    public void deserializeHashMapOfString_mapWithMultipleValues_returnsHashMapWithValues() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeHashMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue("value2"))));
        assertThat(value)
                .isInstanceOf(HashMap.class)
                .containsOnly(entry("key1", "value1"), entry("key2", "value2"));
    }

    @Test
    public void deserializeHashMapOfString_mapWithMultipleValuesSomeNull_returnsHashMapWithValueAndNull() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeHashMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", null)));
        assertThat(value)
                .isInstanceOf(HashMap.class)
                .containsOnly(entry("key1", "value1"), entry("key2", null));
    }

    @Test
    public void deserializeHashMapOfString_mapWithMultipleValuesSomeNullAttributeValue_returnsHashMapWithValueAndNull() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeHashMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue().withNULL(true))));
        assertThat(value)
                .isInstanceOf(HashMap.class)
                .containsOnly(entry("key1", "value1"), entry("key2", null));
    }

    @Test
    public void deserializeHashMapOfString_mapWithMultipleValuesAllNull_returnsHashMapAllNull() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeHashMapOfString(new AttributeValue().withM(mapOf(
                "key1", null,
                "key2", null)));
        assertThat(value)
                .isInstanceOf(HashMap.class)
                .containsOnly(entry("key1", null), entry("key2", null));
    }

    @Test
    public void deserializeHashMapOfString_mapWithMultipleValuesAllNullAttributeValue_returnsHashMapAllNull() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeHashMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue().withNULL(true),
                "key2", new AttributeValue().withNULL(true))));
        assertThat(value)
                .isInstanceOf(HashMap.class)
                .containsOnly(entry("key1", null), entry("key2", null));
    }

    @Test
    public void deserializeHashMapOfString_mapWithMultipleValuesAllMixedNulls_returnsHashMapAllNull() {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeHashMapOfString(new AttributeValue().withM(mapOf(
                "key1", null,
                "key2", new AttributeValue().withNULL(true))));
        assertThat(value)
                .isInstanceOf(HashMap.class)
                .containsOnly(entry("key1", null), entry("key2", null));
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
