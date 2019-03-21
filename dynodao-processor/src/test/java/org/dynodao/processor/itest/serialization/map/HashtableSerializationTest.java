package org.dynodao.processor.itest.serialization.map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractSourceCompilingTest;
import org.junit.jupiter.api.Test;

import java.util.Hashtable;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class HashtableSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializeHashtableOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeHashtableOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    void serializeHashtableOfString_emptyMap_returnsAttributeValueWithEmptyMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeHashtableOfString(mapOf());
        assertThat(value).isEqualTo(new AttributeValue().withM(emptyMap()));
    }

    @Test
    void serializeHashtableOfString_singletonMapWithValue_returnsAttributeValueWithSingleValueSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeHashtableOfString(mapOf("key", "value"));
        assertThat(value).isEqualTo(new AttributeValue().withM(singletonMap("key", new AttributeValue("value"))));
    }

    @Test
    void serializeHashtableOfString_mapWithMultipleValues_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeHashtableOfString(mapOf("key1", "value1", "key2", "value2"));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue("value2"))));
    }

    @Test
    void deserializeHashtableOfString_null_returnsNull() {
        Hashtable<String, String> value = SchemaAttributeValueSerializer.deserializeHashtableOfString(null);
        assertThat(value).isNull();
    }

    @Test
    void deserializeHashtableOfString_nullAttributeValue_returnsNull() {
        Hashtable<String, String> value = SchemaAttributeValueSerializer.deserializeHashtableOfString(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    void deserializeHashtableOfString_mapValueNull_returnsNull() {
        Hashtable<String, String> value = SchemaAttributeValueSerializer.deserializeHashtableOfString(new AttributeValue().withS("string"));
        assertThat(value).isNull();
    }

    @Test
    void deserializeHashtableOfString_emptyMap_returnsEmptyHashtable() {
        Hashtable<String, String> value = SchemaAttributeValueSerializer.deserializeHashtableOfString(new AttributeValue().withM(emptyMap()));
        assertThat(value).isEmpty();
    }

    @Test
    void deserializeHashtableOfString_singletonMapWithValue_returnsSingletonHashtable() {
        Hashtable<String, String> value = SchemaAttributeValueSerializer.deserializeHashtableOfString(new AttributeValue().withM(singletonMap("key", new AttributeValue("value"))));
        assertThat(value).containsOnly(entry("key", "value"));
    }

    @Test
    void deserializeHashtableOfString_mapWithMultipleValues_returnsHashtableWithValues() {
        Hashtable<String, String> value = SchemaAttributeValueSerializer.deserializeHashtableOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue("value2"))));
        assertThat(value).containsOnly(entry("key1", "value1"), entry("key2", "value2"));
    }

    private <K, V> Hashtable<K, V> mapOf() {
        return new Hashtable<>();
    }

    private <K, V> Hashtable<K, V> mapOf(K key1, V value1) {
        Hashtable<K, V> map = new Hashtable<>();
        map.put(key1, value1);
        return map;
    }

    private <K, V> Hashtable<K, V> mapOf(K key1, V value1, K key2, V value2) {
        Hashtable<K, V> map = new Hashtable<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

}
