package org.dynodao.processor.itest.serialization.list;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractIntegrationTest;
import org.dynodao.processor.test.params.AttributeValueSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

class ListSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @Test
    void serializeListOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeListOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @MethodSource("listsOfStringsSource")
    void serializeListOfString_listCases_returnsListAttributeValue(List<String> list) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeListOfString(list);
        assertThat(value).isEqualTo(new AttributeValue().withL(list.stream()
                .map(string -> string == null ? new AttributeValue().withNULL(true) : new AttributeValue(string))
                .collect(toList())));
    }

    static Stream<List<String>> listsOfStringsSource() {
        return Stream.of(listOf(), listOf("value"), listOf("value1", "value2"),
                listOf(null), listOf("value1", null), listOf(null, "value2"), listOf(null, null));
    }

    @ParameterizedTest
    @NullSource
    @AttributeValueSource.WithoutList
    void deserializeListOfString_nullCases_returnsNull(AttributeValue attributeValue) {
        List<String> value = SchemaAttributeValueSerializer.deserializeListOfString(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @MethodSource("listsOfStringAttributeValues")
    void deserializeListOfString_correctTypesInList_returnsList(List<AttributeValue> attributeValueList) {
        List<String> value = SchemaAttributeValueSerializer.deserializeListOfString(new AttributeValue().withL(attributeValueList));
        assertThat(value)
                .isInstanceOf(ArrayList.class)
                .containsExactly(attributeValueList.stream()
                    .map(AttributeValue::getS)
                    .toArray(String[]::new));
    }

    static Stream<List<AttributeValue>> listsOfStringAttributeValues() {
        return Stream.of(listOf(), listOf(new AttributeValue("value")), listOf(new AttributeValue("value1"), new AttributeValue("value2")));
    }

    @ParameterizedTest
    @NullSource
    @AttributeValueSource.WithoutString
    void deserializeListOfString_incorrectTypesInList_returnsListOfNulls(AttributeValue attributeValue) {
        List<String> value = SchemaAttributeValueSerializer.deserializeListOfString(new AttributeValue().withL(listOf(attributeValue)));
        assertThat(value)
                .isInstanceOf(ArrayList.class)
                .containsOnlyNulls().hasSize(1);
    }

    @ParameterizedTest
    @NullSource
    @AttributeValueSource.WithoutString
    void deserializeListOfString_incorrectTypesInListMultipleItems_returnsListWithValueAndNull(AttributeValue attributeValue) {
        List<String> value = SchemaAttributeValueSerializer.deserializeListOfString(new AttributeValue().withL(listOf(new AttributeValue("value"), attributeValue)));
        assertThat(value)
                .isInstanceOf(ArrayList.class)
                .containsExactly("value", null);
    }

    @ParameterizedTest
    @MethodSource("listsOfStringsSource")
    void putAndGet_symmetricCases_returnsItem(List<String> list) {
        Schema schema = schema(list);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));
        assertThat(items).containsExactly(schema);
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(List<String> list) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setList(list);
        return schema;
    }

    private static <T> List<T> listOf() {
        return new ArrayList<>();
    }

    private static <T> List<T> listOf(T value) {
        return new ArrayList<>(singletonList(value));
    }

    private static <T> List<T> listOf(T value1, T value2) {
        return new ArrayList<>(Arrays.asList(value1, value2));
    }

}
