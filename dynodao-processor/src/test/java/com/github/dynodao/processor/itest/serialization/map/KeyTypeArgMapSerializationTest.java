package com.github.dynodao.processor.itest.serialization.map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.github.dynodao.processor.itest.AbstractIntegrationTest;
import com.github.dynodao.processor.test.params.AttributeValueSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class KeyTypeArgMapSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @Test
    void serializeKeyTypeArgMapOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeKeyTypeArgMapOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @MethodSource("keyTypeArgMapsOfStringsSource")
    void serializeKeyTypeArgMapOfString_mapCases_returnsMapAttributeValue(KeyTypeArgMap<String> keyTypeArgMap) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeKeyTypeArgMapOfString(keyTypeArgMap);
        assertThat(value).isEqualTo(new AttributeValue().withM(keyTypeArgMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue())))));
    }

    static Stream<KeyTypeArgMap<String>> keyTypeArgMapsOfStringsSource() {
        return Stream.of(mapOf(), mapOf("key", "value"), mapOf("key1", "value1", "key2", "value2"));
    }

    @ParameterizedTest
    @MethodSource("keyTypeArgMapsWithNullsSource")
    void serializeKeyTypeArgMapOfString_mapCasesWithNulls_returnsMapAttributeValueExcludingNulls(KeyTypeArgMap<String> keyTypeArgMap) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeKeyTypeArgMapOfString(keyTypeArgMap);
        assertThat(value).isEqualTo(new AttributeValue().withM(keyTypeArgMap.entrySet().stream()
                .filter(e -> e.getKey() != null)
                .filter(e -> e.getValue() != null)
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue())))));
    }

    static Stream<KeyTypeArgMap<String>> keyTypeArgMapsWithNullsSource() {
        return Stream.of(mapOf("key", null), mapOf("key1", null, "key2", "value2"), mapOf("key1", "value1", "key2", null),
                mapOf("key1", null, "key2", null), mapOf(null, "value"), mapOf(null, null));
    }

    @ParameterizedTest
    @NullSource
    @AttributeValueSource.WithoutMap
    void deserializeKeyTypeArgMapOfString_nullCases_returnsNull(AttributeValue attributeValue) {
        KeyTypeArgMap<String> value = SchemaAttributeValueSerializer.deserializeKeyTypeArgMapOfString(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @MethodSource("keyTypeArgMapsOfStringsSource")
    void deserializeKeyTypeArgMapOfString_correctTypesInMap_returnsHashMap(KeyTypeArgMap<String> keyTypeArgMap) {
        AttributeValue attributeValue = new AttributeValue().withM(keyTypeArgMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue()))));
        KeyTypeArgMap<String> value = SchemaAttributeValueSerializer.deserializeKeyTypeArgMapOfString(attributeValue);
        assertThat(value).isEqualTo(keyTypeArgMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> e.getValue())));
    }

    @ParameterizedTest
    @AttributeValueSource.WithoutString
    void deserializeKeyTypeArgMapOfString_incorrectTypesInMap_returnsHashMapWithoutItems(AttributeValue attributeValue) {
        KeyTypeArgMap<String> value = SchemaAttributeValueSerializer.deserializeKeyTypeArgMapOfString(new AttributeValue().withM(hashMapOf("key", attributeValue)));
        assertThat(value).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("keyTypeArgMapsWithNullsSource")
    void deserializeKeyTypeArgMapOfString_nullsInMap_returnsHashMapWithoutNulls(KeyTypeArgMap<String> keyTypeArgMap) {
        AttributeValue attributeValue = new AttributeValue().withM(keyTypeArgMap.entrySet().stream()
                .filter(e -> e.getKey() != null) // keys can't be null
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue()))));
        KeyTypeArgMap<String> value = SchemaAttributeValueSerializer.deserializeKeyTypeArgMapOfString(attributeValue);
        assertThat(value).isEqualTo(keyTypeArgMap.entrySet().stream()
                .filter(e -> e.getKey() != null)
                .filter(e -> e.getValue() != null)
                .collect(toMap(e -> e.getKey(), e -> e.getValue())));
    }

    @ParameterizedTest
    @AttributeValueSource.WithoutString
    void deserializeKeyTypeArgMapOfString_incorrectTypesInMapMultipleItems_returnsHashMapOnlyWithCorrectTypes(AttributeValue attributeValue) {
        KeyTypeArgMap<String> value = SchemaAttributeValueSerializer.deserializeKeyTypeArgMapOfString(new AttributeValue().withM(
                hashMapOf("present", new AttributeValue("value"), "non-present", attributeValue)));
        assertThat(value).containsExactly(entry("present", "value"));
    }

    @ParameterizedTest
    @MethodSource("keyTypeArgMapsOfStringsSource")
    void putAndGet_symmetricCases_returnsItem(KeyTypeArgMap<String> keyTypeArgMap) {
        Schema schema = schema(keyTypeArgMap);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));

        KeyTypeArgMap<String> expected = keyTypeArgMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> e.getValue(), (l, r) -> l, KeyTypeArgMap::new));
        assertThat(items).containsExactly(schema(expected));
    }

    @ParameterizedTest
    @MethodSource("keyTypeArgMapsWithNullsSource")
    void putAndGet_asymmetricCases_returnsItemWithoutNulls(KeyTypeArgMap<String> keyTypeArgMap) {
        Schema schema = schema(keyTypeArgMap);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));

        KeyTypeArgMap<String> expected = keyTypeArgMap.entrySet().stream()
                .filter(e -> e.getKey() != null)
                .filter(e -> e.getValue() != null)
                .collect(toMap(e -> e.getKey(), e -> e.getValue(), (l, r) -> l, KeyTypeArgMap::new));
        assertThat(items).containsExactly(schema(expected));
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(KeyTypeArgMap<String> keyTypeArgMap) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setKeyTypeArgMap(keyTypeArgMap);
        return schema;
    }

    private static <K> KeyTypeArgMap<K> mapOf() {
        return new KeyTypeArgMap<>();
    }

    private static <K> KeyTypeArgMap<K> mapOf(K key1, String value1) {
        KeyTypeArgMap<K> map = new KeyTypeArgMap<>();
        map.put(key1, value1);
        return map;
    }

    private static <K> KeyTypeArgMap<K> mapOf(K key1, String value1, K key2, String value2) {
        KeyTypeArgMap<K> map = new KeyTypeArgMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

    private static <K, V> Map<K, V> hashMapOf(K key1, V value1) {
        Map<K, V> map = new HashMap<>();
        map.put(key1, value1);
        return map;
    }

    private static <K, V> Map<K, V> hashMapOf(K key1, V value1, K key2, V value2) {
        Map<K, V> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

}
