package com.github.dynodao.processor.itest.serialization.list;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.github.dynodao.processor.itest.AbstractIntegrationTest;
import com.github.dynodao.processor.test.params.AttributeValueSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

class VectorSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @Test
    void serializeVectorOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeVectorOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @MethodSource("vectorsOfStringsSource")
    void serializeVectorOfString_listCases_returnsListAttributeValue(Vector<String> vector) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeVectorOfString(vector);
        assertThat(value).isEqualTo(new AttributeValue().withL(vector.stream()
                .map(string -> string == null ? new AttributeValue().withNULL(true) : new AttributeValue(string))
                .collect(toList())));
    }

    static Stream<Vector<String>> vectorsOfStringsSource() {
        return Stream.of(listOf(), listOf("value"), listOf("value1", "value2"),
                listOf(null), listOf("value1", null), listOf(null, "value2"), listOf(null, null));
    }

    @ParameterizedTest
    @NullSource
    @AttributeValueSource.WithoutList
    void deserializeVectorOfString_nullCases_returnsNull(AttributeValue attributeValue) {
        Vector<String> value = SchemaAttributeValueSerializer.deserializeVectorOfString(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @MethodSource("listsOfStringAttributeValues")
    void deserializeVectorOfString_correctTypesInList_returnsVector(List<AttributeValue> attributeValueList) {
        Vector<String> value = SchemaAttributeValueSerializer.deserializeVectorOfString(new AttributeValue().withL(attributeValueList));
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
    void deserializeVectorOfString_incorrectTypesInList_returnsVectorOfNulls(AttributeValue attributeValue) {
        Vector<String> value = SchemaAttributeValueSerializer.deserializeVectorOfString(new AttributeValue().withL(listOf(attributeValue)));
        assertThat(value).containsOnlyNulls().hasSize(1);
    }

    @ParameterizedTest
    @NullSource
    @AttributeValueSource.WithoutString
    void deserializeVectorOfString_incorrectTypesInListMultipleItems_returnsVectorWithValueAndNull(AttributeValue attributeValue) {
        Vector<String> value = SchemaAttributeValueSerializer.deserializeVectorOfString(new AttributeValue().withL(listOf(new AttributeValue("value"), attributeValue)));
        assertThat(value).containsExactly("value", null);
    }

    @ParameterizedTest
    @MethodSource("vectorsOfStringsSource")
    void putAndGet_symmetricCases_returnsItem(Vector<String> vector) {
        Schema schema = schema(vector);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));
        assertThat(items).containsExactly(schema);
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(Vector<String> Vector) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setVector(Vector);
        return schema;
    }

    private static <T> Vector<T> listOf() {
        return new Vector<>();
    }

    private static <T> Vector<T> listOf(T value) {
        return new Vector<>(singletonList(value));
    }

    private static <T> Vector<T> listOf(T value1, T value2) {
        return new Vector<>(Arrays.asList(value1, value2));
    }

}
