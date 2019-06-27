package com.github.dynodao.processor.itest.serialization.map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.github.dynodao.processor.itest.AbstractIntegrationTest;
import com.github.dynodao.processor.test.params.AttributeValueSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.Comparator;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class NavigableMapSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    private static final Comparator<String> COMPARATOR = Comparator.nullsFirst(Comparator.naturalOrder());

    @Test
    void serializeTreeMapOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeNavigableMapOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @MethodSource("navigableMapsOfStringsSource")
    void serializeNavigableMapOfString_mapCases_returnsMapAttributeValue(NavigableMap<String, String> navigableMap) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeNavigableMapOfString(navigableMap);
        assertThat(value).isEqualTo(new AttributeValue().withM(navigableMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue())))));
    }

    static Stream<NavigableMap<String, String>> navigableMapsOfStringsSource() {
        return Stream.of(mapOf(), mapOf("key", "value"), mapOf("key1", "value1", "key2", "value2"));
    }

    @ParameterizedTest
    @MethodSource("navigableMapsWithNullsSource")
    void serializeNavigableMapOfString_mapCasesWithNulls_returnsMapAttributeValueExcludingNulls(NavigableMap<String, String> navigableMap) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeNavigableMapOfString(navigableMap);
        assertThat(value).isEqualTo(new AttributeValue().withM(navigableMap.entrySet().stream()
                .filter(e -> e.getKey() != null)
                .filter(e -> e.getValue() != null)
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue())))));
    }

    static Stream<NavigableMap<String, String>> navigableMapsWithNullsSource() {
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
    void deserializeNavigableMapOfString_nullCases_returnsNull(AttributeValue attributeValue) {
        NavigableMap<String, String> value = SchemaAttributeValueSerializer.deserializeNavigableMapOfString(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @MethodSource("navigableMapsOfStringsSource")
    void deserializeNavigableMapOfString_correctTypesInMap_returnsTreeMap(NavigableMap<String, String> navigableMap) {
        AttributeValue attributeValue = new AttributeValue().withM(navigableMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue()))));
        NavigableMap<String, String> value = SchemaAttributeValueSerializer.deserializeNavigableMapOfString(attributeValue);
        assertThat(value)
                .isInstanceOf(TreeMap.class)
                .isEqualTo(navigableMap.entrySet().stream()
                    .collect(toMap(e -> e.getKey(), e -> e.getValue())));
    }

    @ParameterizedTest
    @AttributeValueSource.WithoutString
    void deserializeNavigableMapOfString_incorrectTypesInMap_returnsTreeMapWithoutItems(AttributeValue attributeValue) {
        NavigableMap<String, String> value = SchemaAttributeValueSerializer.deserializeNavigableMapOfString(new AttributeValue().withM(mapOf("key", attributeValue)));
        assertThat(value)
                .isInstanceOf(TreeMap.class)
                .isEmpty();
    }

    @ParameterizedTest
    @MethodSource("navigableMapsWithNullsSource")
    void deserializeNavigableMapOfString_nullsInMap_returnsTreeMapWithoutNulls(NavigableMap<String, String> navigableMap) {
        AttributeValue attributeValue = new AttributeValue().withM(navigableMap.entrySet().stream()
                .filter(e -> e.getKey() != null) // keys can't be null
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue()))));
        NavigableMap<String, String> value = SchemaAttributeValueSerializer.deserializeNavigableMapOfString(attributeValue);
        assertThat(value)
                .isInstanceOf(TreeMap.class)
                .isEqualTo(navigableMap.entrySet().stream()
                        .filter(e -> e.getKey() != null)
                        .filter(e -> e.getValue() != null)
                        .collect(toMap(e -> e.getKey(), e -> e.getValue())));
    }

    @ParameterizedTest
    @AttributeValueSource.WithoutString
    void deserializeNavigableMapOfString_incorrectTypesInMapMultipleItems_returnsTreeMapOnlyWithCorrectTypes(AttributeValue attributeValue) {
        NavigableMap<String, String> value = SchemaAttributeValueSerializer.deserializeNavigableMapOfString(new AttributeValue().withM(
                mapOf("present", new AttributeValue("value"), "non-present", attributeValue)));
        assertThat(value)
                .isInstanceOf(TreeMap.class)
                .containsExactly(entry("present", "value"));
    }

    @ParameterizedTest
    @MethodSource("navigableMapsOfStringsSource")
    void putAndGet_symmetricCases_returnsItem(NavigableMap<String, String> navigableMap) {
        Schema schema = schema(navigableMap);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));

        NavigableMap<String, String> expected = navigableMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> e.getValue(), (l, r) -> l, TreeMap::new));
        assertThat(items).containsExactly(schema(expected));
    }

    @ParameterizedTest
    @MethodSource("navigableMapsWithNullsSource")
    void putAndGet_asymmetricCases_returnsItemWithoutNulls(NavigableMap<String, String> navigableMap) {
        Schema schema = schema(navigableMap);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));

        NavigableMap<String, String> expected = navigableMap.entrySet().stream()
                .filter(e -> e.getKey() != null)
                .filter(e -> e.getValue() != null)
                .collect(toMap(e -> e.getKey(), e -> e.getValue(), (l, r) -> l, TreeMap::new));
        assertThat(items).containsExactly(schema(expected));
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(NavigableMap<String, String> navigableMap) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setNavigableMap(navigableMap);
        return schema;
    }

    private static <K extends Comparable<K>, V> NavigableMap<K, V> mapOf() {
        return new TreeMap<>();
    }

    private static <K, V> NavigableMap<K, V> mapOf(Comparator<K> comparator) {
        return new TreeMap<>(comparator);
    }

    private static <K extends Comparable<K>, V> NavigableMap<K, V> mapOf(K key, V value) {
        NavigableMap<K, V> map = new TreeMap<>();
        map.put(key, value);
        return map;
    }

    private static <K, V> NavigableMap<K, V> mapOf(Comparator<K> comparator, K key, V value) {
        NavigableMap<K, V> map = new TreeMap<>(comparator);
        map.put(key, value);
        return map;
    }

    private static <K extends Comparable<K>, V> NavigableMap<K, V> mapOf(K key1, V value1, K key2, V value2) {
        NavigableMap<K, V> map = new TreeMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

    private static <K, V> NavigableMap<K, V> mapOf(Comparator<K> comparator, K key1, V value1, K key2, V value2) {
        NavigableMap<K, V> map = new TreeMap<>(comparator);
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

}
