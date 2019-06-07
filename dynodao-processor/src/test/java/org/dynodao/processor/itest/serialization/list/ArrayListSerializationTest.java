package org.dynodao.processor.itest.serialization.list;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractIntegrationTest;
import org.dynodao.processor.test.ParameterizedTestSources;
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

class ArrayListSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @Test
    void serializeArrayListOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeArrayListOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @MethodSource("arrayListsOfStringsSource")
    void serializeArrayListOfString_listCases_returnsListAttributeValue(ArrayList<String> arrayList) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeArrayListOfString(arrayList);
        assertThat(value).isEqualTo(new AttributeValue().withL(arrayList.stream()
                .map(string -> string == null ? new AttributeValue().withNULL(true) : new AttributeValue(string))
                .collect(toList())));
    }

    static Stream<ArrayList<String>> arrayListsOfStringsSource() {
        return Stream.of(listOf(), listOf("value"), listOf("value1", "value2"),
                listOf(null), listOf("value1", null), listOf(null, "value2"), listOf(null, null));
    }

    @ParameterizedTest
    @NullSource
    @ParameterizedTestSources.AttributeValuesWithoutListSource
    void deserializeArrayListOfString_nullCases_returnsNull(AttributeValue attributeValue) {
        ArrayList<String> value = SchemaAttributeValueSerializer.deserializeArrayListOfString(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @MethodSource("listsOfStringAttributeValues")
    void deserializeArrayListOfString_correctTypesInList_returnsArrayList(List<AttributeValue> attributeValueList) {
        ArrayList<String> value = SchemaAttributeValueSerializer.deserializeArrayListOfString(new AttributeValue().withL(attributeValueList));
        assertThat(value).containsExactly(attributeValueList.stream()
                .map(AttributeValue::getS)
                .toArray(String[]::new));
    }

    static Stream<List<AttributeValue>> listsOfStringAttributeValues() {
        return Stream.of(listOf(), listOf(new AttributeValue("value")), listOf(new AttributeValue("value1"), new AttributeValue("value2")));
    }

    @ParameterizedTest
    @NullSource
    @ParameterizedTestSources.AttributeValuesWithoutStringSource
    void deserializeArrayListOfString_incorrectTypesInList_returnsArrayListOfNulls(AttributeValue attributeValue) {
        ArrayList<String> value = SchemaAttributeValueSerializer.deserializeArrayListOfString(new AttributeValue().withL(listOf(attributeValue)));
        assertThat(value).containsOnlyNulls().hasSize(1);
    }

    @ParameterizedTest
    @NullSource
    @ParameterizedTestSources.AttributeValuesWithoutStringSource
    void deserializeArrayListOfString_incorrectTypesInListMultipleItems_returnsArrayListWithValueAndNull(AttributeValue attributeValue) {
        ArrayList<String> value = SchemaAttributeValueSerializer.deserializeArrayListOfString(new AttributeValue().withL(listOf(new AttributeValue("value"), attributeValue)));
        assertThat(value).containsExactly("value", null);
    }

    @ParameterizedTest
    @MethodSource("arrayListsOfStringsSource")
    void putAndGet_symmetricCases_returnsItem(ArrayList<String> arrayList) {
        Schema schema = schema(arrayList);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));
        assertThat(items).containsExactly(schema);
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(ArrayList<String> arrayList) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setArrayList(arrayList);
        return schema;
    }

    private static <T> ArrayList<T> listOf() {
        return new ArrayList<>();
    }

    private static <T> ArrayList<T> listOf(T value) {
        return new ArrayList<>(singletonList(value));
    }

    private static <T> ArrayList<T> listOf(T value1, T value2) {
        return new ArrayList<>(Arrays.asList(value1, value2));
    }

}
