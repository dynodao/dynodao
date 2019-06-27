package com.github.dynodao.processor.itest.serialization.map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.github.dynodao.processor.itest.AbstractIntegrationTest;
import com.github.dynodao.processor.test.params.AttributeValueSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class ConcurrentHashMapSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @Test
    void serializeConcurrentHashMapOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeConcurrentHashMapOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @MethodSource("concurrentHashMapsOfStringsSource")
    void serializeConcurrentHashMapOfString_mapCases_returnsMapAttributeValue(ConcurrentHashMap<String, String> concurrentHashMap) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeConcurrentHashMapOfString(concurrentHashMap);
        assertThat(value).isEqualTo(new AttributeValue().withM(concurrentHashMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue())))));
    }

    static Stream<ConcurrentHashMap<String, String>> concurrentHashMapsOfStringsSource() {
        return Stream.of(mapOf(), mapOf("key", "value"), mapOf("key1", "value1", "key2", "value2"));
    }

    @ParameterizedTest
    @NullSource
    @AttributeValueSource.WithoutMap
    void deserializeConcurrentHashMapOfString_nullCases_returnsNull(AttributeValue attributeValue) {
        ConcurrentHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentHashMapOfString(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @MethodSource("concurrentHashMapsOfStringsSource")
    void deserializeConcurrentHashMapOfString_correctTypesInMap_returnsConcurrentHashMap(ConcurrentHashMap<String, String> concurrentHashMap) {
        AttributeValue attributeValue = new AttributeValue().withM(concurrentHashMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue()))));
        ConcurrentHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentHashMapOfString(attributeValue);
        assertThat(value).isEqualTo(concurrentHashMap);
    }

    @ParameterizedTest
    @AttributeValueSource.WithoutString
    void deserializeConcurrentHashMapOfString_incorrectTypesInMap_returnsConcurrentHashMapWithoutItems(AttributeValue attributeValue) {
        ConcurrentHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentHashMapOfString(new AttributeValue().withM(mapOf("key", attributeValue)));
        assertThat(value).isEmpty();
    }

    @ParameterizedTest
    @AttributeValueSource.WithoutString
    void deserializeConcurrentHashMapOfString_incorrectTypesInMapMultipleItems_returnsConcurrentHashMapOnlyWithCorrectTypes(AttributeValue attributeValue) {
        ConcurrentHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentHashMapOfString(new AttributeValue().withM(
                mapOf("present", new AttributeValue("value"), "non-present", attributeValue)));
        assertThat(value).containsExactly(entry("present", "value"));
    }

    @ParameterizedTest
    @MethodSource("concurrentHashMapsOfStringsSource")
    void putAndGet_symmetricCases_returnsItem(ConcurrentHashMap<String, String> concurrentHashMap) {
        Schema schema = schema(concurrentHashMap);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));
        assertThat(items).containsExactly(schema);
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(ConcurrentHashMap<String, String> concurrentHashMap) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setConcurrentHashMap(concurrentHashMap);
        return schema;
    }

    private static <K, V> ConcurrentHashMap<K, V> mapOf() {
        return new ConcurrentHashMap<>();
    }

    private static <K, V> ConcurrentHashMap<K, V> mapOf(K key, V value) {
        ConcurrentHashMap<K, V> map = new ConcurrentHashMap<>();
        map.put(key, value);
        return map;
    }

    private static <K, V> ConcurrentHashMap<K, V> mapOf(K key1, V value1, K key2, V value2) {
        ConcurrentHashMap<K, V> map = new ConcurrentHashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

}
