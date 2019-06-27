package com.github.dynodao.processor.itest.serialization.map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.github.dynodao.processor.itest.AbstractIntegrationTest;
import com.github.dynodao.processor.test.params.AttributeValueSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;

class ConcurrentSkipListMapSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @Test
    void serializeConcurrentSkipListMapOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeConcurrentSkipListMapOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @MethodSource("concurrentSkipListMapOfStringsSource")
    void serializeConcurrentSkipListMapOfString_mapCases_returnsMapAttributeValue(ConcurrentSkipListMap<String, String> concurrentNavigableMap) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeConcurrentSkipListMapOfString(concurrentNavigableMap);
        assertThat(value).isEqualTo(new AttributeValue().withM(concurrentNavigableMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue())))));
    }

    static Stream<ConcurrentSkipListMap<String, String>> concurrentSkipListMapOfStringsSource() {
        return Stream.of(mapOf(), mapOf("key", "value"), mapOf("key1", "value1", "key2", "value2"));
    }

    @ParameterizedTest
    @NullSource
    @AttributeValueSource.WithoutMap
    void deserializeConcurrentSkipListMapOfString_nullCases_returnsNull(AttributeValue attributeValue) {
        ConcurrentSkipListMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentSkipListMapOfString(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @MethodSource("concurrentSkipListMapOfStringsSource")
    void deserializeConcurrentSkipListMapOfString_correctTypesInMap_returnsConcurrentSkipListMap(ConcurrentSkipListMap<String, String> concurrentSkipListMap) {
        AttributeValue attributeValue = new AttributeValue().withM(concurrentSkipListMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue()))));
        ConcurrentSkipListMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentSkipListMapOfString(attributeValue);
        assertThat(value).isEqualTo(concurrentSkipListMap);
    }

    @ParameterizedTest
    @AttributeValueSource.WithoutString
    void deserializeConcurrentSkipListMapOfString_incorrectTypesInMap_returnsConcurrentSkipListMapWithoutItems(AttributeValue attributeValue) {
        ConcurrentSkipListMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentSkipListMapOfString(new AttributeValue().withM(mapOf("key", attributeValue)));
        assertThat(value).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("concurrentSkipListMapOfStringsSource")
    void putAndGet_symmetricCases_returnsItem(ConcurrentSkipListMap<String, String> concurrentSkipListMap) {
        Schema schema = schema(concurrentSkipListMap);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));
        assertThat(items).containsExactly(schema);
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(ConcurrentSkipListMap<String, String> concurrentSkipListMap) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setConcurrentSkipListMap(concurrentSkipListMap);
        return schema;
    }

    private static <K extends Comparable<K>, V> ConcurrentSkipListMap<K, V> mapOf() {
        return new ConcurrentSkipListMap<>();
    }

    private static <K extends Comparable<K>, V> ConcurrentSkipListMap<K, V> mapOf(K key, V value) {
        ConcurrentSkipListMap<K, V> map = new ConcurrentSkipListMap<>();
        map.put(key, value);
        return map;
    }

    private static <K extends Comparable<K>, V> ConcurrentSkipListMap<K, V> mapOf(K key1, V value1, K key2, V value2) {
        ConcurrentSkipListMap<K, V> map = new ConcurrentSkipListMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

}
