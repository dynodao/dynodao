package org.dynodao.processor.itest.serialization.document;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractIntegrationTest;
import org.dynodao.processor.test.ParameterizedTestSources;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

class DocumentSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @Test
    void serializeDocument_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeDocument(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @CsvSource({
            "a1,a2,a3",
            "a1,a2,", "a1,,a3", ",a2,a3",
            "a1,,", ",a2,", ",,a3",
            ",,"
    })
    void serializeDocument_typicalUseCases_returnsMapAttributeWithCorrectAttributeNames(String attribute1, String attribute2, String attribute3) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeDocument(document(attribute1, attribute2, attribute3));
        assertThat(value).isEqualTo(attributeValue(attribute1, attribute2, attribute3));
    }

    @Test
    void serializeDocumentAsItem_null_returnsEmptyMap() {
        Map<String, AttributeValue> item = SchemaAttributeValueSerializer.serializeDocumentAsItem(null);
        assertThat(item).isEmpty();
    }

    @ParameterizedTest
    @CsvSource({
            "a1,a2,a3",
            "a1,a2,", "a1,,a3", ",a2,a3",
            "a1,,", ",a2,", ",,a3",
            ",,"
    })
    void serializeDocumentAsItem_typicalUseCases_returnsMap(String attribute1, String attribute2, String attribute3) {
        Map<String, AttributeValue> item = SchemaAttributeValueSerializer.serializeDocumentAsItem(document(attribute1, attribute2, attribute3));
        assertThat(item).isEqualTo(item(attribute1, attribute2, attribute3));
    }

    @ParameterizedTest
    @NullSource
    @MethodSource(ParameterizedTestSources.ATTRIBUTE_VALUES_WITHOUT_MAP_SOURCE)
    void deserializeDocument_nullCases_returnsNull(AttributeValue attributeValue) {
        Document value = SchemaAttributeValueSerializer.deserializeDocument(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @CsvSource({
            "a1,a2,a3",
            "a1,a2,", "a1,,a3", ",a2,a3",
            "a1,,", ",a2,", ",,a3",
            ",,"
    })
    void deserializeDocument_correctKeysAndTypesMapCases_returnsDocument(String attribute1, String attribute2, String attribute3) {
        Document value = SchemaAttributeValueSerializer.deserializeDocument(attributeValue(attribute1, attribute2, attribute3));
        assertThat(value).isEqualTo(document(attribute1, attribute2, attribute3));
    }

    @Test
    void deserializeDocument_emptyMap_returnsDocumentWithNullFields() {
        Document document = SchemaAttributeValueSerializer.deserializeDocument(new AttributeValue().withM(emptyMap()));
        assertThat(document).isEqualTo(document(null, null, null));
    }

    @ParameterizedTest
    @CsvSource({
            "dynamoNameIsAttribute3,value",
            "key,value"
    })
    void deserializeDocument_incorrectKeys_returnsDocumentWithNullFields(String key, String value) {
        Document document = SchemaAttributeValueSerializer.deserializeDocument(new AttributeValue().withM(singletonMap(key, new AttributeValue(value))));
        assertThat(document).isEqualTo(document(null, null, null));
    }

    @ParameterizedTest
    @MethodSource(ParameterizedTestSources.ATTRIBUTE_VALUES_WITHOUT_STRING_SOURCE)
    void deserializeDocument_keysHaveWrongTypes_returnsDocumentWithNullFields(AttributeValue attributeValue) {
        Document value = SchemaAttributeValueSerializer.deserializeDocument(new AttributeValue().withM(singletonMap("attribute1", attributeValue)));
        assertThat(value).isEqualTo(document(null, null, null));
    }

    @ParameterizedTest
    @NullSource
    void deserializeDocumentFromItem_null_returnsNull(Map<String, AttributeValue> item) {
        Document value = SchemaAttributeValueSerializer.deserializeDocumentFromItem(item);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @CsvSource({
            "a1,a2,a3",
            "a1,a2,", "a1,,a3", ",a2,a3",
            "a1,,", ",a2,", ",,a3",
            ",,"
    })
    void deserializeDocumentFromItem_correctKeysAndTypesMapCases_returnsDocument(String attribute1, String attribute2, String attribute3) {
        Document value = SchemaAttributeValueSerializer.deserializeDocumentFromItem(item(attribute1, attribute2, attribute3));
        assertThat(value).isEqualTo(document(attribute1, attribute2, attribute3));
    }

    @Test
    void deserializeDocumentFromItem_emptyMap_returnsDocumentWithNullFields() {
        Document value = SchemaAttributeValueSerializer.deserializeDocumentFromItem(emptyMap());
        assertThat(value).isEqualTo(document(null, null, null));
    }

    @ParameterizedTest
    @CsvSource({
            "dynamoNameIsAttribute3,value",
            "key,value"
    })
    void deserializeDocumentFromItem_incorrectKeys_returnsDocumentWithNullFields(String key, String value) {
        Document document = SchemaAttributeValueSerializer.deserializeDocumentFromItem(singletonMap(key, new AttributeValue(value)));
        assertThat(document).isEqualTo(document(null, null, null));
    }

    @ParameterizedTest
    @MethodSource(ParameterizedTestSources.ATTRIBUTE_VALUES_WITHOUT_STRING_SOURCE)
    void deserializeDocumentFromItem_keysHaveWrongTypes_returnsDocumentWithNullFields(AttributeValue attributeValue) {
        Document value = SchemaAttributeValueSerializer.deserializeDocumentFromItem(singletonMap("attribute1", attributeValue));
        assertThat(value).isEqualTo(document(null, null, null));
    }

    @ParameterizedTest
    @CsvSource({
            "a1,a2,a3",
            "a1,a2,", "a1,,a3", ",a2,a3",
            "a1,,", ",a2,", ",,a3",
            ",,"
    })
    void putAndGet_symmetricCases_returnsItem(String attribute1, String attribute2, String attribute3) {
        Document document = document(attribute1, attribute2, attribute3);
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setDocument(document);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));
        assertThat(items).containsExactly(schema);
    }

    private Document document(String attribute1, String attribute2, String attribute3) {
        Document document = new Document();
        document.setAttribute1(attribute1);
        document.setAttribute2(attribute2);
        document.setDynamoNameIsAttribute3(attribute3);
        return document;
    }

    private Map<String, AttributeValue> item(String attribute1, String attribute2, String attribute3) {
        Map<String, AttributeValue> item = new TreeMap<>();
        item.put("attribute1", attribute1 == null ? new AttributeValue().withNULL(true) : new AttributeValue(attribute1));
        item.put("attribute2", attribute2 == null ? new AttributeValue().withNULL(true) : new AttributeValue(attribute2));
        item.put("attribute3", attribute3 == null ? new AttributeValue().withNULL(true) : new AttributeValue(attribute3));
        return item;
    }

    private AttributeValue attributeValue(String attribute1, String attribute2, String attribute3) {
        return new AttributeValue().withM(item(attribute1, attribute2, attribute3));
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

}
