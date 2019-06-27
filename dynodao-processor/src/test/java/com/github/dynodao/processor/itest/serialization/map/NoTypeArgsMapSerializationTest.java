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

class NoTypeArgsMapSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @Test
    void serializeNoTypeArgsMap_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeNoTypeArgsMap(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @MethodSource("noTypeArgsMapsOfStringsSource")
    void serializeNoTypeArgsMap_mapCases_returnsMapAttributeValue(NoTypeArgsMap noTypeArgsMap) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeNoTypeArgsMap(noTypeArgsMap);
        assertThat(value).isEqualTo(new AttributeValue().withM(noTypeArgsMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue())))));
    }

    static Stream<NoTypeArgsMap> noTypeArgsMapsOfStringsSource() {
        return Stream.of(mapOf(), mapOf("key", "value"), mapOf("key1", "value1", "key2", "value2"));
    }

    @ParameterizedTest
    @MethodSource("noTypeArgsMapsWithNullsSource")
    void serializeNoTypeArgsMap_mapCasesWithNulls_returnsMapAttributeValueExcludingNulls(NoTypeArgsMap noTypeArgsMap) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeNoTypeArgsMap(noTypeArgsMap);
        assertThat(value).isEqualTo(new AttributeValue().withM(noTypeArgsMap.entrySet().stream()
                .filter(e -> e.getKey() != null)
                .filter(e -> e.getValue() != null)
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue())))));
    }

    static Stream<NoTypeArgsMap> noTypeArgsMapsWithNullsSource() {
        return Stream.of(mapOf("key", null), mapOf("key1", null, "key2", "value2"), mapOf("key1", "value1", "key2", null),
                mapOf("key1", null, "key2", null), mapOf(null, "value"), mapOf(null, null));
    }

    @ParameterizedTest
    @NullSource
    @AttributeValueSource.WithoutMap
    void deserializeNoTypeArgsMap_nullCases_returnsNull(AttributeValue attributeValue) {
        NoTypeArgsMap value = SchemaAttributeValueSerializer.deserializeNoTypeArgsMap(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @MethodSource("noTypeArgsMapsOfStringsSource")
    void deserializeNoTypeArgsMap_correctTypesInMap_returnsHashMap(NoTypeArgsMap noTypeArgsMap) {
        AttributeValue attributeValue = new AttributeValue().withM(noTypeArgsMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue()))));
        NoTypeArgsMap value = SchemaAttributeValueSerializer.deserializeNoTypeArgsMap(attributeValue);
        assertThat(value).isEqualTo(noTypeArgsMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> e.getValue())));
    }

    @ParameterizedTest
    @AttributeValueSource.WithoutString
    void deserializeNoTypeArgsMap_incorrectTypesInMap_returnsHashMapWithoutItems(AttributeValue attributeValue) {
        NoTypeArgsMap value = SchemaAttributeValueSerializer.deserializeNoTypeArgsMap(new AttributeValue().withM(hashMapOf("key", attributeValue)));
        assertThat(value).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("noTypeArgsMapsWithNullsSource")
    void deserializeNoTypeArgsMap_nullsInMap_returnsHashMapWithoutNulls(NoTypeArgsMap noTypeArgsMap) {
        AttributeValue attributeValue = new AttributeValue().withM(noTypeArgsMap.entrySet().stream()
                .filter(e -> e.getKey() != null) // keys can't be null
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue()))));
        NoTypeArgsMap value = SchemaAttributeValueSerializer.deserializeNoTypeArgsMap(attributeValue);
        assertThat(value).isEqualTo(noTypeArgsMap.entrySet().stream()
                .filter(e -> e.getKey() != null)
                .filter(e -> e.getValue() != null)
                .collect(toMap(e -> e.getKey(), e -> e.getValue())));
    }

    @ParameterizedTest
    @AttributeValueSource.WithoutString
    void deserializeNoTypeArgsMap_incorrectTypesInMapMultipleItems_returnsHashMapOnlyWithCorrectTypes(AttributeValue attributeValue) {
        NoTypeArgsMap value = SchemaAttributeValueSerializer.deserializeNoTypeArgsMap(new AttributeValue().withM(
                hashMapOf("present", new AttributeValue("value"), "non-present", attributeValue)));
        assertThat(value).containsExactly(entry("present", "value"));
    }

    @ParameterizedTest
    @MethodSource("noTypeArgsMapsOfStringsSource")
    void putAndGet_symmetricCases_returnsItem(NoTypeArgsMap noTypeArgsMap) {
        Schema schema = schema(noTypeArgsMap);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));

        NoTypeArgsMap expected = noTypeArgsMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> e.getValue(), (l, r) -> l, NoTypeArgsMap::new));
        assertThat(items).containsExactly(schema(expected));
    }

    @ParameterizedTest
    @MethodSource("noTypeArgsMapsWithNullsSource")
    void putAndGet_asymmetricCases_returnsItemWithoutNulls(NoTypeArgsMap noTypeArgsMap) {
        Schema schema = schema(noTypeArgsMap);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));

        NoTypeArgsMap expected = noTypeArgsMap.entrySet().stream()
                .filter(e -> e.getKey() != null)
                .filter(e -> e.getValue() != null)
                .collect(toMap(e -> e.getKey(), e -> e.getValue(), (l, r) -> l, NoTypeArgsMap::new));
        assertThat(items).containsExactly(schema(expected));
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(NoTypeArgsMap noTypeArgsMap) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setNoTypeArgsMap(noTypeArgsMap);
        return schema;
    }

    private static NoTypeArgsMap mapOf() {
        return new NoTypeArgsMap();
    }

    private static NoTypeArgsMap mapOf(String key1, String value1) {
        NoTypeArgsMap map = new NoTypeArgsMap();
        map.put(key1, value1);
        return map;
    }

    private static NoTypeArgsMap mapOf(String key1, String value1, String key2, String value2) {
        NoTypeArgsMap map = new NoTypeArgsMap();
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
