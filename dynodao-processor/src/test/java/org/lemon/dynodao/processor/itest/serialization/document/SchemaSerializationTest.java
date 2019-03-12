package org.lemon.dynodao.processor.itest.serialization.document;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.junit.jupiter.api.Test;
import org.lemon.dynodao.processor.itest.AbstractSourceCompilingTest;

import java.util.Map;
import java.util.TreeMap;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

class SchemaSerializationTest extends AbstractSourceCompilingTest {

    @Test
    void serializeSchema_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeSchema(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    void serializeSchema_allFieldsPresent_returnsMapAttributeValueWithOverrideAttributeNames() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeSchema(schema("key", true, true, "a", document("a1", "a2", "a3")));
        assertThat(value).isEqualTo(schemaAttributeValue("key", true, true, "a", document("a1", "a2", "a3")));
    }

    @Test
    void serializeSchema_someFieldsNull_returnsMapAttributeValueWithNulls() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeSchema(schema("key", true, null, null, null));
        assertThat(value).isEqualTo(schemaAttributeValue("key", true, null, null, null));
    }

    @Test
    void serializeSchema_allObjectFieldsNull_returnsMapAttributeValueWithNulls() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeSchema(schema(null, true, null, null, null));
        assertThat(value).isEqualTo(schemaAttributeValue(null, true, null, null, null));
    }

    @Test
    void serializeSchemaAsItem_null_returnsEmptyMap() {
        Map<String, AttributeValue> item = SchemaAttributeValueSerializer.serializeSchemaAsItem(null);
        assertThat(item).isEmpty();
    }

    @Test
    void serializeSchemaAsItem_allFieldsPresent_returnsMapWithOverrideAttributeNames() {
        Map<String, AttributeValue> item = SchemaAttributeValueSerializer.serializeSchemaAsItem(schema("key", true, true, "a", document("a1", "a2", "a3")));
        assertThat(item).isEqualTo(schemaItem("key", true, true, "a", document("a1", "a2", "a3")));
    }

    @Test
    void serializeSchemaAsItem_someFieldsNull_returnsMapWithNulls() {
        Map<String, AttributeValue> item = SchemaAttributeValueSerializer.serializeSchemaAsItem(schema("key", true, null, null, null));
        assertThat(item).isEqualTo(schemaItem("key", true, null, null, null));
    }

    @Test
    void serializeSchemaAsItem_allObjectFieldsNull_returnsMaWithNulls() {
        Map<String, AttributeValue> item = SchemaAttributeValueSerializer.serializeSchemaAsItem(schema(null, true, null, null, null));
        assertThat(item).isEqualTo(schemaItem(null, true, null, null, null));
    }

    @Test
    void deserializeSchema_null_returnsNull() {
        Schema value = SchemaAttributeValueSerializer.deserializeSchema(null);
        assertThat(value).isNull();
    }

    @Test
    void deserializeSchema_nullAttributeValue_returnsNull() {
        Schema value = SchemaAttributeValueSerializer.deserializeSchema(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    void deserializeSchema_mapValueNull_returnsNull() {
        Schema value = SchemaAttributeValueSerializer.deserializeSchema(new AttributeValue().withS("not map"));
        assertThat(value).isNull();
    }

    @Test
    void deserializeSchema_mapAttributeValue_returnsSchema() {
        Schema value = SchemaAttributeValueSerializer.deserializeSchema(schemaAttributeValue("key", true, true, "a", document("a1", "a2", "a3")));
        assertThat(value).isEqualTo(schema("key", true, true, "a", document("a1", "a2", "a3")));
    }

    @Test
    void deserializeSchema_emptyMap_returnsDocumentWithNullFields() {
        Schema value = SchemaAttributeValueSerializer.deserializeSchema(new AttributeValue().withM(emptyMap()));
        assertThat(value).isEqualTo(schema(null, false, null, null, null));
    }

    @Test
    void deserializeSchema_valueHaveWrongKeys_returnsDocumentWithNullFields() {
        Schema value = SchemaAttributeValueSerializer.deserializeSchema(new AttributeValue().withM(singletonMap("dynamoNameIsAttribute", new AttributeValue("a"))));
        assertThat(value).isEqualTo(schema(null, false, null, null, null));
    }

    @Test
    void deserializeSchema_someKeysCorrect_returnsDocumentWithNullForMissingFieldsOnly() {
        Schema value = SchemaAttributeValueSerializer.deserializeSchema(new AttributeValue().withM(singletonMap("hashKey", new AttributeValue("key"))));
        assertThat(value).isEqualTo(schema("key", false, null, null, null));
    }

    @Test
    void deserializeSchema_keysHaveWrongTypes_returnsDocumentWithNullFields() {
        Schema value = SchemaAttributeValueSerializer.deserializeSchema(new AttributeValue().withM(singletonMap("hashKey", new AttributeValue().withM(emptyMap()))));
        assertThat(value).isEqualTo(schema(null, false, null, null, null));
    }

    @Test
    void deserializeSchemaFromItem_null_returnsNull() {
        Schema value = SchemaAttributeValueSerializer.deserializeSchemaFromItem(null);
        assertThat(value).isNull();
    }

    @Test
    void deserializeSchemaFromItem_allFieldsPresent_returnsSchema() {
        Schema value = SchemaAttributeValueSerializer.deserializeSchemaFromItem(schemaItem("key", true, true, "a", document("a1", "a2", "a3")));
        assertThat(value).isEqualTo(schema("key", true, true, "a", document("a1", "a2", "a3")));
    }

    @Test
    void deserializeSchemaFromItem_emptyMap_returnsDocumentWithNullFields() {
        Schema value = SchemaAttributeValueSerializer.deserializeSchemaFromItem(emptyMap());
        assertThat(value).isEqualTo(schema(null, false, null, null, null));
    }

    @Test
    void deserializeSchemaFromItem_valueHaveWrongKeys_returnsDocumentWithNullFields() {
        Schema value = SchemaAttributeValueSerializer.deserializeSchemaFromItem(singletonMap("dynamoNameIsAttribute", new AttributeValue("a")));
        assertThat(value).isEqualTo(schema(null, false, null, null, null));
    }

    @Test
    void deserializeSchemaFromItem_someKeysCorrect_returnsDocumentWithNullForMissingFieldsOnly() {
        Schema value = SchemaAttributeValueSerializer.deserializeSchemaFromItem(singletonMap("hashKey", new AttributeValue("key")));
        assertThat(value).isEqualTo(schema("key", false, null, null, null));
    }

    @Test
    void deserializeSchemaFromItem_keysHaveWrongTypes_returnsDocumentWithNullFields() {
        Schema value = SchemaAttributeValueSerializer.deserializeSchemaFromItem(singletonMap("hashKey", new AttributeValue().withM(emptyMap())));
        assertThat(value).isEqualTo(schema(null, false, null, null, null));
    }

    private Schema schema(String hashKey, boolean bool, Boolean boolObj, String attribute, Document document) {
        Schema schema = new Schema();
        schema.setHashKey(hashKey);
        schema.setBool(bool);
        schema.setBoolObj(boolObj);
        schema.setDynamoNameIsAttribute(attribute);
        schema.setDocument(document);
        return schema;
    }

    private Document document(String attribute1, String attribute2, String attribute3) {
        Document document = new Document();
        document.setAttribute1(attribute1);
        document.setAttribute2(attribute2);
        document.setDynamoNameIsAttribute3(attribute3);
        return document;
    }

    private AttributeValue schemaAttributeValue(String hashKey, boolean bool, Boolean boolObj, String attribute, Document document) {
        return new AttributeValue().withM(schemaItem(hashKey, bool, boolObj, attribute, document));
    }

    private Map<String, AttributeValue> schemaItem(String hashKey, boolean bool, Boolean boolObj, String attribute, Document document) {
        Map<String, AttributeValue> item = new TreeMap<>();
        item.put("hashKey", hashKey == null ? new AttributeValue().withNULL(true) : new AttributeValue(hashKey));
        item.put("bool", new AttributeValue().withBOOL(bool));
        item.put("boolObj", boolObj == null ? new AttributeValue().withNULL(true) : new AttributeValue().withBOOL(boolObj));
        item.put("attribute", attribute == null ? new AttributeValue().withNULL(true) : new AttributeValue(attribute));
        item.put("document", document == null ? new AttributeValue().withNULL(true) : documentAttributeValue(document));
        return item;
    }

    private AttributeValue documentAttributeValue(Document document) {
        String attribute1 = document.getAttribute1();
        String attribute2 = document.getAttribute2();
        String attribute3 = document.getDynamoNameIsAttribute3();
        return new AttributeValue().withM(documentItem(attribute1, attribute2, attribute3));
    }

    private Map<String, AttributeValue> documentItem(String attribute1, String attribute2, String attribute3) {
        Map<String, AttributeValue> item = new TreeMap<>();
        item.put("attribute1", attribute1 == null ? new AttributeValue().withNULL(true) : new AttributeValue(attribute1));
        item.put("attribute2", attribute2 == null ? new AttributeValue().withNULL(true) : new AttributeValue(attribute2));
        item.put("attribute3", attribute3 == null ? new AttributeValue().withNULL(true) : new AttributeValue(attribute3));
        return item;
    }

}
