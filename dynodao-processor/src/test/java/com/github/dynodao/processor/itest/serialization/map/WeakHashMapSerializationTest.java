package com.github.dynodao.processor.itest.serialization.map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.github.dynodao.processor.itest.AbstractIntegrationTest;
import com.github.dynodao.processor.test.params.AttributeValueSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.WeakHashMap;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class WeakHashMapSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @Test
    void serializeWeakHashMapOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeWeakHashMapOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @MethodSource("weakHashMapsOfStringsSource")
    void serializeWeakHashMapOfString_mapCases_returnsMapAttributeValue(WeakHashMap<String, String> weakHashMap) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeWeakHashMapOfString(weakHashMap);
        assertThat(value).isEqualTo(new AttributeValue().withM(weakHashMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue())))));
    }

    static Stream<WeakHashMap<String, String>> weakHashMapsOfStringsSource() {
        return Stream.of(mapOf(), mapOf("key", "value"), mapOf("key1", "value1", "key2", "value2"));
    }

    @ParameterizedTest
    @MethodSource("weakHashMapsWithNullsSource")
    void serializeWeakHashMapOfString_mapCasesWithNulls_returnsMapAttributeValueExcludingNulls(WeakHashMap<String, String> weakHashMap) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeWeakHashMapOfString(weakHashMap);
        assertThat(value).isEqualTo(new AttributeValue().withM(weakHashMap.entrySet().stream()
                .filter(e -> e.getKey() != null)
                .filter(e -> e.getValue() != null)
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue())))));
    }

    static Stream<WeakHashMap<String, String>> weakHashMapsWithNullsSource() {
        return Stream.of(mapOf("key", null), mapOf("key1", null, "key2", "value2"), mapOf("key1", "value1", "key2", null),
                mapOf("key1", null, "key2", null), mapOf(null, "value"), mapOf(null, null));
    }

    @ParameterizedTest
    @NullSource
    @AttributeValueSource.WithoutMap
    void deserializeWeakHashMapOfString_nullCases_returnsNull(AttributeValue attributeValue) {
        WeakHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeWeakHashMapOfString(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @MethodSource("weakHashMapsOfStringsSource")
    void deserializeWeakHashMapOfString_correctTypesInMap_returnsWeakHashMap(WeakHashMap<String, String> weakHashMap) {
        AttributeValue attributeValue = new AttributeValue().withM(weakHashMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue()))));
        WeakHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeWeakHashMapOfString(attributeValue);
        assertThat(value).isEqualTo(weakHashMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> e.getValue())));
    }

    @ParameterizedTest
    @AttributeValueSource.WithoutString
    void deserializeWeakHashMapOfString_incorrectTypesInMap_returnsWeakHashMapWithoutItems(AttributeValue attributeValue) {
        WeakHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeWeakHashMapOfString(new AttributeValue().withM(mapOf("key", attributeValue)));
        assertThat(value).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("weakHashMapsWithNullsSource")
    void deserializeWeakHashMapOfString_nullsInMap_returnsWeakHashMapWithoutNulls(WeakHashMap<String, String> weakHashMap) {
        AttributeValue attributeValue = new AttributeValue().withM(weakHashMap.entrySet().stream()
                .filter(e -> e.getKey() != null) // keys can't be null
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue()))));
        WeakHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeWeakHashMapOfString(attributeValue);
        assertThat(value).isEqualTo(weakHashMap.entrySet().stream()
                .filter(e -> e.getKey() != null)
                .filter(e -> e.getValue() != null)
                .collect(toMap(e -> e.getKey(), e -> e.getValue())));
    }

    @ParameterizedTest
    @AttributeValueSource.WithoutString
    void deserializeWeakHashMapOfString_incorrectTypesInMapMultipleItems_returnsWeakHashMapOnlyWithCorrectTypes(AttributeValue attributeValue) {
        WeakHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeWeakHashMapOfString(new AttributeValue().withM(
                mapOf("present", new AttributeValue("value"), "non-present", attributeValue)));
        assertThat(value).containsExactly(entry("present", "value"));
    }

    @ParameterizedTest
    @MethodSource("weakHashMapsOfStringsSource")
    void putAndGet_symmetricCases_returnsItem(WeakHashMap<String, String> weakHashMap) {
        Schema schema = schema(weakHashMap);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));

        WeakHashMap<String, String> expected = weakHashMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> e.getValue(), (l, r) -> l, WeakHashMap::new));
        assertThat(items).containsExactly(schema(expected));
    }

    @ParameterizedTest
    @MethodSource("weakHashMapsWithNullsSource")
    void putAndGet_asymmetricCases_returnsItemWithoutNulls(WeakHashMap<String, String> weakHashMap) {
        Schema schema = schema(weakHashMap);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));

        WeakHashMap<String, String> expected = weakHashMap.entrySet().stream()
                .filter(e -> e.getKey() != null)
                .filter(e -> e.getValue() != null)
                .collect(toMap(e -> e.getKey(), e -> e.getValue(), (l, r) -> l, WeakHashMap::new));
        assertThat(items).containsExactly(schema(expected));
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(WeakHashMap<String, String> weakHashMap) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setWeakHashMap(weakHashMap);
        return schema;
    }

    private static <K, V> WeakHashMap<K, V> mapOf() {
        return new WeakHashMap<>();
    }

    private static <K, V> WeakHashMap<K, V> mapOf(K key1, V value1) {
        WeakHashMap<K, V> map = new WeakHashMap<>();
        map.put(key1, value1);
        return map;
    }

    private static <K, V> WeakHashMap<K, V> mapOf(K key1, V value1, K key2, V value2) {
        WeakHashMap<K, V> map = new WeakHashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

}
