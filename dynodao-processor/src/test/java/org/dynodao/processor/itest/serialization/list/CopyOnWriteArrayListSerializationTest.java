package org.dynodao.processor.itest.serialization.list;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractIntegrationTest;
import org.dynodao.processor.test.ParameterizedTestSources;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

class CopyOnWriteArrayListSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @Test
    void serializeCopyOnWriteArrayListOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeCopyOnWriteArrayListOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @MethodSource("copyOnWriteArrayListsOfStringsSource")
    void serializeCopyOnWriteArrayListOfString_listCases_returnsListAttributeValue(CopyOnWriteArrayList<String> copyOnWriteArrayList) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeCopyOnWriteArrayListOfString(copyOnWriteArrayList);
        assertThat(value).isEqualTo(new AttributeValue().withL(copyOnWriteArrayList.stream()
                .map(string -> string == null ? new AttributeValue().withNULL(true) : new AttributeValue(string))
                .collect(toList())));
    }

    static Stream<CopyOnWriteArrayList<String>> copyOnWriteArrayListsOfStringsSource() {
        return Stream.of(listOf(), listOf("value"), listOf("value1", "value2"),
                listOf(null), listOf("value1", null), listOf(null, "value2"), listOf(null, null));
    }

    @ParameterizedTest
    @NullSource
    @ParameterizedTestSources.AttributeValuesWithoutListSource
    void deserializeCopyOnWriteArrayListOfString_nullCases_returnsNull(AttributeValue attributeValue) {
        CopyOnWriteArrayList<String> value = SchemaAttributeValueSerializer.deserializeCopyOnWriteArrayListOfString(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @MethodSource("listsOfStringAttributeValues")
    void deserializeCopyOnWriteArrayListOfString_correctTypesInList_returnsCopyOnWriteArrayList(List<AttributeValue> attributeValueList) {
        CopyOnWriteArrayList<String> value = SchemaAttributeValueSerializer.deserializeCopyOnWriteArrayListOfString(new AttributeValue().withL(attributeValueList));
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
    void deserializeCopyOnWriteArrayListOfString_incorrectTypesInList_returnsCopyOnWriteArrayListOfNulls(AttributeValue attributeValue) {
        CopyOnWriteArrayList<String> value = SchemaAttributeValueSerializer.deserializeCopyOnWriteArrayListOfString(new AttributeValue().withL(listOf(attributeValue)));
        assertThat(value).containsOnlyNulls().hasSize(1);
    }

    @ParameterizedTest
    @NullSource
    @ParameterizedTestSources.AttributeValuesWithoutStringSource
    void deserializeCopyOnWriteArrayListOfString_incorrectTypesInListMultipleItems_returnsCopyOnWriteArrayListWithValueAndNull(AttributeValue attributeValue) {
        CopyOnWriteArrayList<String> value = SchemaAttributeValueSerializer.deserializeCopyOnWriteArrayListOfString(new AttributeValue().withL(listOf(new AttributeValue("value"), attributeValue)));
        assertThat(value).containsExactly("value", null);
    }

    @ParameterizedTest
    @MethodSource("copyOnWriteArrayListsOfStringsSource")
    void putAndGet_symmetricCases_returnsItem(CopyOnWriteArrayList<String> copyOnWriteArrayList) {
        Schema schema = schema(copyOnWriteArrayList);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));
        assertThat(items).containsExactly(schema);
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(CopyOnWriteArrayList<String> copyOnWriteArrayList) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setCopyOnWriteArrayList(copyOnWriteArrayList);
        return schema;
    }

    private static <T> CopyOnWriteArrayList<T> listOf() {
        return new CopyOnWriteArrayList<>();
    }

    private static <T> CopyOnWriteArrayList<T> listOf(T value) {
        return new CopyOnWriteArrayList<>(singletonList(value));
    }

    private static <T> CopyOnWriteArrayList<T> listOf(T value1, T value2) {
        return new CopyOnWriteArrayList<>(Arrays.asList(value1, value2));
    }

}
