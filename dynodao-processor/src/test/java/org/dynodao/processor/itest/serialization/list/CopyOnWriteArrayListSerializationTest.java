package org.dynodao.processor.itest.serialization.list;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractSourceCompilingTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class CopyOnWriteArrayListSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializeCopyOnWriteArrayListOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeCopyOnWriteArrayListOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    void serializeCopyOnWriteArrayListOfString_emptyList_returnsAttributeValueWithEmptyMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeCopyOnWriteArrayListOfString(listOf());
        assertThat(value).isEqualTo(new AttributeValue().withL(emptyList()));
    }

    @Test
    void serializeCopyOnWriteArrayListOfString_singletonListWithValue_returnsAttributeValueWithSingleSerializedValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeCopyOnWriteArrayListOfString(listOf("value"));
        assertThat(value).isEqualTo(new AttributeValue().withL(new AttributeValue("value")));
    }

    @Test
    void serializeCopyOnWriteArrayListOfString_singletonListWithNull_returnsAttributeValueWithNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeCopyOnWriteArrayListOfString(listOf(null));
        assertThat(value).isEqualTo(new AttributeValue().withL(new AttributeValue().withNULL(true)));
    }

    @Test
    void serializeCopyOnWriteArrayListOfString_listWithMultipleValues_returnsAttributeValueWithSerializedList() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeCopyOnWriteArrayListOfString(listOf("value1", "value2"));
        assertThat(value).isEqualTo(new AttributeValue().withL(new AttributeValue("value1"), new AttributeValue("value2")));
    }

    @Test
    void serializeCopyOnWriteArrayListOfString_listWithSomeNullValues_returnsAttributeValueWithSerializeList() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeCopyOnWriteArrayListOfString(listOf("value1", null));
        assertThat(value).isEqualTo(new AttributeValue().withL(new AttributeValue("value1"), new AttributeValue().withNULL(true)));
    }

    @Test
    void serializeCopyOnWriteArrayListOfString_listWithAllNullValues_returnsAttributeValueWithSerializeList() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeCopyOnWriteArrayListOfString(listOf(null, null));
        assertThat(value).isEqualTo(new AttributeValue().withL(new AttributeValue().withNULL(true), new AttributeValue().withNULL(true)));
    }

    @Test
    void deserializeCopyOnWriteArrayListOfString_null_returnsNull() {
        CopyOnWriteArrayList<String> value = SchemaAttributeValueSerializer.deserializeCopyOnWriteArrayListOfString(null);
        assertThat(value).isNull();
    }

    @Test
    void deserializeCopyOnWriteArrayListOfString_nullAttributeValue_returnsNull() {
        CopyOnWriteArrayList<String> value = SchemaAttributeValueSerializer.deserializeCopyOnWriteArrayListOfString(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    void deserializeCopyOnWriteArrayListOfString_listValueNull_returnsNull() {
        CopyOnWriteArrayList<String> value = SchemaAttributeValueSerializer.deserializeCopyOnWriteArrayListOfString(new AttributeValue().withS("not list"));
        assertThat(value).isNull();
    }

    @Test
    void deserializeCopyOnWriteArrayListOfString_emptyMap_returnsEmptyCopyOnWriteArrayList() {
        CopyOnWriteArrayList<String> value = SchemaAttributeValueSerializer.deserializeCopyOnWriteArrayListOfString(new AttributeValue().withL(emptyList()));
        assertThat(value).isEmpty();
    }

    @Test
    void deserializeLinkedListOfString_singletonListWithValue_returnsSingletonCopyOnWriteArrayList() {
        CopyOnWriteArrayList<String> value = SchemaAttributeValueSerializer.deserializeCopyOnWriteArrayListOfString(new AttributeValue().withL(singletonList(new AttributeValue("value"))));
        assertThat(value).containsExactly("value");
    }

    @Test
    void deserializeCopyOnWriteArrayListOfString_singletonListWithNull_returnsSingletonCopyOnWriteArrayList() {
        CopyOnWriteArrayList<String> value = SchemaAttributeValueSerializer.deserializeCopyOnWriteArrayListOfString(new AttributeValue().withL(singletonList(null)));
        assertThat(value).containsOnlyNulls().hasSize(1);
    }

    @Test
    void deserializeCopyOnWriteArrayListOfString_singletonListWithNullAttributeValue_returnsSingletonCopyOnWriteArrayList() {
        CopyOnWriteArrayList<String> value = SchemaAttributeValueSerializer.deserializeCopyOnWriteArrayListOfString(new AttributeValue().withL(singletonList(new AttributeValue().withNULL(true))));
        assertThat(value).containsOnlyNulls().hasSize(1);
    }

    @Test
    void deserializeCopyOnWriteArrayListOfString_listWithMultipleValues_returnsCopyOnWriteArrayListWithValues() {
        CopyOnWriteArrayList<String> value = SchemaAttributeValueSerializer.deserializeCopyOnWriteArrayListOfString(new AttributeValue().withL(listOf(new AttributeValue("value1"), new AttributeValue("value2"))));
        assertThat(value).containsExactly("value1", "value2");
    }

    @Test
    void deserializeCopyOnWriteArrayListOfString_listWithMultipleValuesSomeNull_returnsCopyOnWriteArrayListWithValueAndNull() {
        CopyOnWriteArrayList<String> value = SchemaAttributeValueSerializer.deserializeCopyOnWriteArrayListOfString(new AttributeValue().withL(listOf(new AttributeValue("value1"), null)));
        assertThat(value).containsExactly("value1", null);
    }

    @Test
    void deserializeCopyOnWriteArrayListOfString_listWithMultipleValuesSomeNullAttributeValue_returnsCopyOnWriteArrayListWithValueAndNull() {
        CopyOnWriteArrayList<String> value = SchemaAttributeValueSerializer.deserializeCopyOnWriteArrayListOfString(new AttributeValue().withL(listOf(new AttributeValue("value1"), new AttributeValue().withNULL(true))));
        assertThat(value).containsExactly("value1", null);
    }

    @Test
    void deserializeCopyOnWriteArrayListOfString_listWithMultipleValuesAllNull_returnsCopyOnWriteArrayListAllNull() {
        CopyOnWriteArrayList<String> value = SchemaAttributeValueSerializer.deserializeCopyOnWriteArrayListOfString(new AttributeValue().withL(listOf(null, null)));
        assertThat(value).containsExactly(null, null);
    }

    @Test
    void deserializeCopyOnWriteArrayListOfString_listWithMultipleValuesAllNullAttributeValue_returnsCopyOnWriteArrayListAllNull() {
        CopyOnWriteArrayList<String> value = SchemaAttributeValueSerializer.deserializeCopyOnWriteArrayListOfString(new AttributeValue().withL(listOf(new AttributeValue().withNULL(true), new AttributeValue().withNULL(true))));
        assertThat(value).containsExactly(null, null);
    }

    @Test
    void deserializeCopyOnWriteArrayListOfString_mapWithMultipleValuesAllMixedNulls_returnsCopyOnWriteArrayListAllNull() {
        CopyOnWriteArrayList<String> value = SchemaAttributeValueSerializer.deserializeCopyOnWriteArrayListOfString(new AttributeValue().withL(listOf(null, new AttributeValue().withNULL(true))));
        assertThat(value).containsExactly(null, null);
    }

    private <T> CopyOnWriteArrayList<T> listOf() {
        return new CopyOnWriteArrayList<>();
    }

    private <T> CopyOnWriteArrayList<T> listOf(T value) {
        return new CopyOnWriteArrayList<>(singletonList(value));
    }

    private <T> CopyOnWriteArrayList<T> listOf(T value1, T value2) {
        return new CopyOnWriteArrayList<>(Arrays.asList(value1, value2));
    }

}
