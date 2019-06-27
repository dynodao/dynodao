package com.github.dynodao.processor.itest.serialization.map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.assertj.core.api.Assertions;
import com.github.dynodao.processor.itest.AbstractIntegrationTest;
import com.github.dynodao.processor.test.params.AttributeValueSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.Comparator;
import java.util.TreeMap;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class TreeMapSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    private static final Comparator<String> COMPARATOR = Comparator.nullsFirst(Comparator.naturalOrder());

    @Test
    void serializeTreeMapOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeTreeMapOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @MethodSource("treeMapsOfStringsSource")
    void serializeTreeMapOfString_mapCases_returnsMapAttributeValue(TreeMap<String, String> treeMap) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeTreeMapOfString(treeMap);
        assertThat(value).isEqualTo(new AttributeValue().withM(treeMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue())))));
    }

    static Stream<TreeMap<String, String>> treeMapsOfStringsSource() {
        return Stream.of(
                mapOf(), mapOf("key", "value"), mapOf("key1", "value1", "key2", "value2"),
                mapOf(COMPARATOR), mapOf(COMPARATOR, "key", "value"), mapOf(COMPARATOR, "key1", "value1", "key2", "value"));
    }

    @ParameterizedTest
    @MethodSource("treeMapsWithNullsSource")
    void serializeTreeMapOfString_mapCasesWithNulls_returnsMapAttributeValueExcludingNulls(TreeMap<String, String> treeMap) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeTreeMapOfString(treeMap);
        assertThat(value).isEqualTo(new AttributeValue().withM(treeMap.entrySet().stream()
                .filter(e -> e.getKey() != null)
                .filter(e -> e.getValue() != null)
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue())))));
    }

    static Stream<TreeMap<String, String>> treeMapsWithNullsSource() {
        return Stream.of(mapOf("key", null),
                mapOf("key1", null, "key2", "value2"), mapOf("key1", "value1", "key2", null), mapOf("key1", null, "key2", null),
                mapOf(COMPARATOR, "key", null),
                mapOf(COMPARATOR, "key1", null, "key2", "value2"), mapOf(COMPARATOR, "key1", "value1", "key2", null), mapOf(COMPARATOR, "key1", null, "key2", null),
                mapOf(COMPARATOR, null, "value"), mapOf(COMPARATOR, null, null),
                mapOf(COMPARATOR, "key1", "value1", null, "value2"), mapOf(COMPARATOR, "key1", "value1", null, null),
                mapOf(COMPARATOR, null, "value1", "key2", "value2"), mapOf(COMPARATOR, null, null, "key2", "value2"));
    }

    @ParameterizedTest
    @NullSource
    @AttributeValueSource.WithoutMap
    void deserializeTreeMapOfString_nullCases_returnsNull(AttributeValue attributeValue) {
        TreeMap<String, String> value = SchemaAttributeValueSerializer.deserializeTreeMapOfString(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @MethodSource("treeMapsOfStringsSource")
    void deserializeTreeMapOfString_correctTypesInMap_returnsTreeMap(TreeMap<String, String> treeMap) {
        AttributeValue attributeValue = new AttributeValue().withM(treeMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue()))));
        TreeMap<String, String> value = SchemaAttributeValueSerializer.deserializeTreeMapOfString(attributeValue);
        assertThat(value).isEqualTo(treeMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> e.getValue())));
    }

    @ParameterizedTest
    @AttributeValueSource.WithoutString
    void deserializeTreeMapOfString_incorrectTypesInMap_returnsTreeMapWithoutItems(AttributeValue attributeValue) {
        TreeMap<String, String> value = SchemaAttributeValueSerializer.deserializeTreeMapOfString(new AttributeValue().withM(mapOf("key", attributeValue)));
        assertThat(value).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("treeMapsWithNullsSource")
    void deserializeTreeMapOfString_nullsInMap_returnsTreeMapWithoutNulls(TreeMap<String, String> treeMap) {
        AttributeValue attributeValue = new AttributeValue().withM(treeMap.entrySet().stream()
                .filter(e -> e.getKey() != null) // keys can't be null
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue()))));
        TreeMap<String, String> value = SchemaAttributeValueSerializer.deserializeTreeMapOfString(attributeValue);
        assertThat(value).isEqualTo(treeMap.entrySet().stream()
                .filter(e -> e.getKey() != null)
                .filter(e -> e.getValue() != null)
                .collect(toMap(e -> e.getKey(), e -> e.getValue())));
    }

    @ParameterizedTest
    @AttributeValueSource.WithoutString
    void deserializeTreeMapOfString_incorrectTypesInMapMultipleItems_returnsTreeMapOnlyWithCorrectTypes(AttributeValue attributeValue) {
        TreeMap<String, String> value = SchemaAttributeValueSerializer.deserializeTreeMapOfString(new AttributeValue().withM(
                mapOf("present", new AttributeValue("value"), "non-present", attributeValue)));
        assertThat(value).containsExactly(entry("present", "value"));
    }

    @ParameterizedTest
    @MethodSource("treeMapsOfStringsSource")
    void putAndGet_symmetricCases_returnsItem(TreeMap<String, String> treeMap) {
        Schema schema = schema(treeMap);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));

        TreeMap<String, String> expected = treeMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> e.getValue(), (l, r) -> l, TreeMap::new));
        Assertions.assertThat(items).containsExactly(schema(expected));
    }

    @ParameterizedTest
    @MethodSource("treeMapsWithNullsSource")
    void putAndGet_asymmetricCases_returnsItemWithoutNulls(TreeMap<String, String> treeMap) {
        Schema schema = schema(treeMap);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));

        TreeMap<String, String> expected = treeMap.entrySet().stream()
                .filter(e -> e.getKey() != null)
                .filter(e -> e.getValue() != null)
                .collect(toMap(e -> e.getKey(), e -> e.getValue(), (l, r) -> l, TreeMap::new));
        Assertions.assertThat(items).containsExactly(schema(expected));
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(TreeMap<String, String> treeMap) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setTreeMap(treeMap);
        return schema;
    }

    private static <K extends Comparable<K>, V> TreeMap<K, V> mapOf() {
        return new TreeMap<>();
    }

    private static <K, V> TreeMap<K, V> mapOf(Comparator<K> comparator) {
        return new TreeMap<>(comparator);
    }

    private static <K extends Comparable<K>, V> TreeMap<K, V> mapOf(K key, V value) {
        TreeMap<K, V> map = new TreeMap<>();
        map.put(key, value);
        return map;
    }

    private static <K, V> TreeMap<K, V> mapOf(Comparator<K> comparator, K key, V value) {
        TreeMap<K, V> map = new TreeMap<>(comparator);
        map.put(key, value);
        return map;
    }

    private static <K extends Comparable<K>, V> TreeMap<K, V> mapOf(K key1, V value1, K key2, V value2) {
        TreeMap<K, V> map = new TreeMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

    private static <K, V> TreeMap<K, V> mapOf(Comparator<K> comparator, K key1, V value1, K key2, V value2) {
        TreeMap<K, V> map = new TreeMap<>(comparator);
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

}
