package org.dynodao.processor.itest.serialization.map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractIntegrationTest;
import org.dynodao.processor.test.params.AttributeValueSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.Hashtable;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class HashtableSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @Test
    void serializeHashtableOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeHashtableOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @MethodSource("hashtablesOfStringsSource")
    void serializeHashtableOfString_mapCases_returnsMapAttributeValue(Hashtable<String, String> hashtable) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeHashtableOfString(hashtable);
        assertThat(value).isEqualTo(new AttributeValue().withM(hashtable.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue())))));
    }

    static Stream<Hashtable<String, String>> hashtablesOfStringsSource() {
        return Stream.of(mapOf(), mapOf("key", "value"), mapOf("key1", "value1", "key2", "value2"));
    }

    @ParameterizedTest
    @NullSource
    @AttributeValueSource.WithoutMap
    void deserializeHashtableOfString_nullCases_returnsNull(AttributeValue attributeValue) {
        Hashtable<String, String> value = SchemaAttributeValueSerializer.deserializeHashtableOfString(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @MethodSource("hashtablesOfStringsSource")
    void deserializeHashtableOfString_correctTypesInMap_returnsHashtable(Hashtable<String, String> hashtable) {
        AttributeValue attributeValue = new AttributeValue().withM(hashtable.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> new AttributeValue(e.getValue()))));
        Hashtable<String, String> value = SchemaAttributeValueSerializer.deserializeHashtableOfString(attributeValue);
        assertThat(value).isEqualTo(hashtable.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> e.getValue())));
    }

    @ParameterizedTest
    @AttributeValueSource.WithoutString
    void deserializeHashtableOfString_incorrectTypesInMap_returnsHashtableWithoutItems(AttributeValue attributeValue) {
        Hashtable<String, String> value = SchemaAttributeValueSerializer.deserializeHashtableOfString(new AttributeValue().withM(mapOf("key", attributeValue)));
        assertThat(value).isEmpty();
    }

    @ParameterizedTest
    @AttributeValueSource.WithoutString
    void deserializeHashtableOfString_incorrectTypesInMapMultipleItems_returnsHashtableOnlyWithCorrectTypes(AttributeValue attributeValue) {
        Hashtable<String, String> value = SchemaAttributeValueSerializer.deserializeHashtableOfString(new AttributeValue().withM(
                mapOf("present", new AttributeValue("value"), "non-present", attributeValue)));
        assertThat(value).containsExactly(entry("present", "value"));
    }

    @ParameterizedTest
    @MethodSource("hashtablesOfStringsSource")
    void putAndGet_symmetricCases_returnsItem(Hashtable<String, String> hashtable) {
        Schema schema = schema(hashtable);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));

        Hashtable<String, String> expected = hashtable.entrySet().stream()
                .collect(toMap(e -> e.getKey(), e -> e.getValue(), (l, r) -> l, Hashtable::new));
        assertThat(items).containsExactly(schema(expected));
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(Hashtable<String, String> hashtable) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setHashtable(hashtable);
        return schema;
    }

    private static <K, V> Hashtable<K, V> mapOf() {
        return new Hashtable<>();
    }

    private static <K, V> Hashtable<K, V> mapOf(K key1, V value1) {
        Hashtable<K, V> map = new Hashtable<>();
        map.put(key1, value1);
        return map;
    }

    private static <K, V> Hashtable<K, V> mapOf(K key1, V value1, K key2, V value2) {
        Hashtable<K, V> map = new Hashtable<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

}
