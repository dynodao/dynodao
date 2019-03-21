package org.dynodao.processor.itest.serialization.map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractSourceCompilingTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class ConcurrentNavigableMapSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializeConcurrentNavigableMapOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeConcurrentNavigableMapOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    void serializeConcurrentNavigableMapOfString_emptyConcurrentNavigableMap_returnsAttributeValueWithEmptyConcurrentNavigableMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeConcurrentNavigableMapOfString(mapOf());
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf()));
    }

    @Test
    void serializeConcurrentNavigableMapOfString_singletonConcurrentNavigableMapWithValue_returnsAttributeValueWithSingleValueSerializedConcurrentNavigableMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeConcurrentNavigableMapOfString(mapOf("key", "value"));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf("key", new AttributeValue("value"))));
    }

    @Test
    void serializeConcurrentNavigableMapOfString_mapWithMultipleValues_returnsAttributeValueWithSerializedConcurrentNavigableMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeConcurrentNavigableMapOfString(mapOf("key1", "value1", "key2", "value2"));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue("value2"))));
    }

    @Test
    void deserializeConcurrentNavigableMapOfString_null_returnsNull() {
        ConcurrentNavigableMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentNavigableMapOfString(null);
        assertThat(value).isNull();
    }

    @Test
    void deserializeConcurrentNavigableMapOfString_nullAttributeValue_returnsNull() {
        ConcurrentNavigableMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentNavigableMapOfString(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    void deserializeConcurrentNavigableMapOfString_mapValueNull_returnsNull() {
        ConcurrentNavigableMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentNavigableMapOfString(new AttributeValue().withS("string"));
        assertThat(value).isNull();
    }

    @Test
    void deserializeConcurrentNavigableMapOfString_emptyConcurrentNavigableMap_returnsEmptyConcurrentSkipListMap() {
        ConcurrentNavigableMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentNavigableMapOfString(new AttributeValue().withM(mapOf()));
        assertThat(value)
                .isInstanceOf(ConcurrentSkipListMap.class)
                .isEmpty();
    }

    @Test
    void deserializeConcurrentNavigableMapOfString_singletonConcurrentNavigableMapWithValue_returnsSingletonConcurrentSkipListMap() {
        ConcurrentNavigableMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentNavigableMapOfString(new AttributeValue().withM(mapOf("key", new AttributeValue("value"))));
        assertThat(value)
                .isInstanceOf(ConcurrentSkipListMap.class)
                .containsExactly(entry("key", "value"));
    }

    @Test
    void deserializeConcurrentNavigableMapOfString_mapWithMultipleValues_returnsConcurrentSkipListMapWithValues() {
        ConcurrentNavigableMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentNavigableMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue("value2"))));
        assertThat(value)
                .isInstanceOf(ConcurrentSkipListMap.class)
                .containsExactly(entry("key1", "value1"), entry("key2", "value2"));
    }

    private <K extends Comparable<K>, V> ConcurrentNavigableMap<K, V> mapOf() {
        return new ConcurrentSkipListMap<>();
    }

    private <K extends Comparable<K>, V> ConcurrentNavigableMap<K, V> mapOf(K key, V value) {
        ConcurrentNavigableMap<K, V> map = new ConcurrentSkipListMap<>();
        map.put(key, value);
        return map;
    }

    private <K extends Comparable<K>, V> ConcurrentNavigableMap<K, V> mapOf(K key1, V value1, K key2, V value2) {
        ConcurrentNavigableMap<K, V> map = new ConcurrentSkipListMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

}
