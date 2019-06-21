package org.dynodao.processor.itest.serialization.map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractIntegrationTest;
import org.dynodao.processor.test.params.AttributeValueSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class SortedMapSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    private static final Comparator<String> COMPARATOR = Comparator.nullsFirst(Comparator.naturalOrder());

    @Test
    void serializeTreeMapOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeSortedMapOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @MethodSource("sortedMapsOfStringsSource")
    void serializeSortedMapOfString_mapCases_returnsMapAttributeValue(SortedMap<String, String> sortedMap) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeSortedMapOfString(sortedMap);
        assertThat(value).isEqualTo(new AttributeValue().withM(sortedMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue())))));
    }

    static Stream<SortedMap<String, String>> sortedMapsOfStringsSource() {
        return Stream.of(mapOf(), mapOf("key", "value"), mapOf("key1", "value1", "key2", "value2"));
    }

    @ParameterizedTest
    @MethodSource("sortedMapsWithNullsSource")
    void serializeSortedMapOfString_mapCasesWithNulls_returnsMapAttributeValueExcludingNulls(SortedMap<String, String> sortedMap) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeSortedMapOfString(sortedMap);
        assertThat(value).isEqualTo(new AttributeValue().withM(sortedMap.entrySet().stream()
                .filter(e -> e.getKey() != null)
                .filter(e -> e.getValue() != null)
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue())))));
    }

    static Stream<SortedMap<String, String>> sortedMapsWithNullsSource() {
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
    void deserializeSortedMapOfString_nullCases_returnsNull(AttributeValue attributeValue) {
        SortedMap<String, String> value = SchemaAttributeValueSerializer.deserializeSortedMapOfString(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @MethodSource("sortedMapsOfStringsSource")
    void deserializeSortedMapOfString_correctTypesInMap_returnsTreeMap(SortedMap<String, String> sortedMap) {
        AttributeValue attributeValue = new AttributeValue().withM(sortedMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue()))));
        SortedMap<String, String> value = SchemaAttributeValueSerializer.deserializeSortedMapOfString(attributeValue);
        assertThat(value)
                .isInstanceOf(TreeMap.class)
                .isEqualTo(sortedMap.entrySet().stream()
                        .collect(toMap(e -> e.getKey(), e -> e.getValue())));
    }

    @ParameterizedTest
    @AttributeValueSource.WithoutString
    void deserializeSortedMapOfString_incorrectTypesInMap_returnsTreeMapWithoutItems(AttributeValue attributeValue) {
        SortedMap<String, String> value = SchemaAttributeValueSerializer.deserializeSortedMapOfString(new AttributeValue().withM(mapOf("key", attributeValue)));
        assertThat(value)
                .isInstanceOf(TreeMap.class)
                .isEmpty();
    }

    @ParameterizedTest
    @MethodSource("sortedMapsWithNullsSource")
    void deserializeSortedMapOfString_nullsInMap_returnsTreeMapWithoutNulls(SortedMap<String, String> sortedMap) {
        AttributeValue attributeValue = new AttributeValue().withM(sortedMap.entrySet().stream()
                .filter(e -> e.getKey() != null) // keys can't be null
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue()))));
        SortedMap<String, String> value = SchemaAttributeValueSerializer.deserializeSortedMapOfString(attributeValue);
        assertThat(value)
                .isInstanceOf(TreeMap.class)
                .isEqualTo(sortedMap.entrySet().stream()
                        .filter(e -> e.getKey() != null)
                        .filter(e -> e.getValue() != null)
                        .collect(toMap(e -> e.getKey(), e -> e.getValue())));
    }

    @ParameterizedTest
    @AttributeValueSource.WithoutString
    void deserializeSortedMapOfString_incorrectTypesInMapMultipleItems_returnsTreeMapOnlyWithCorrectTypes(AttributeValue attributeValue) {
        SortedMap<String, String> value = SchemaAttributeValueSerializer.deserializeSortedMapOfString(new AttributeValue().withM(
                mapOf("present", new AttributeValue("value"), "non-present", attributeValue)));
        assertThat(value)
                .isInstanceOf(TreeMap.class)
                .containsExactly(entry("present", "value"));
    }

    @ParameterizedTest
    @MethodSource("sortedMapsOfStringsSource")
    void putAndGet_symmetricCases_returnsItem(SortedMap<String, String> sortedMap) {
        Schema schema = schema(sortedMap);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));

        SortedMap<String, String> expected = sortedMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> e.getValue(), (l, r) -> l, TreeMap::new));
        assertThat(items).containsExactly(schema(expected));
    }

    @ParameterizedTest
    @MethodSource("sortedMapsWithNullsSource")
    void putAndGet_asymmetricCases_returnsItemWithoutNulls(SortedMap<String, String> sortedMap) {
        Schema schema = schema(sortedMap);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));

        SortedMap<String, String> expected = sortedMap.entrySet().stream()
                .filter(e -> e.getValue() != null)
                .collect(toMap(e -> e.getKey(), e -> e.getValue(), (l, r) -> l, TreeMap::new));
        assertThat(items).containsExactly(schema(expected));
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(SortedMap<String, String> sortedMap) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setSortedMap(sortedMap);
        return schema;
    }

    private static <K extends Comparable<K>, V> SortedMap<K, V> mapOf() {
        return new TreeMap<>();
    }

    private static <K, V> SortedMap<K, V> mapOf(Comparator<K> comparator) {
        return new TreeMap<>(comparator);
    }

    private static <K extends Comparable<K>, V> SortedMap<K, V> mapOf(K key, V value) {
        SortedMap<K, V> map = new TreeMap<>();
        map.put(key, value);
        return map;
    }

    private static <K, V> SortedMap<K, V> mapOf(Comparator<K> comparator, K key, V value) {
        SortedMap<K, V> map = new TreeMap<>(comparator);
        map.put(key, value);
        return map;
    }

    private static <K extends Comparable<K>, V> SortedMap<K, V> mapOf(K key1, V value1, K key2, V value2) {
        SortedMap<K, V> map = new TreeMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

    private static <K, V> SortedMap<K, V> mapOf(Comparator<K> comparator, K key1, V value1, K key2, V value2) {
        SortedMap<K, V> map = new TreeMap<>(comparator);
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

}
