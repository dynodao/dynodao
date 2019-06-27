package com.github.dynodao.processor.itest.serialization.map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.github.dynodao.processor.itest.AbstractIntegrationTest;
import com.github.dynodao.processor.test.params.AttributeValueSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class MapSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @Test
    void serializeMapOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeMapOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @MethodSource("mapsOfStringsSource")
    void serializeMapOfString_mapCases_returnsMapAttributeValue(Map<String, String> map) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeMapOfString(map);
        assertThat(value).isEqualTo(new AttributeValue().withM(map.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue())))));
    }

    static Stream<Map<String, String>> mapsOfStringsSource() {
        return Stream.of(mapOf(), mapOf("key", "value"), mapOf("key1", "value1", "key2", "value2"));
    }

    @ParameterizedTest
    @MethodSource("mapsWithNullsSource")
    void serializeMapOfString_mapCasesWithNulls_returnsMapAttributeValueExcludingNulls(Map<String, String> map) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeMapOfString(map);
        assertThat(value).isEqualTo(new AttributeValue().withM(map.entrySet().stream()
                .filter(e -> e.getKey() != null)
                .filter(e -> e.getValue() != null)
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue())))));
    }

    static Stream<Map<String, String>> mapsWithNullsSource() {
        return Stream.of(mapOf("key", null), mapOf("key1", null, "key2", "value2"), mapOf("key1", "value1", "key2", null),
                mapOf("key1", null, "key2", null), mapOf(null, "value"), mapOf(null, null));
    }

    @ParameterizedTest
    @NullSource
    @AttributeValueSource.WithoutMap
    void deserializeMapOfString_nullCases_returnsNull(AttributeValue attributeValue) {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeMapOfString(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @MethodSource("mapsOfStringsSource")
    void deserializeMapOfString_correctTypesInMap_returnsLinkedHashMap(Map<String, String> map) {
        AttributeValue attributeValue = new AttributeValue().withM(map.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue()))));
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeMapOfString(attributeValue);
        assertThat(value)
                .isInstanceOf(LinkedHashMap.class)
                .isEqualTo(map.entrySet().stream()
                    .collect(toMap(e -> e.getKey(), e -> e.getValue())));
    }

    @ParameterizedTest
    @AttributeValueSource.WithoutString
    void deserializeMapOfString_incorrectTypesInMap_returnsLinkedHashMapWithoutItems(AttributeValue attributeValue) {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeMapOfString(new AttributeValue().withM(mapOf("key", attributeValue)));
        assertThat(value).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("mapsWithNullsSource")
    void deserializeMapOfString_nullsInMap_returnsLinkedHashMapWithoutNulls(Map<String, String> map) {
        AttributeValue attributeValue = new AttributeValue().withM(map.entrySet().stream()
                .filter(e -> e.getKey() != null) // keys can't be null
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue()))));
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeMapOfString(attributeValue);
        assertThat(value)
                .isInstanceOf(LinkedHashMap.class)
                .isEqualTo(map.entrySet().stream()
                    .filter(e -> e.getKey() != null)
                    .filter(e -> e.getValue() != null)
                    .collect(toMap(e -> e.getKey(), e -> e.getValue())));
    }

    @ParameterizedTest
    @AttributeValueSource.WithoutString
    void deserializeMapOfString_incorrectTypesInMapMultipleItems_returnsLinkedHashMapOnlyWithCorrectTypes(AttributeValue attributeValue) {
        Map<String, String> value = SchemaAttributeValueSerializer.deserializeMapOfString(new AttributeValue().withM(
                mapOf("present", new AttributeValue("value"), "non-present", attributeValue)));
        assertThat(value)
                .isInstanceOf(LinkedHashMap.class)
                .containsExactly(entry("present", "value"));
    }

    @ParameterizedTest
    @MethodSource("mapsOfStringsSource")
    void putAndGet_symmetricCases_returnsItem(Map<String, String> map) {
        Schema schema = schema(map);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));

        Map<String, String> expected = map.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> e.getValue(), (l, r) -> l, HashMap::new));
        assertThat(items).containsExactly(schema(expected));
    }

    @ParameterizedTest
    @MethodSource("mapsWithNullsSource")
    void putAndGet_asymmetricCases_returnsItemWithoutNulls(Map<String, String> map) {
        Schema schema = schema(map);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));

        Map<String, String> expected = map.entrySet().stream()
                .filter(e -> e.getKey() != null)
                .filter(e -> e.getValue() != null)
                .collect(toMap(e -> e.getKey(), e -> e.getValue(), (l, r) -> l, HashMap::new));
        assertThat(items).containsExactly(schema(expected));
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(Map<String, String> map) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setMap(map);
        return schema;
    }

    private static <K, V> Map<K, V> mapOf() {
        return new LinkedHashMap<>();
    }

    private static <K, V> Map<K, V> mapOf(K key1, V value1) {
        Map<K, V> map = new LinkedHashMap<>();
        map.put(key1, value1);
        return map;
    }

    private static <K, V> Map<K, V> mapOf(K key1, V value1, K key2, V value2) {
        Map<K, V> map = new LinkedHashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

}
