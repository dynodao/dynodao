package org.dynodao.processor.itest.serialization.map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractIntegrationTest;
import org.dynodao.processor.test.params.AttributeValueSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class LinkedHashMapSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @Test
    void serializeLinkedHashMapOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeLinkedHashMapOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @MethodSource("linkedHashMapsOfStringsSource")
    void serializeLinkedHashMapOfString_mapCases_returnsMapAttributeValue(LinkedHashMap<String, String> linkedHashMap) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeLinkedHashMapOfString(linkedHashMap);
        assertThat(value).isEqualTo(new AttributeValue().withM(linkedHashMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue())))));
    }

    static Stream<HashMap<String, String>> linkedHashMapsOfStringsSource() {
        return Stream.of(mapOf(), mapOf("key", "value"), mapOf("key1", "value1", "key2", "value2"));
    }

    @ParameterizedTest
    @MethodSource("linkedHashMapsWithNullsSource")
    void serializeLinkedHashMapOfString_mapCasesWithNulls_returnsMapAttributeValueExcludingNulls(LinkedHashMap<String, String> linkedHashMap) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeLinkedHashMapOfString(linkedHashMap);
        assertThat(value).isEqualTo(new AttributeValue().withM(linkedHashMap.entrySet().stream()
                .filter(e -> e.getKey() != null)
                .filter(e -> e.getValue() != null)
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue())))));
    }

    static Stream<HashMap<String, String>> linkedHashMapsWithNullsSource() {
        return Stream.of(mapOf("key", null), mapOf("key1", null, "key2", "value2"), mapOf("key1", "value1", "key2", null),
                mapOf("key1", null, "key2", null), mapOf(null, "value"), mapOf(null, null));
    }

    @ParameterizedTest
    @NullSource
    @AttributeValueSource.WithoutMap
    void deserializeLinkedHashMapOfString_nullCases_returnsNull(AttributeValue attributeValue) {
        LinkedHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeLinkedHashMapOfString(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @MethodSource("linkedHashMapsOfStringsSource")
    void deserializeLinkedHashMapOfString_correctTypesInMap_returnsHashMap(HashMap<String, String> hashMap) {
        AttributeValue attributeValue = new AttributeValue().withM(hashMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue()))));
        LinkedHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeLinkedHashMapOfString(attributeValue);
        assertThat(value).isEqualTo(hashMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> e.getValue())));
    }

    @ParameterizedTest
    @AttributeValueSource.WithoutString
    void deserializeLinkedHashMapOfString_incorrectTypesInMap_returnsHashMapWithoutItems(AttributeValue attributeValue) {
        LinkedHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeLinkedHashMapOfString(new AttributeValue().withM(mapOf("key", attributeValue)));
        assertThat(value).isEmpty();
    }

    @ParameterizedTest
    @AttributeValueSource.WithoutString
    void deserializeLinkedHashMapOfString_incorrectTypesInMapMultipleItems_returnsHashMapOnlyWithCorrectTypes(AttributeValue attributeValue) {
        LinkedHashMap<String, String> value = SchemaAttributeValueSerializer.deserializeLinkedHashMapOfString(new AttributeValue().withM(
                mapOf("present", new AttributeValue("value"), "non-present", attributeValue)));
        assertThat(value).containsExactly(entry("present", "value"));
    }

    @ParameterizedTest
    @MethodSource("linkedHashMapsOfStringsSource")
    void putAndGet_symmetricCases_returnsItem(LinkedHashMap<String, String> linkedHashMap) {
        Schema schema = schema(linkedHashMap);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));

        LinkedHashMap<String, String> expected = linkedHashMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> e.getValue(), (l, r) -> l, LinkedHashMap::new));
        assertThat(items).containsExactly(schema(expected));
    }

    @ParameterizedTest
    @MethodSource("linkedHashMapsWithNullsSource")
    void putAndGet_asymmetricCases_returnsItemWithoutNulls(LinkedHashMap<String, String> linkedHashMap) {
        Schema schema = schema(linkedHashMap);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));

        LinkedHashMap<String, String> expected = linkedHashMap.entrySet().stream()
                .filter(e -> e.getKey() != null)
                .filter(e -> e.getValue() != null)
                .collect(toMap(e -> e.getKey(), e -> e.getValue(), (l, r) -> l, LinkedHashMap::new));
        assertThat(items).containsExactly(schema(expected));
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(LinkedHashMap<String, String> linkedHashMap) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setLinkedHashMap(linkedHashMap);
        return schema;
    }

    private static <K, V> LinkedHashMap<K, V> mapOf() {
        return new LinkedHashMap<>();
    }

    private static <K, V> LinkedHashMap<K, V> mapOf(K key1, V value1) {
        LinkedHashMap<K, V> map = new LinkedHashMap<>();
        map.put(key1, value1);
        return map;
    }

    private static <K, V> LinkedHashMap<K, V> mapOf(K key1, V value1, K key2, V value2) {
        LinkedHashMap<K, V> map = new LinkedHashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

}
