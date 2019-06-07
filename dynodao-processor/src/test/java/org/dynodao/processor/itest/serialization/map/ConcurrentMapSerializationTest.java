package org.dynodao.processor.itest.serialization.map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractIntegrationTest;
import org.dynodao.processor.test.ParameterizedTestSources;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class ConcurrentMapSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @Test
    void serializeConcurrentMapOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeConcurrentMapOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @MethodSource("concurrentMapsOfStringsSource")
    void serializeConcurrentMapOfString_mapCases_returnsMapAttributeValue(ConcurrentMap<String, String> concurrentHashMap) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeConcurrentMapOfString(concurrentHashMap);
        assertThat(value).isEqualTo(new AttributeValue().withM(concurrentHashMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue())))));
    }

    static Stream<ConcurrentMap<String, String>> concurrentMapsOfStringsSource() {
        return Stream.of(mapOf(), mapOf("key", "value"), mapOf("key1", "value1", "key2", "value2"));
    }

    @ParameterizedTest
    @NullSource
    @ParameterizedTestSources.AttributeValuesWithoutMapSource
    void deserializeConcurrentMapOfString_nullCases_returnsNull(AttributeValue attributeValue) {
        ConcurrentMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentMapOfString(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @MethodSource("concurrentMapsOfStringsSource")
    void deserializeConcurrentHashMapOfString_correctTypesInMap_returnsConcurrentHashMap(ConcurrentMap<String, String> concurrentMap) {
        AttributeValue attributeValue = new AttributeValue().withM(concurrentMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue()))));
        ConcurrentMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentHashMapOfString(attributeValue);
        assertThat(value)
                .isInstanceOf(ConcurrentHashMap.class)
                .isEqualTo(concurrentMap);
    }

    @ParameterizedTest
    @ParameterizedTestSources.AttributeValuesWithoutStringSource
    void deserializeConcurrentMapOfString_incorrectTypesInMap_returnsConcurrentHashMapWithoutItems(AttributeValue attributeValue) {
        ConcurrentMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentMapOfString(new AttributeValue().withM(mapOf("key", attributeValue)));
        assertThat(value).isEmpty();
    }

    @ParameterizedTest
    @ParameterizedTestSources.AttributeValuesWithoutStringSource
    void deserializeConcurrentMapOfString_incorrectTypesInMapMultipleItems_returnsConcurrentHashMapOnlyWithCorrectTypes(AttributeValue attributeValue) {
        ConcurrentMap<String, String> value = SchemaAttributeValueSerializer.deserializeConcurrentMapOfString(new AttributeValue().withM(
                mapOf("present", new AttributeValue("value"), "non-present", attributeValue)));
        assertThat(value)
                .isInstanceOf(ConcurrentHashMap.class)
                .containsExactly(entry("present", "value"));
    }

    @ParameterizedTest
    @MethodSource("concurrentMapsOfStringsSource")
    void putAndGet_symmetricCases_returnsItem(ConcurrentMap<String, String> concurrentMap) {
        Schema schema = schema(concurrentMap);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));
        assertThat(items).containsExactly(schema);
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(ConcurrentMap<String, String> concurrentMap) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setConcurrentMap(concurrentMap);
        return schema;
    }

    private static <K, V> ConcurrentMap<K, V> mapOf() {
        return new ConcurrentHashMap<>();
    }

    private static <K, V> ConcurrentMap<K, V> mapOf(K key, V value) {
        ConcurrentMap<K, V> map = new ConcurrentHashMap<>();
        map.put(key, value);
        return map;
    }

    private static <K, V> ConcurrentMap<K, V> mapOf(K key1, V value1, K key2, V value2) {
        ConcurrentMap<K, V> map = new ConcurrentHashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

}
