package org.dynodao.processor.itest.serialization.list;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractIntegrationTest;
import org.dynodao.processor.test.params.AttributeValueSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

class LinkedListSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @Test
    void serializeLinkedListOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeLinkedListOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @MethodSource("linkedListsOfStringsSource")
    void serializeLinkedListOfString_listCases_returnsListAttributeValue(LinkedList<String> linkedList) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeLinkedListOfString(linkedList);
        assertThat(value).isEqualTo(new AttributeValue().withL(linkedList.stream()
                .map(string -> string == null ? new AttributeValue().withNULL(true) : new AttributeValue(string))
                .collect(toList())));
    }

    static Stream<LinkedList<String>> linkedListsOfStringsSource() {
        return Stream.of(listOf(), listOf("value"), listOf("value1", "value2"),
                listOf(null), listOf("value1", null), listOf(null, "value2"), listOf(null, null));
    }

    @ParameterizedTest
    @NullSource
    @AttributeValueSource.WithoutList
    void deserializeLinkedListOfString_nullCases_returnsNull(AttributeValue attributeValue) {
        LinkedList<String> value = SchemaAttributeValueSerializer.deserializeLinkedListOfString(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @MethodSource("listsOfStringAttributeValues")
    void deserializeLinkedListOfString_correctTypesInList_returnsLinkedList(List<AttributeValue> attributeValueList) {
        LinkedList<String> value = SchemaAttributeValueSerializer.deserializeLinkedListOfString(new AttributeValue().withL(attributeValueList));
        assertThat(value).containsExactly(attributeValueList.stream()
                .map(AttributeValue::getS)
                .toArray(String[]::new));
    }

    static Stream<List<AttributeValue>> listsOfStringAttributeValues() {
        return Stream.of(listOf(), listOf(new AttributeValue("value")), listOf(new AttributeValue("value1"), new AttributeValue("value2")));
    }

    @ParameterizedTest
    @NullSource
    @AttributeValueSource.WithoutString
    void deserializeLinkedListOfString_incorrectTypesInList_returnsLinkedListOfNulls(AttributeValue attributeValue) {
        LinkedList<String> value = SchemaAttributeValueSerializer.deserializeLinkedListOfString(new AttributeValue().withL(listOf(attributeValue)));
        assertThat(value).containsOnlyNulls().hasSize(1);
    }

    @ParameterizedTest
    @NullSource
    @AttributeValueSource.WithoutString
    void deserializeLinkedListOfString_incorrectTypesInListMultipleItems_returnsLinkedListWithValueAndNull(AttributeValue attributeValue) {
        LinkedList<String> value = SchemaAttributeValueSerializer.deserializeLinkedListOfString(new AttributeValue().withL(listOf(new AttributeValue("value"), attributeValue)));
        assertThat(value).containsExactly("value", null);
    }

    @ParameterizedTest
    @MethodSource("linkedListsOfStringsSource")
    void putAndGet_symmetricCases_returnsItem(LinkedList<String> linkedList) {
        Schema schema = schema(linkedList);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));
        assertThat(items).containsExactly(schema);
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(LinkedList<String> linkedList) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setLinkedList(linkedList);
        return schema;
    }

    private static <T> LinkedList<T> listOf() {
        return new LinkedList<>();
    }

    private static <T> LinkedList<T> listOf(T value) {
        return new LinkedList<>(singletonList(value));
    }

    private static <T> LinkedList<T> listOf(T value1, T value2) {
        return new LinkedList<>(Arrays.asList(value1, value2));
    }

}
