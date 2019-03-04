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

class DocumentSerializionTest extends AbstractSourceCompilingTest {

    @Test
    void serializeDocument_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeDocument(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @Test
    void serializeDocument_allFieldsPresent_returnsMapAttributeValueWithOverrideAttributeNames() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeDocument(document("a1", "a2", "a3"));
        assertThat(value).isEqualTo(attributeValue("a1", "a2", "a3"));
    }

    @Test
    void serializeDocument_someFieldsNull_returnsMapAttributeValueWithNulls() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeDocument(document(null, null, "a3"));
        assertThat(value).isEqualTo(attributeValue(null, null, "a3"));
    }

    @Test
    void serializeDocument_allFieldsNull_returnsMapAttributeValueWithNulls() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeDocument(document(null, null, null));
        assertThat(value).isEqualTo(attributeValue(null, null, null));
    }

    @Test
    void deserializeDocument_null_returnsNull() {
        Document value = SchemaAttributeValueSerializer.deserializeDocument(null);
        assertThat(value).isNull();
    }

    @Test
    void deserializeDocument_nullAttributeValue_returnsNull() {
        Document value = SchemaAttributeValueSerializer.deserializeDocument(new AttributeValue().withNULL(true));
        assertThat(value).isNull();
    }

    @Test
    void deserializeDocument_mapValueNull_returnsNull() {
        Document value = SchemaAttributeValueSerializer.deserializeDocument(new AttributeValue().withS("not map"));
        assertThat(value).isNull();
    }

    @Test
    void deserializeDocument_mapAttributeValue_returnsDocument() {
        Document value = SchemaAttributeValueSerializer.deserializeDocument(attributeValue("a1", "a2", "a3"));
        assertThat(value).isEqualTo(document("a1", "a2", "a3"));
    }

    @Test
    void deserializeDocument_emptyMap_returnsDocumentWithNullFields() {
        Document value = SchemaAttributeValueSerializer.deserializeDocument(new AttributeValue().withM(emptyMap()));
        assertThat(value).isEqualTo(document(null, null, null));
    }

    @Test
    void deserializeDocument_valueHaveWrongKeys_returnsDocumentWithNullFields() {
        Document value = SchemaAttributeValueSerializer.deserializeDocument(new AttributeValue().withM(singletonMap("dynamoNameIsAttribute3", new AttributeValue("a3"))));
        assertThat(value).isEqualTo(document(null, null, null));
    }
    
    @Test
    void deserializeDocument_someKeysCorrect_returnsDocumentWithNullForMissingFieldsOnly() {
        Document value = SchemaAttributeValueSerializer.deserializeDocument(new AttributeValue().withM(singletonMap("attribute3", new AttributeValue("a3"))));
        assertThat(value).isEqualTo(document(null, null, "a3"));
    }

    @Test
    void deserializeDocument_keysHaveWrongTypes_returnsDocumentWithNullFields() {
        Document value = SchemaAttributeValueSerializer.deserializeDocument(new AttributeValue().withM(singletonMap("attribute3", new AttributeValue().withM(emptyMap()))));
        assertThat(value).isEqualTo(document(null, null, null));
    }

    private Document document(String attribute1, String attribute2, String attribute3) {
        Document document = new Document();
        document.setAttribute1(attribute1);
        document.setAttribute2(attribute2);
        document.setDynamoNameIsAttribute3(attribute3);
        return document;
    }

    private AttributeValue attributeValue(String attribute1, String attribute2, String attribute3) {
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
