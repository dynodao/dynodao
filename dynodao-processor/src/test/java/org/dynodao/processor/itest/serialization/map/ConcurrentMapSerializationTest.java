package org.dynodao.processor.itest.serialization.map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractSourceCompilingTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class ConcurrentMapSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializeConcurrentMapOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeConcurrentMapOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    void serializeConcurrentMapOfString_emptyConcurrentMap_returnsAttributeValueWithEmptyConcurrentMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeConcurrentMapOfString(mapOf());
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf()));
    }

    @Test
    void serializeConcurrentMapOfString_singletonConcurrentMapWithValue_returnsAttributeValueWithSingleValueSerializedConcurrentMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeConcurrentMapOfString(mapOf("key", "value"));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf("key", new AttributeValue("value"))));
    }

    @Test
    void serializeConcurrentMapOfString_mapWithMultipleValues_returnsAttributeValueWithSerializedConcurrentMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeConcurrentMapOfString(mapOf("key1", "value1", "key2", "value2"));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue("value2"))));
    }

    @Test
    void deserializeConcurrentMapOfString_null_returnsNull() {
        ConcurrentMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentMapOfString(null);
        assertThat(value).isNull();
    }

    @Test
    void deserializeConcurrentMapOfString_nullAttributeValue_returnsNull() {
        ConcurrentMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentMapOfString(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    void deserializeConcurrentMapOfString_mapValueNull_returnsNull() {
        ConcurrentMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentMapOfString(new AttributeValue().withS("string"));
        assertThat(value).isNull();
    }

    @Test
    void deserializeConcurrentMapOfString_emptyConcurrentMap_returnsEmptyConcurrentHashMap() {
        ConcurrentMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentMapOfString(new AttributeValue().withM(mapOf()));
        assertThat(value)
                .isInstanceOf(ConcurrentHashMap.class)
                .isEmpty();
    }

    @Test
    void deserializeConcurrentMapOfString_singletonConcurrentMapWithValue_returnsSingletonConcurrentHashMap() {
        ConcurrentMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentMapOfString(new AttributeValue().withM(mapOf("key", new AttributeValue("value"))));
        assertThat(value)
                .isInstanceOf(ConcurrentHashMap.class)
                .containsOnly(entry("key", "value"));
    }

    @Test
    void deserializeConcurrentMapOfString_mapWithMultipleValues_returnsConcurrentHashMapWithValues() {
        ConcurrentMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue("value2"))));
        assertThat(value)
                .isInstanceOf(ConcurrentHashMap.class)
                .containsOnly(entry("key1", "value1"), entry("key2", "value2"));
    }

    private <K, V> ConcurrentMap<K, V> mapOf() {
        return new ConcurrentHashMap<>();
    }

    private <K, V> ConcurrentMap<K, V> mapOf(K key, V value) {
        ConcurrentMap<K, V> map = new ConcurrentHashMap<>();
        map.put(key, value);
        return map;
    }

    private <K, V> ConcurrentMap<K, V> mapOf(K key1, V value1, K key2, V value2) {
        ConcurrentMap<K, V> map = new ConcurrentHashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

}
