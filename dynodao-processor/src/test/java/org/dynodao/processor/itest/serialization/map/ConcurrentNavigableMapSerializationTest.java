package org.dynodao.processor.itest.serialization.map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractIntegrationTest;
import org.dynodao.processor.test.ParameterizedTestSources;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class ConcurrentNavigableMapSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @Test
    void serializeConcurrentNavigableMapOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeConcurrentNavigableMapOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @MethodSource("concurrentNavigableMapOfStringsSource")
    void serializeConcurrentNavigableMapOfString_mapCases_returnsMapAttributeValue(ConcurrentNavigableMap<String, String> concurrentNavigableMap) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeConcurrentNavigableMapOfString(concurrentNavigableMap);
        assertThat(value).isEqualTo(new AttributeValue().withM(concurrentNavigableMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue())))));
    }

    static Stream<ConcurrentNavigableMap<String, String>> concurrentNavigableMapOfStringsSource() {
        return Stream.of(mapOf(), mapOf("key", "value"), mapOf("key1", "value1", "key2", "value2"));
    }

    @ParameterizedTest
    @NullSource
    @ParameterizedTestSources.AttributeValuesWithoutMapSource
    void deserializeConcurrentNavigableMapOfString_nullCases_returnsNull(AttributeValue attributeValue) {
        ConcurrentNavigableMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentNavigableMapOfString(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @MethodSource("concurrentNavigableMapOfStringsSource")
    void deserializeConcurrentNavigableMapOfString_correctTypesInMap_returnsConcurrentSkipListMap(ConcurrentNavigableMap<String, String> concurrentNavigableMap) {
        AttributeValue attributeValue = new AttributeValue().withM(concurrentNavigableMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue()))));
        ConcurrentNavigableMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentNavigableMapOfString(attributeValue);
        assertThat(value)
                .isInstanceOf(ConcurrentSkipListMap.class)
                .isEqualTo(concurrentNavigableMap);
    }

    @ParameterizedTest
    @ParameterizedTestSources.AttributeValuesWithoutStringSource
    void deserializeConcurrentNavigableMapOfString_incorrectTypesInMap_returnsConcurrentSkipListMapWithoutItems(AttributeValue attributeValue) {
        ConcurrentNavigableMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentNavigableMapOfString(new AttributeValue().withM(mapOf("key", attributeValue)));
        assertThat(value)
                .isInstanceOf(ConcurrentSkipListMap.class)
                .isEmpty();
    }

    @ParameterizedTest
    @ParameterizedTestSources.AttributeValuesWithoutStringSource
    void deserializeConcurrentNavigableMapOfString_incorrectTypesInMapMultipleItems_returnsConcurrentSkipListMapOnlyWithCorrectTypes(AttributeValue attributeValue) {
        ConcurrentNavigableMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentNavigableMapOfString(new AttributeValue().withM(
                mapOf("present", new AttributeValue("value"), "non-present", attributeValue)));
        assertThat(value)
                .isInstanceOf(ConcurrentSkipListMap.class)
                .containsExactly(entry("present", "value"));
    }

    @ParameterizedTest
    @MethodSource("concurrentNavigableMapOfStringsSource")
    void putAndGet_symmetricCases_returnsItem(ConcurrentNavigableMap<String, String> concurrentNavigableMap) {
        Schema schema = schema(concurrentNavigableMap);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));
        assertThat(items).containsExactly(schema);
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(ConcurrentNavigableMap<String, String> concurrentNavigableMap) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setConcurrentNavigableMap(concurrentNavigableMap);
        return schema;
    }

    private static <K extends Comparable<K>, V> ConcurrentNavigableMap<K, V> mapOf() {
        return new ConcurrentSkipListMap<>();
    }

    private static <K extends Comparable<K>, V> ConcurrentNavigableMap<K, V> mapOf(K key, V value) {
        ConcurrentNavigableMap<K, V> map = new ConcurrentSkipListMap<>();
        map.put(key, value);
        return map;
    }

    private static <K extends Comparable<K>, V> ConcurrentNavigableMap<K, V> mapOf(K key1, V value1, K key2, V value2) {
        ConcurrentNavigableMap<K, V> map = new ConcurrentSkipListMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

}
