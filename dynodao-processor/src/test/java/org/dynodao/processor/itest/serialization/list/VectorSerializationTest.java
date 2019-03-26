package org.dynodao.processor.itest.serialization.list;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractSourceCompilingTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Vector;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class VectorSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializeVectorOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeVectorOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    void serializeVectorOfString_emptyList_returnsAttributeValueWithEmptyMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeVectorOfString(listOf());
        assertThat(value).isEqualTo(new AttributeValue().withL(emptyList()));
    }

    @Test
    void serializeVectorOfString_singletonListWithValue_returnsAttributeValueWithSingleSerializedValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeVectorOfString(listOf("value"));
        assertThat(value).isEqualTo(new AttributeValue().withL(new AttributeValue("value")));
    }

    @Test
    void serializeVectorOfString_singletonListWithNull_returnsAttributeValueWithNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeVectorOfString(listOf(null));
        assertThat(value).isEqualTo(new AttributeValue().withL(new AttributeValue().withNULL(true)));
    }

    @Test
    void serializeVectorOfString_listWithMultipleValues_returnsAttributeValueWithSerializedList() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeVectorOfString(listOf("value1", "value2"));
        assertThat(value).isEqualTo(new AttributeValue().withL(new AttributeValue("value1"), new AttributeValue("value2")));
    }

    @Test
    void serializeVectorOfString_listWithSomeNullValues_returnsAttributeValueWithSerializeList() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeVectorOfString(listOf("value1", null));
        assertThat(value).isEqualTo(new AttributeValue().withL(new AttributeValue("value1"), new AttributeValue().withNULL(true)));
    }

    @Test
    void serializeVectorOfString_listWithAllNullValues_returnsAttributeValueWithSerializeList() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeVectorOfString(listOf(null, null));
        assertThat(value).isEqualTo(new AttributeValue().withL(new AttributeValue().withNULL(true), new AttributeValue().withNULL(true)));
    }

    @Test
    void deserializeVectorOfString_null_returnsNull() {
        Vector<String> value = SchemaAttributeValueSerializer.deserializeVectorOfString(null);
        assertThat(value).isNull();
    }

    @Test
    void deserializeVectorOfString_nullAttributeValue_returnsNull() {
        Vector<String> value = SchemaAttributeValueSerializer.deserializeVectorOfString(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    void deserializeVectorOfString_listValueNull_returnsNull() {
        Vector<String> value = SchemaAttributeValueSerializer.deserializeVectorOfString(new AttributeValue().withS("not list"));
        assertThat(value).isNull();
    }

    @Test
    void deserializeVectorOfString_emptyMap_returnsEmptyVector() {
        Vector<String> value = SchemaAttributeValueSerializer.deserializeVectorOfString(new AttributeValue().withL(emptyList()));
        assertThat(value).isEmpty();
    }

    @Test
    void deserializeVectorOfString_singletonListWithValue_returnsSingletonVector() {
        Vector<String> value = SchemaAttributeValueSerializer.deserializeVectorOfString(new AttributeValue().withL(singletonList(new AttributeValue("value"))));
        assertThat(value).containsExactly("value");
    }

    @Test
    void deserializeVectorOfString_singletonListWithNull_returnsSingletonVector() {
        Vector<String> value = SchemaAttributeValueSerializer.deserializeVectorOfString(new AttributeValue().withL(singletonList(null)));
        assertThat(value).containsOnlyNulls().hasSize(1);
    }

    @Test
    void deserializeVectorOfString_singletonListWithNullAttributeValue_returnsSingletonVector() {
        Vector<String> value = SchemaAttributeValueSerializer.deserializeVectorOfString(new AttributeValue().withL(singletonList(new AttributeValue().withNULL(true))));
        assertThat(value).containsOnlyNulls().hasSize(1);
    }

    @Test
    void deserializeVectorOfString_listWithMultipleValues_returnsVectorWithValues() {
        Vector<String> value = SchemaAttributeValueSerializer.deserializeVectorOfString(new AttributeValue().withL(listOf(new AttributeValue("value1"), new AttributeValue("value2"))));
        assertThat(value).containsExactly("value1", "value2");
    }

    @Test
    void deserializeVectorOfString_listWithMultipleValuesSomeNull_returnsVectorWithValueAndNull() {
        Vector<String> value = SchemaAttributeValueSerializer.deserializeVectorOfString(new AttributeValue().withL(listOf(new AttributeValue("value1"), null)));
        assertThat(value).containsExactly("value1", null);
    }

    @Test
    void deserializeVectorOfString_listWithMultipleValuesSomeNullAttributeValue_returnsVectorWithValueAndNull() {
        Vector<String> value = SchemaAttributeValueSerializer.deserializeVectorOfString(new AttributeValue().withL(listOf(new AttributeValue("value1"), new AttributeValue().withNULL(true))));
        assertThat(value).containsExactly("value1", null);
    }

    @Test
    void deserializeVectorOfString_listWithMultipleValuesAllNull_returnsVectorAllNull() {
        Vector<String> value = SchemaAttributeValueSerializer.deserializeVectorOfString(new AttributeValue().withL(listOf(null, null)));
        assertThat(value).containsExactly(null, null);
    }

    @Test
    void deserializeVectorOfString_listWithMultipleValuesAllNullAttributeValue_returnsVectorAllNull() {
        Vector<String> value = SchemaAttributeValueSerializer.deserializeVectorOfString(new AttributeValue().withL(listOf(new AttributeValue().withNULL(true), new AttributeValue().withNULL(true))));
        assertThat(value).containsExactly(null, null);
    }

    @Test
    void deserializeVectorOfString_mapWithMultipleValuesAllMixedNulls_returnsVectorAllNull() {
        Vector<String> value = SchemaAttributeValueSerializer.deserializeVectorOfString(new AttributeValue().withL(listOf(null, new AttributeValue().withNULL(true))));
        assertThat(value).containsExactly(null, null);
    }

    private <T> Vector<T> listOf() {
        return new Vector<>();
    }

    private <T> Vector<T> listOf(T value) {
        return new Vector<>(singletonList(value));
    }

    private <T> Vector<T> listOf(T value1, T value2) {
        return new Vector<>(Arrays.asList(value1, value2));
    }

}
