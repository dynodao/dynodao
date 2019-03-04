package org.lemon.dynodao.processor.itest.serialization.document;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.junit.jupiter.api.Test;
import org.lemon.dynodao.processor.itest.AbstractSourceCompilingTest;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

class SchemaSerializionTest extends AbstractSourceCompilingTest {

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
        Map<String, AttributeValue> map = new TreeMap<>();
        map.put("hashKey", hashKey == null ? new AttributeValue().withNULL(true) : new AttributeValue(hashKey));
        map.put("bool", new AttributeValue().withBOOL(bool));
        map.put("boolObj", boolObj == null ? new AttributeValue().withNULL(true) : new AttributeValue().withBOOL(boolObj));
        map.put("attribute", attribute == null ? new AttributeValue().withNULL(true) : new AttributeValue(attribute));
        map.put("document", document == null ? new AttributeValue().withNULL(true) : documentAttributeValue(document));
        return new AttributeValue().withM(map);
    }

    private AttributeValue documentAttributeValue(Document document) {
        String attribute1 = document.getAttribute1();
        String attribute2 = document.getAttribute2();
        String attribute3 = document.getDynamoNameIsAttribute3();
        Map<String, AttributeValue> map = new TreeMap<>();
        map.put("attribute1", attribute1 == null ? new AttributeValue().withNULL(true) : new AttributeValue(attribute1));
        map.put("attribute2", attribute2 == null ? new AttributeValue().withNULL(true) : new AttributeValue(attribute2));
        map.put("attribute3", attribute3 == null ? new AttributeValue().withNULL(true) : new AttributeValue(attribute3));
        return new AttributeValue().withM(map);
    }

    @Override
    protected Set<Class<?>> ignoreTestEqualsClasses() {
        Set<Class<?>> classes = super.ignoreTestEqualsClasses();
        classes.add(Document.class);
        return classes;
    }

}
