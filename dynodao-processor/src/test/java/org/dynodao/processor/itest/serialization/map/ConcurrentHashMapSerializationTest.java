package org.dynodao.processor.itest.serialization.map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractSourceCompilingTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class ConcurrentHashMapSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializeConcurrentHashMapOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeConcurrentHashMapOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    void serializeConcurrentHashMapOfString_emptyConcurrentHashMap_returnsAttributeValueWithEmptyConcurrentHashMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeConcurrentHashMapOfString(mapOf());
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf()));
    }

    @Test
    void serializeConcurrentHashMapOfString_singletonConcurrentHashMapWithValue_returnsAttributeValueWithSingleValueSerializedConcurrentHashMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeConcurrentHashMapOfString(mapOf("key", "value"));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf("key", new AttributeValue("value"))));
    }

    @Test
    void serializeConcurrentHashMapOfString_mapWithMultipleValues_returnsAttributeValueWithSerializedConcurrentHashMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeConcurrentHashMapOfString(mapOf("key1", "value1", "key2", "value2"));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue("value2"))));
    }

    @Test
    void deserializeConcurrentHashMapOfString_null_returnsNull() {
        ConcurrentHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentHashMapOfString(null);
        assertThat(value).isNull();
    }

    @Test
    void deserializeConcurrentHashMapOfString_nullAttributeValue_returnsNull() {
        ConcurrentHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentHashMapOfString(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    void deserializeConcurrentHashMapOfString_mapValueNull_returnsNull() {
        ConcurrentHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentHashMapOfString(new AttributeValue().withS("string"));
        assertThat(value).isNull();
    }

    @Test
    void deserializeConcurrentHashMapOfString_emptyConcurrentHashMap_returnsEmptyConcurrentHashMap() {
        ConcurrentHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentHashMapOfString(new AttributeValue().withM(mapOf()));
        assertThat(value).isEmpty();
    }

    @Test
    void deserializeConcurrentHashMapOfString_singletonConcurrentHashMapWithValue_returnsSingletonConcurrentHashMap() {
        ConcurrentHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentHashMapOfString(new AttributeValue().withM(mapOf("key", new AttributeValue("value"))));
        assertThat(value).containsOnly(entry("key", "value"));
    }

    @Test
    void deserializeConcurrentHashMapOfString_mapWithMultipleValues_returnsConcurrentHashMapWithValues() {
        ConcurrentHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentHashMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue("value2"))));
        assertThat(value).containsOnly(entry("key1", "value1"), entry("key2", "value2"));
    }

    private <K, V> ConcurrentHashMap<K, V> mapOf() {
        return new ConcurrentHashMap<>();
    }

    private <K, V> ConcurrentHashMap<K, V> mapOf(K key, V value) {
        ConcurrentHashMap<K, V> map = new ConcurrentHashMap<>();
        map.put(key, value);
        return map;
    }

    private <K, V> ConcurrentHashMap<K, V> mapOf(K key1, V value1, K key2, V value2) {
        ConcurrentHashMap<K, V> map = new ConcurrentHashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

}
