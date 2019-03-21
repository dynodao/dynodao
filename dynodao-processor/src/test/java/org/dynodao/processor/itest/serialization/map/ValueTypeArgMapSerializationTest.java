package org.dynodao.processor.itest.serialization.map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractSourceCompilingTest;
import org.junit.jupiter.api.Test;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

class ValueTypeArgMapSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializeValueTypeArgMapOfString_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeValueTypeArgMapOfString(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    void serializeValueTypeArgMapOfString_emptyMap_returnsAttributeValueWithEmptyMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeValueTypeArgMapOfString(mapOf());
        assertThat(value).isEqualTo(new AttributeValue().withM(emptyMap()));
    }

    @Test
    void serializeValueTypeArgMapOfString_singletonMapWithValue_returnsAttributeValueWithSingleValueSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeValueTypeArgMapOfString(mapOf("key", "value"));
        assertThat(value).isEqualTo(new AttributeValue().withM(singletonMap("key", new AttributeValue("value"))));
    }

    @Test
    void serializeValueTypeArgMapOfString_singletonMapWithNullValue_returnsAttributeValueWithSingleNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeValueTypeArgMapOfString(mapOf("key", null));
        assertThat(value).isEqualTo(new AttributeValue().withM(singletonMap("key", new AttributeValue().withNULL(true))));
    }

    @Test
    void serializeValueTypeArgMapOfString_mapWithMultipleValues_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeValueTypeArgMapOfString(mapOf("key1", "value1", "key2", "value2"));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue("value2"))));
    }

    @Test
    void serializeValueTypeArgMapOfString_mapWithMultipleValuesSomeNull_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeValueTypeArgMapOfString(mapOf("key1", "value1", "key2", null));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue().withNULL(true))));
    }

    @Test
    void serializeValueTypeArgMapOfString_mapWithMultipleValuesAllNull_returnsAttributeValueWithSerializedMap() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeValueTypeArgMapOfString(mapOf("key1", null, "key2", null));
        assertThat(value).isEqualTo(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue().withNULL(true),
                "key2", new AttributeValue().withNULL(true))));
    }

    @Test
    void deserializeValueTypeArgMapOfString_null_returnsNull() {
        ValueTypeArgMap<String> value = SchemaAttributeValueSerializer.deserializeValueTypeArgMapOfString(null);
        assertThat(value).isNull();
    }

    @Test
    void deserializeValueTypeArgMapOfString_nullAttributeValue_returnsNull() {
        ValueTypeArgMap<String> value = SchemaAttributeValueSerializer.deserializeValueTypeArgMapOfString(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    void deserializeValueTypeArgMapOfString_mapValueNull_returnsNull() {
        ValueTypeArgMap<String> value = SchemaAttributeValueSerializer.deserializeValueTypeArgMapOfString(new AttributeValue().withS("string"));
        assertThat(value).isNull();
    }

    @Test
    void deserializeValueTypeArgMapOfString_emptyMap_returnsEmptyValueTypeArgMap() {
        ValueTypeArgMap<String> value = SchemaAttributeValueSerializer.deserializeValueTypeArgMapOfString(new AttributeValue().withM(emptyMap()));
        assertThat(value).isEmpty();
    }

    @Test
    void deserializeValueTypeArgMapOfString_singletonMapWithValue_returnsSingletonValueTypeArgMap() {
        ValueTypeArgMap<String> value = SchemaAttributeValueSerializer.deserializeValueTypeArgMapOfString(new AttributeValue().withM(singletonMap("key", new AttributeValue("value"))));
        assertThat(value).containsOnly(entry("key", "value"));
    }

    @Test
    void deserializeValueTypeArgMapOfString_singletonMapWithNull_returnsSingletonValueTypeArgMap() {
        ValueTypeArgMap<String> value = SchemaAttributeValueSerializer.deserializeValueTypeArgMapOfString(new AttributeValue().withM(singletonMap("key", null)));
        assertThat(value).containsOnly(entry("key", null));
    }

    @Test
    void deserializeValueTypeArgMapOfString_singletonMapWithNullAttributeValue_returnsSingletonValueTypeArgMap() {
        ValueTypeArgMap<String> value = SchemaAttributeValueSerializer.deserializeValueTypeArgMapOfString(new AttributeValue().withM(singletonMap("key", new AttributeValue().withNULL(true))));
        assertThat(value).containsOnly(entry("key", null));
    }

    @Test
    void deserializeValueTypeArgMapOfString_mapWithMultipleValues_returnsValueTypeArgMapWithValues() {
        ValueTypeArgMap<String> value = SchemaAttributeValueSerializer.deserializeValueTypeArgMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue("value2"))));
        assertThat(value).containsOnly(entry("key1", "value1"), entry("key2", "value2"));
    }

    @Test
    void deserializeValueTypeArgMapOfString_mapWithMultipleValuesSomeNull_returnsValueTypeArgMapWithValueAndNull() {
        ValueTypeArgMap<String> value = SchemaAttributeValueSerializer.deserializeValueTypeArgMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", null)));
        assertThat(value).containsOnly(entry("key1", "value1"), entry("key2", null));
    }

    @Test
    void deserializeValueTypeArgMapOfString_mapWithMultipleValuesSomeNullAttributeValue_returnsValueTypeArgMapWithValueAndNull() {
        ValueTypeArgMap<String> value = SchemaAttributeValueSerializer.deserializeValueTypeArgMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue("value1"),
                "key2", new AttributeValue().withNULL(true))));
        assertThat(value).containsOnly(entry("key1", "value1"), entry("key2", null));
    }

    @Test
    void deserializeValueTypeArgMapOfString_mapWithMultipleValuesAllNull_returnsValueTypeArgMapAllNull() {
        ValueTypeArgMap<String> value = SchemaAttributeValueSerializer.deserializeValueTypeArgMapOfString(new AttributeValue().withM(mapOf(
                "key1", null,
                "key2", null)));
        assertThat(value).containsOnly(entry("key1", null), entry("key2", null));
    }

    @Test
    void deserializeValueTypeArgMapOfString_mapWithMultipleValuesAllNullAttributeValue_returnsValueTypeArgMapAllNull() {
        ValueTypeArgMap<String> value = SchemaAttributeValueSerializer.deserializeValueTypeArgMapOfString(new AttributeValue().withM(mapOf(
                "key1", new AttributeValue().withNULL(true),
                "key2", new AttributeValue().withNULL(true))));
        assertThat(value).containsOnly(entry("key1", null), entry("key2", null));
    }

    @Test
    void deserializeValueTypeArgMapOfString_mapWithMultipleValuesAllMixedNulls_returnsValueTypeArgMapAllNull() {
        ValueTypeArgMap<String> value = SchemaAttributeValueSerializer.deserializeValueTypeArgMapOfString(new AttributeValue().withM(mapOf(
                "key1", null,
                "key2", new AttributeValue().withNULL(true))));
        assertThat(value).containsOnly(entry("key1", null), entry("key2", null));
    }

    private <V> ValueTypeArgMap<V> mapOf() {
        return new ValueTypeArgMap<>();
    }

    private <V> ValueTypeArgMap<V> mapOf(String key1, V value1) {
        ValueTypeArgMap<V> map = new ValueTypeArgMap<>();
        map.put(key1, value1);
        return map;
    }

    private <V> ValueTypeArgMap<V> mapOf(String key1, V value1, String key2, V value2) {
        ValueTypeArgMap<V> map = new ValueTypeArgMap<>();
        map.put(key1, value1);
        map.put(key2, value2);
        return map;
    }

}
