package org.dynodao.processor.itest.serialization.list;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractSourceCompilingTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class ListSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializeListOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeListOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    void serializeListOfString_emptyList_returnsAttributeValueWithEmptyMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeListOfString(emptyList());
        assertThat(value).isEqualTo(new AttributeValue().withL(emptyList()));
    }

    @Test
    void serializeListOfString_singletonListWithValue_returnsAttributeValueWithSingleSerializedValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeListOfString(singletonList("value"));
        assertThat(value).isEqualTo(new AttributeValue().withL(new AttributeValue("value")));
    }

    @Test
    void serializeListOfString_singletonListWithNull_returnsAttributeValueWithNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeListOfString(singletonList(null));
        assertThat(value).isEqualTo(new AttributeValue().withL(new AttributeValue().withNULL(true)));
    }

    @Test
    void serializeListOfString_listWithMultipleValues_returnsAttributeValueWithSerializedList() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeListOfString(listOf("value1", "value2"));
        assertThat(value).isEqualTo(new AttributeValue().withL(new AttributeValue("value1"), new AttributeValue("value2")));
    }

    @Test
    void serializeListOfString_listWithSomeNullValues_returnsAttributeValueWithSerializeList() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeListOfString(listOf("value1", null));
        assertThat(value).isEqualTo(new AttributeValue().withL(new AttributeValue("value1"), new AttributeValue().withNULL(true)));
    }

    @Test
    void serializeListOfString_listWithAllNullValues_returnsAttributeValueWithSerializeList() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeListOfString(listOf(null, null));
        assertThat(value).isEqualTo(new AttributeValue().withL(new AttributeValue().withNULL(true), new AttributeValue().withNULL(true)));
    }

    @Test
    void deserializeListOfString_null_returnsNull() {
        List<String> value = SchemaAttributeValueSerializer.deserializeListOfString(null);
        assertThat(value).isNull();
    }

    @Test
    void deserializeListOfString_nullAttributeValue_returnsNull() {
        List<String> value = SchemaAttributeValueSerializer.deserializeListOfString(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    void deserializeListOfString_listValueNull_returnsNull() {
        List<String> value = SchemaAttributeValueSerializer.deserializeListOfString(new AttributeValue().withS("not list"));
        assertThat(value).isNull();
    }

    @Test
    void deserializeListOfString_emptyMap_returnsEmptyArrayList() {
        List<String> value = SchemaAttributeValueSerializer.deserializeListOfString(new AttributeValue().withL(emptyList()));
        assertThat(value)
                .isInstanceOf(ArrayList.class)
                .isEmpty();
    }

    @Test
    void deserializeListOfString_singletonListWithValue_returnsSingletonArrayList() {
        List<String> value = SchemaAttributeValueSerializer.deserializeListOfString(new AttributeValue().withL(singletonList(new AttributeValue("value"))));
        assertThat(value)
                .isInstanceOf(ArrayList.class)
                .containsExactly("value");
    }

    @Test
    void deserializeListOfString_singletonListWithNull_returnsSingletonArrayList() {
        List<String> value = SchemaAttributeValueSerializer.deserializeListOfString(new AttributeValue().withL(singletonList(null)));
        assertThat(value)
                .isInstanceOf(ArrayList.class)
                .containsOnlyNulls().hasSize(1);
    }

    @Test
    void deserializeListOfString_singletonListWithNullAttributeValue_returnsSingletonArrayList() {
        List<String> value = SchemaAttributeValueSerializer.deserializeListOfString(new AttributeValue().withL(singletonList(new AttributeValue().withNULL(true))));
        assertThat(value)
                .isInstanceOf(ArrayList.class)
                .containsOnlyNulls().hasSize(1);
    }

    @Test
    void deserializeListOfString_listWithMultipleValues_returnsArrayListWithValues() {
        List<String> value = SchemaAttributeValueSerializer.deserializeListOfString(new AttributeValue().withL(listOf(new AttributeValue("value1"), new AttributeValue("value2"))));
        assertThat(value)
                .isInstanceOf(ArrayList.class)
                .containsExactly("value1", "value2");
    }

    @Test
    void deserializeListOfString_listWithMultipleValuesSomeNull_returnsArrayListWithValueAndNull() {
        List<String> value = SchemaAttributeValueSerializer.deserializeListOfString(new AttributeValue().withL(listOf(new AttributeValue("value1"), null)));
        assertThat(value)
                .isInstanceOf(ArrayList.class)
                .containsExactly("value1", null);
    }

    @Test
    void deserializeListOfString_listWithMultipleValuesSomeNullAttributeValue_returnsArrayListWithValueAndNull() {
        List<String> value = SchemaAttributeValueSerializer.deserializeListOfString(new AttributeValue().withL(listOf(new AttributeValue("value1"), new AttributeValue().withNULL(true))));
        assertThat(value)
                .isInstanceOf(ArrayList.class)
                .containsExactly("value1", null);
    }

    @Test
    void deserializeListOfString_listWithMultipleValuesAllNull_returnsArrayListAllNull() {
        List<String> value = SchemaAttributeValueSerializer.deserializeListOfString(new AttributeValue().withL(listOf(null, null)));
        assertThat(value)
                .isInstanceOf(ArrayList.class)
                .containsExactly(null, null);
    }

    @Test
    void deserializeListOfString_listWithMultipleValuesAllNullAttributeValue_returnsArrayListAllNull() {
        List<String> value = SchemaAttributeValueSerializer.deserializeListOfString(new AttributeValue().withL(listOf(new AttributeValue().withNULL(true), new AttributeValue().withNULL(true))));
        assertThat(value)
                .isInstanceOf(ArrayList.class)
                .containsExactly(null, null);
    }

    @Test
    void deserializeListOfString_mapWithMultipleValuesAllMixedNulls_returnsArrayListAllNull() {
        List<String> value = SchemaAttributeValueSerializer.deserializeListOfString(new AttributeValue().withL(listOf(null, new AttributeValue().withNULL(true))));
        assertThat(value)
                .isInstanceOf(ArrayList.class)
                .containsExactly(null, null);
    }

    private <T> List<T> listOf(T value1, T value2) {
        return Arrays.asList(value1, value2);
    }

}
