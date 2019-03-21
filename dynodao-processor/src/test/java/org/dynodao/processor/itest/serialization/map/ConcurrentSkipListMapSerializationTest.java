package org.dynodao.processor.itest.serialization.map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractSourceCompilingTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentSkipListMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class ConcurrentSkipListMapSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializeConcurrentSkipListMapOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeConcurrentSkipListMapOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    void serializeConcurrentSkipListMapOfString_emptyConcurrentSkipListMap_returnsAttributeValueWithEmptyConcurrentSkipListMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeConcurrentSkipListMapOfString(mapOf());
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf()));
    }

    @Test
    void serializeConcurrentSkipListMapOfString_singletonConcurrentSkipListMapWithValue_returnsAttributeValueWithSingleValueSerializedConcurrentSkipListMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeConcurrentSkipListMapOfString(mapOf("key", "value"));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf("key", new AttributeValue("value"))));
    }

    @Test
    void serializeConcurrentSkipListMapOfString_mapWithMultipleValues_returnsAttributeValueWithSerializedConcurrentSkipListMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeConcurrentSkipListMapOfString(mapOf("key1", "value1", "key2", "value2"));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue("value2"))));
    }

    @Test
    void deserializeConcurrentSkipListMapOfString_null_returnsNull() {
        ConcurrentSkipListMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentSkipListMapOfString(null);
        assertThat(value).isNull();
    }

    @Test
    void deserializeConcurrentSkipListMapOfString_nullAttributeValue_returnsNull() {
        ConcurrentSkipListMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentSkipListMapOfString(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    void deserializeConcurrentSkipListMapOfString_mapValueNull_returnsNull() {
        ConcurrentSkipListMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentSkipListMapOfString(new AttributeValue().withS("string"));
        assertThat(value).isNull();
    }

    @Test
    void deserializeConcurrentSkipListMapOfString_emptyConcurrentSkipListMap_returnsEmptyConcurrentSkipListMap() {
        ConcurrentSkipListMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentSkipListMapOfString(new AttributeValue().withM(mapOf()));
        assertThat(value).isEmpty();
    }

    @Test
    void deserializeConcurrentSkipListMapOfString_singletonConcurrentSkipListMapWithValue_returnsSingletonConcurrentSkipListMap() {
        ConcurrentSkipListMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentSkipListMapOfString(new AttributeValue().withM(mapOf("key", new AttributeValue("value"))));
        assertThat(value).containsExactly(entry("key", "value"));
    }

    @Test
    void deserializeConcurrentSkipListMapOfString_mapWithMultipleValues_returnsConcurrentSkipListMapWithValues() {
        ConcurrentSkipListMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentSkipListMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue("value2"))));
        assertThat(value).containsExactly(entry("key1", "value1"), entry("key2", "value2"));
    }

    private <K extends Comparable<K>, V> ConcurrentSkipListMap<K, V> mapOf() {
        return new ConcurrentSkipListMap<>();
    }

    private <K extends Comparable<K>, V> ConcurrentSkipListMap<K, V> mapOf(K key, V value) {
        ConcurrentSkipListMap<K, V> map = new ConcurrentSkipListMap<>();
        map.put(key, value);
        return map;
    }

    private <K extends Comparable<K>, V> ConcurrentSkipListMap<K, V> mapOf(K key1, V value1, K key2, V value2) {
        ConcurrentSkipListMap<K, V> map = new ConcurrentSkipListMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

}
