package com.github.dynodao.processor.itest.serialization.map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.github.dynodao.processor.itest.AbstractIntegrationTest;
import com.github.dynodao.processor.test.params.AttributeValueSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class ValueTypeArgMapSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @Test
    void serializeValueTypeArgMapOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeValueTypeArgMapOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @MethodSource("valueTypeArgMapsOfStringsSource")
    void serializeValueTypeArgMapOfString_mapCases_returnsMapAttributeValue(ValueTypeArgMap<String> valueTypeArgMap) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeValueTypeArgMapOfString(valueTypeArgMap);
        assertThat(value).isEqualTo(new AttributeValue().withM(valueTypeArgMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue())))));
    }

    static Stream<ValueTypeArgMap<String>> valueTypeArgMapsOfStringsSource() {
        return Stream.of(mapOf(), mapOf("key", "value"), mapOf("key1", "value1", "key2", "value2"));
    }

    @ParameterizedTest
    @MethodSource("valueTypeArgMapsWithNullsSource")
    void serializeValueTypeArgMapOfString_mapCasesWithNulls_returnsMapAttributeValueExcludingNulls(ValueTypeArgMap<String> valueTypeArgMap) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeValueTypeArgMapOfString(valueTypeArgMap);
        assertThat(value).isEqualTo(new AttributeValue().withM(valueTypeArgMap.entrySet().stream()
                .filter(e -> e.getKey() != null)
                .filter(e -> e.getValue() != null)
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue())))));
    }

    static Stream<ValueTypeArgMap<String>> valueTypeArgMapsWithNullsSource() {
        return Stream.of(mapOf("key", null), mapOf("key1", null, "key2", "value2"), mapOf("key1", "value1", "key2", null),
                mapOf("key1", null, "key2", null), mapOf(null, "value"), mapOf(null, null));
    }

    @ParameterizedTest
    @NullSource
    @AttributeValueSource.WithoutMap
    void deserializeValueTypeArgMapOfString_nullCases_returnsNull(AttributeValue attributeValue) {
        ValueTypeArgMap<String> value = SchemaAttributeValueSerializer.deserializeValueTypeArgMapOfString(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @MethodSource("valueTypeArgMapsOfStringsSource")
    void deserializeValueTypeArgMapOfString_correctTypesInMap_returnsValueTypeArgMap(ValueTypeArgMap<String> valueTypeArgMap) {
        AttributeValue attributeValue = new AttributeValue().withM(valueTypeArgMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue()))));
        ValueTypeArgMap<String> value = SchemaAttributeValueSerializer.deserializeValueTypeArgMapOfString(attributeValue);
        assertThat(value).isEqualTo(valueTypeArgMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> e.getValue())));
    }

    @ParameterizedTest
    @AttributeValueSource.WithoutString
    void deserializeValueTypeArgMapOfString_incorrectTypesInMap_returnsValueTypeArgMapWithoutItems(AttributeValue attributeValue) {
        ValueTypeArgMap<String> value = SchemaAttributeValueSerializer.deserializeValueTypeArgMapOfString(new AttributeValue().withM(mapOf("key", attributeValue)));
        assertThat(value).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("valueTypeArgMapsWithNullsSource")
    void deserializeValueTypeArgMapOfString_nullsInMap_returnsValueTypeArgMapWithoutNulls(ValueTypeArgMap<String> valueTypeArgMap) {
        AttributeValue attributeValue = new AttributeValue().withM(valueTypeArgMap.entrySet().stream()
                .filter(e -> e.getKey() != null) // keys can't be null
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue()))));
        ValueTypeArgMap<String> value = SchemaAttributeValueSerializer.deserializeValueTypeArgMapOfString(attributeValue);
        assertThat(value).isEqualTo(valueTypeArgMap.entrySet().stream()
                .filter(e -> e.getKey() != null)
                .filter(e -> e.getValue() != null)
                .collect(toMap(e -> e.getKey(), e -> e.getValue())));
    }

    @ParameterizedTest
    @AttributeValueSource.WithoutString
    void deserializeValueTypeArgMapOfString_incorrectTypesInMapMultipleItems_returnsValueTypeArgMapOnlyWithCorrectTypes(AttributeValue attributeValue) {
        ValueTypeArgMap<String> value = SchemaAttributeValueSerializer.deserializeValueTypeArgMapOfString(new AttributeValue().withM(
                mapOf("present", new AttributeValue("value"), "non-present", attributeValue)));
        assertThat(value).containsExactly(entry("present", "value"));
    }

    @ParameterizedTest
    @MethodSource("valueTypeArgMapsOfStringsSource")
    void putAndGet_symmetricCases_returnsItem(ValueTypeArgMap<String> valueTypeArgMap) {
        Schema schema = schema(valueTypeArgMap);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));

        ValueTypeArgMap<String> expected = valueTypeArgMap.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> e.getValue(), (l, r) -> l, ValueTypeArgMap::new));
        assertThat(items).containsExactly(schema(expected));
    }

    @ParameterizedTest
    @MethodSource("valueTypeArgMapsWithNullsSource")
    void putAndGet_asymmetricCases_returnsItemWithoutNulls(ValueTypeArgMap<String> valueTypeArgMap) {
        Schema schema = schema(valueTypeArgMap);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));

        ValueTypeArgMap<String> expected = valueTypeArgMap.entrySet().stream()
                .filter(e -> e.getKey() != null)
                .filter(e -> e.getValue() != null)
                .collect(toMap(e -> e.getKey(), e -> e.getValue(), (l, r) -> l, ValueTypeArgMap::new));
        assertThat(items).containsExactly(schema(expected));
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(ValueTypeArgMap<String> valueTypeArgMap) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setValueTypeArgMap(valueTypeArgMap);
        return schema;
    }

    private static <V> ValueTypeArgMap<V> mapOf() {
        return new ValueTypeArgMap<>();
    }

    private static <V> ValueTypeArgMap<V> mapOf(String key1, V value1) {
        ValueTypeArgMap<V> map = new ValueTypeArgMap<>();
        map.put(key1, value1);
        return map;
    }

    private static <V> ValueTypeArgMap<V> mapOf(String key1, V value1, String key2, V value2) {
        ValueTypeArgMap<V> map = new ValueTypeArgMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

}
