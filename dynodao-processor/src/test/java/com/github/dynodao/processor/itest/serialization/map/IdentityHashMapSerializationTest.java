package com.github.dynodao.processor.itest.serialization.map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.github.dynodao.processor.itest.AbstractIntegrationTest;
import com.github.dynodao.processor.test.params.AttributeValueSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class IdentityHashMapSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @Test
    void serializeIdentityHashMapOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeIdentityHashMapOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @MethodSource("identityHashMapsOfStringsSource")
    void serializeIdentityHashMapOfString_mapCases_returnsMapAttributeValue(IdentityHashMap<String, String> hashMap) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeIdentityHashMapOfString(hashMap);
        assertThat(value).isEqualTo(new AttributeValue().withM(hashMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue())))));
    }

    static Stream<IdentityHashMap<String, String>> identityHashMapsOfStringsSource() {
        return Stream.of(mapOf(), mapOf("key", "value"), mapOf("key1", "value1", "key2", "value2"));
    }

    @ParameterizedTest
    @MethodSource("identityHashMapsWithNullsSource")
    void serializeIdentityHashMapOfString_mapCasesWithNulls_returnsMapAttributeValueExcludingNulls(IdentityHashMap<String, String> hashMap) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeIdentityHashMapOfString(hashMap);
        assertThat(value).isEqualTo(new AttributeValue().withM(hashMap.entrySet().stream()
                .filter(e -> e.getKey() != null)
                .filter(e -> e.getValue() != null)
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue())))));
    }

    static Stream<IdentityHashMap<String, String>> identityHashMapsWithNullsSource() {
        return Stream.of(mapOf("key", null), mapOf("key1", null, "key2", "value2"), mapOf("key1", "value1", "key2", null),
                mapOf("key1", null, "key2", null), mapOf(null, "value"), mapOf(null, null));
    }

    @ParameterizedTest
    @NullSource
    @AttributeValueSource.WithoutMap
    void deserializeIdentityHashMapOfString_nullCases_returnsNull(AttributeValue attributeValue) {
        IdentityHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeIdentityHashMapOfString(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @MethodSource("identityHashMapsOfStringsSource")
    void deserializeIdentityHashMapOfString_correctTypesInMap_returnsIdentityHashMap(IdentityHashMap<String, String> hashMap) {
        AttributeValue attributeValue = new AttributeValue().withM(hashMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue()))));
        IdentityHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeIdentityHashMapOfString(attributeValue);
        assertThat(value).isEqualTo(hashMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> e.getValue())));
    }

    @ParameterizedTest
    @AttributeValueSource.WithoutString
    void deserializeIdentityHashMapOfString_incorrectTypesInMap_returnsIdentityHashMapWithoutItems(AttributeValue attributeValue) {
        IdentityHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeIdentityHashMapOfString(new AttributeValue().withM(mapOf("key", attributeValue)));
        assertThat(value).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("identityHashMapsWithNullsSource")
    void deserializeIdentityHashMapOfString_nullsInMap_returnsIdentityHashMapWithoutNulls(IdentityHashMap<String, String> hashMap) {
        AttributeValue attributeValue = new AttributeValue().withM(hashMap.entrySet().stream()
                .filter(e -> e.getKey() != null) // keys can't be null
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue()))));
        IdentityHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeIdentityHashMapOfString(attributeValue);
        assertThat(value).isEqualTo(hashMap.entrySet().stream()
                .filter(e -> e.getKey() != null)
                .filter(e -> e.getValue() != null)
                .collect(toMap(e -> e.getKey(), e -> e.getValue())));
    }

    @ParameterizedTest
    @AttributeValueSource.WithoutString
    void deserializeIdentityHashMapOfString_incorrectTypesInMapMultipleItems_returnsIdentityHashMapOnlyWithCorrectTypes(AttributeValue attributeValue) {
        IdentityHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeIdentityHashMapOfString(new AttributeValue().withM(
                mapOf("present", new AttributeValue("value"), "non-present", attributeValue)));
        assertThat(value).containsExactly(entry("present", "value"));
    }

    @ParameterizedTest
    @MethodSource("identityHashMapsOfStringsSource")
    void putAndGet_symmetricCases_returnsItem(IdentityHashMap<String, String> identityHashMap) {
        Schema schema = schema(identityHashMap);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));

        IdentityHashMap<String, String> expected = identityHashMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> e.getValue(), (l, r) -> l, IdentityHashMap::new));
        assertThat(items)
                .usingElementComparatorIgnoringFields("identityHashMap") // identity hash maps don't have a nice equals since they use == on entries
                .containsExactly(schema(expected))
                .allMatch(item -> new HashMap<>(expected).equals(item.getIdentityHashMap()));
    }

    @ParameterizedTest
    @MethodSource("identityHashMapsWithNullsSource")
    void putAndGet_asymmetricCases_returnsItemWithoutNulls(IdentityHashMap<String, String> identityHashMap) {
        Schema schema = schema(identityHashMap);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));

        IdentityHashMap<String, String> expected = identityHashMap.entrySet().stream()
                .filter(e -> e.getKey() != null)
                .filter(e -> e.getValue() != null)
                .collect(toMap(e -> e.getKey(), e -> e.getValue(), (l, r) -> l, IdentityHashMap::new));
        assertThat(items)
                .usingElementComparatorIgnoringFields("identityHashMap") // identity hash maps don't have a nice equals since they use == on entries
                .containsExactly(schema(expected))
                .allMatch(item -> new HashMap<>(expected).equals(item.getIdentityHashMap()));

    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(IdentityHashMap<String, String> identityHashMap) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setIdentityHashMap(identityHashMap);
        return schema;
    }

    private static <K, V> IdentityHashMap<K, V> mapOf() {
        return new IdentityHashMap<>();
    }

    private static <K, V> IdentityHashMap<K, V> mapOf(K key1, V value1) {
        IdentityHashMap<K, V> map = new IdentityHashMap<>();
        map.put(key1, value1);
        return map;
    }

    private static <K, V> IdentityHashMap<K, V> mapOf(K key1, V value1, K key2, V value2) {
        IdentityHashMap<K, V> map = new IdentityHashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

}
