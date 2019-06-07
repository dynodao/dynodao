package org.dynodao.processor.itest.serialization.map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractIntegrationTest;
import org.dynodao.processor.test.ParameterizedTestSources;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.HashMap;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class HashMapSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @Test
    void serializeHashMapOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeHashMapOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @MethodSource("hashMapsOfStringsSource")
    void serializeHashMapOfString_mapCases_returnsMapAttributeValue(HashMap<String, String> hashMap) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeHashMapOfString(hashMap);
        assertThat(value).isEqualTo(new AttributeValue().withM(hashMap.entrySet().stream()
                .filter(e -> e.getValue() != null)
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue())))));
    }

    static Stream<HashMap<String, String>> hashMapsOfStringsSource() {
        return Stream.of(mapOf(), mapOf("key", "value"), mapOf("key1", "value1", "key2", "value2"),
                mapOf("key", null), mapOf("key1", null, "key2", "value2"), mapOf("key1", "value1", "key2", null),
                mapOf("key1", null, "key2", null), mapOf(null, "value"), mapOf(null, null));
    }

    @ParameterizedTest
    @NullSource
    @MethodSource(ParameterizedTestSources.ATTRIBUTE_VALUES_WITHOUT_MAP_SOURCE)
    void deserializeHashMapOfString_nullCases_returnsNull(AttributeValue attributeValue) {
        HashMap<String, String> value = SchemaAttributeValueSerializer.deserializeHashMapOfString(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @MethodSource("hashMapsOfStringsSource")
    void deserializeHashMapOfString_correctTypesInMap_returnsHashMap(HashMap<String, String> hashMap) {
        AttributeValue attributeValue = new AttributeValue().withM(hashMap.entrySet().stream()
                .filter(e -> e.getValue() != null)
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue()))));
        HashMap<String, String> value = SchemaAttributeValueSerializer.deserializeHashMapOfString(attributeValue);
        assertThat(value).isEqualTo(hashMap.entrySet().stream()
                .filter(e -> e.getValue() != null)
                .collect(toMap(e -> e.getKey(), e -> e.getValue())));
    }

    @ParameterizedTest
    @MethodSource(ParameterizedTestSources.ATTRIBUTE_VALUES_WITHOUT_STRING_SOURCE)
    void deserializeHashMapOfString_incorrectTypesInMap_returnsHashMapWithoutItems(AttributeValue attributeValue) {
        HashMap<String, String> value = SchemaAttributeValueSerializer.deserializeHashMapOfString(new AttributeValue().withM(mapOf("key", attributeValue)));
        assertThat(value).isEmpty();
    }

    @ParameterizedTest
    @MethodSource(ParameterizedTestSources.ATTRIBUTE_VALUES_WITHOUT_STRING_SOURCE)
    void deserializeHashMapOfString_incorrectTypesInMapMultipleItems_returnsHashMapOnlyWithCorrectTypes(AttributeValue attributeValue) {
        HashMap<String, String> value = SchemaAttributeValueSerializer.deserializeHashMapOfString(new AttributeValue().withM(
                mapOf("present", new AttributeValue("value"), "non-present", attributeValue)));
        assertThat(value).containsExactly(entry("present", "value"));
    }

    @ParameterizedTest
    @MethodSource("hashMapsOfStringsSource")
    void putAndGet_symmetricCases_returnsItem(HashMap<String, String> hashMap) {
        Schema schema = schema(hashMap);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));

        HashMap<String, String> expected = hashMap.entrySet().stream()
                .filter(e -> e.getValue() != null)
                .collect(toMap(e -> e.getKey(), e -> e.getValue(), (l, r) -> l, HashMap::new));
        assertThat(items).containsExactly(schema(expected));
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(HashMap<String, String> hashMap) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setHashMap(hashMap);
        return schema;
    }

    private static <K, V> HashMap<K, V> mapOf() {
        return new HashMap<>();
    }

    private static <K, V> HashMap<K, V> mapOf(K key1, V value1) {
        HashMap<K, V> map = new HashMap<>();
        map.put(key1, value1);
        return map;
    }

    private static <K, V> HashMap<K, V> mapOf(K key1, V value1, K key2, V value2) {
        HashMap<K, V> map = new HashMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

}
