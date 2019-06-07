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

class NoTypeArgsListSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @Test
    void serializeNoTypeArgsList_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeNoTypeArgsList(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @MethodSource("noTypeArgsListsOfStringsSource")
    void serializeNoTypeArgsList_listCases_returnsListAttributeValue(NoTypeArgsList noTypeArgsList) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeNoTypeArgsList(noTypeArgsList);
        assertThat(value).isEqualTo(new AttributeValue().withL(noTypeArgsList.stream()
                .map(string -> string == null ? new AttributeValue().withNULL(true) : new AttributeValue(string))
                .collect(toList())));
    }

    static Stream<NoTypeArgsList> noTypeArgsListsOfStringsSource() {
        return Stream.of(listOf(), listOf("value"), listOf("value1", "value2"),
                listOf(null), listOf("value1", null), listOf(null, "value2"), listOf(null, null));
    }

    @ParameterizedTest
    @NullSource
    @ParameterizedTestSources.AttributeValuesWithoutListSource
    void deserializeNoTypeArgsList_nullCases_returnsNull(AttributeValue attributeValue) {
        NoTypeArgsList value = SchemaAttributeValueSerializer.deserializeNoTypeArgsList(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @MethodSource("listsOfStringAttributeValues")
    void deserializeNoTypeArgsList_correctTypesInList_returnsNoTypeArgsList(List<AttributeValue> attributeValueList) {
        NoTypeArgsList value = SchemaAttributeValueSerializer.deserializeNoTypeArgsList(new AttributeValue().withL(attributeValueList));
        assertThat(value).containsExactly(attributeValueList.stream()
                .map(AttributeValue::getS)
                .toArray(String[]::new));
    }

    static Stream<List<AttributeValue>> listsOfStringAttributeValues() {
        return Stream.of(arrayListOf(), arrayListOf(new AttributeValue("value")), arrayListOf(new AttributeValue("value1"), new AttributeValue("value2")));
    }

    @ParameterizedTest
    @NullSource
    @ParameterizedTestSources.AttributeValuesWithoutStringSource
    void deserializeNoTypeArgsList_incorrectTypesInList_returnsNoTypeArgsListOfNulls(AttributeValue attributeValue) {
        NoTypeArgsList value = SchemaAttributeValueSerializer.deserializeNoTypeArgsList(new AttributeValue().withL(arrayListOf(attributeValue)));
        assertThat(value).containsOnlyNulls().hasSize(1);
    }

    @ParameterizedTest
    @NullSource
    @ParameterizedTestSources.AttributeValuesWithoutStringSource
    void deserializeNoTypeArgsList_incorrectTypesInListMultipleItems_returnsNoTypeArgsListWithValueAndNull(AttributeValue attributeValue) {
        NoTypeArgsList value = SchemaAttributeValueSerializer.deserializeNoTypeArgsList(new AttributeValue().withL(arrayListOf(new AttributeValue("value"), attributeValue)));
        assertThat(value).containsExactly("value", null);
    }

    @ParameterizedTest
    @MethodSource("noTypeArgsListsOfStringsSource")
    void putAndGet_symmetricCases_returnsItem(NoTypeArgsList noTypeArgsList) {
        Schema schema = schema(noTypeArgsList);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));
        assertThat(items).containsExactly(schema);
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(NoTypeArgsList noTypeArgsList) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setNoTypeArgsList(noTypeArgsList);
        return schema;
    }

    private static NoTypeArgsList listOf() {
        return new NoTypeArgsList();
    }

    private static NoTypeArgsList listOf(String value) {
        NoTypeArgsList list = new NoTypeArgsList();
        list.add(value);
        return list;
    }

    private static NoTypeArgsList listOf(String value1, String value2) {
        NoTypeArgsList list = new NoTypeArgsList();
        list.add(value1);
        list.add(value2);
        return list;
    }

    private static <T> List<T> arrayListOf() {
        return new ArrayList<>();
    }

    private static <T> List<T> arrayListOf(T value) {
        return new ArrayList<>(singletonList(value));
    }

    private static <T> List<T> arrayListOf(T value1, T value2) {
        return new ArrayList<>(Arrays.asList(value1, value2));
    }

}
