package org.dynodao.processor.itest.serialization.document;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractIntegrationTest;
import org.dynodao.processor.test.PackageScanner;
import org.dynodao.processor.test.params.AttributeValueSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

class SchemaSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "key";

    @Test
    void serializeSchema_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeSchema(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @CsvSource({
            "key,true,true,a,true,a1,a2,a3",
            "key,true,true,a,true,a1,a2,",
            "key,true,false,,true,a1,a2,",
            "key,true,,,false,,,",
            ",true,,,false,,,"
    })
    void serializeSchema_typicalUseCases_returnsMapAttributeWithCorrectAttributeNames(@AggregateWith(SchemaAggregator.class) Schema schema, @AggregateWith(SchemaAttributeValueAggregator.class) AttributeValue attributeValue) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeSchema(schema);
        assertThat(value).isEqualTo(attributeValue);
    }

    @Test
    void serializeSchemaAsItem_null_returnsEmptyMap() {
        Map<String, AttributeValue> item = SchemaAttributeValueSerializer.serializeSchemaAsItem(null);
        assertThat(item).isEmpty();
    }

    @ParameterizedTest
    @CsvSource({
            "key,true,true,a,true,a1,a2,a3",
            "key,true,true,a,true,a1,a2,",
            "key,true,false,,true,a1,a2,",
            "key,true,,,false,,,",
            ",true,,,false,,,"
    })
    void serializeSchemaAsItem_typicalUseCases_returnsMapWithOverrideAttributeNames(@AggregateWith(SchemaAggregator.class) Schema schema, @AggregateWith(SchemaItemAggregator.class) Map<String, AttributeValue> schemaItem) {
        Map<String, AttributeValue> item = SchemaAttributeValueSerializer.serializeSchemaAsItem(schema);
        assertThat(item).isEqualTo(schemaItem);
    }

    @ParameterizedTest
    @NullSource
    @AttributeValueSource.WithoutMap
    void deserializeSchema_nullCases_returnsNull(AttributeValue attributeValue) {
        Schema value = SchemaAttributeValueSerializer.deserializeSchema(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @CsvSource({
            "key,true,true,a,true,a1,a2,a3",
            "key,true,true,a,true,a1,a2,",
            "key,true,false,,true,a1,a2,",
            "key,true,,,false,,,",
            ",true,,,false,,,"
    })
    void deserializeSchema_correctKeysAndTypesMapCases_returnsSchema(@AggregateWith(SchemaAggregator.class) Schema schema, @AggregateWith(SchemaAttributeValueAggregator.class) AttributeValue attributeValue) {
        Schema value = SchemaAttributeValueSerializer.deserializeSchema(attributeValue);
        assertThat(value).isEqualTo(schema);
    }

    @Test
    void deserializeSchema_emptyMap_returnsDocumentWithNullFields() {
        Schema schema = SchemaAttributeValueSerializer.deserializeSchema(new AttributeValue().withM(emptyMap()));
        assertThat(schema).isEqualTo(schema(null, false, null, null, null));
    }

    @ParameterizedTest
    @CsvSource({
            "dynamoNameIsAttribute,value",
            "key,value"
    })
    void deserializeSchema_incorrectKeys_returnsSchemaWithNullFields(String key, String value) {
        Schema schema = SchemaAttributeValueSerializer.deserializeSchema(new AttributeValue().withM(singletonMap(key, new AttributeValue(value))));
        assertThat(schema).isEqualTo(schema(null, false, null, null, null));
    }

    @ParameterizedTest
    @AttributeValueSource.WithoutString
    void deserializeSchema_keysHaveWrongTypes_returnsSchemaWithNullFields(AttributeValue attributeValue) {
        Schema value = SchemaAttributeValueSerializer.deserializeSchema(new AttributeValue().withM(singletonMap("hashKey", attributeValue)));
        assertThat(value).isEqualTo(schema(null, false, null, null, null));
    }

    @ParameterizedTest
    @NullSource
    void deserializeSchemaFromItem_null_returnsNull(Map<String, AttributeValue> item) {
        Schema value = SchemaAttributeValueSerializer.deserializeSchemaFromItem(item);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @CsvSource({
            "key,true,true,a,true,a1,a2,a3",
            "key,true,true,a,true,a1,a2,",
            "key,true,false,,true,a1,a2,",
            "key,true,,,false,,,",
            ",true,,,false,,,"
    })
    void deserializeSchemaFromItem_correctKeysAndTypesMapCases_returnsSchema(@AggregateWith(SchemaAggregator.class) Schema schema, @AggregateWith(SchemaItemAggregator.class) Map<String, AttributeValue> item) {
        Schema value = SchemaAttributeValueSerializer.deserializeSchemaFromItem(item);
        assertThat(value).isEqualTo(schema);
    }

    @Test
    void deserializeSchemaFromItem_emptyMap_returnsDocumentWithNullFields() {
        Schema value = SchemaAttributeValueSerializer.deserializeSchemaFromItem(emptyMap());
        assertThat(value).isEqualTo(schema(null, false, null, null, null));
    }

    @ParameterizedTest
    @CsvSource({
            "dynamoNameIsAttribute,value",
            "key,value"
    })
    void deserializeSchemaFromItem_incorrectKeys_returnsDocumentWithNullFields(String key, String value) {
        Schema schema = SchemaAttributeValueSerializer.deserializeSchemaFromItem(singletonMap(key, new AttributeValue(value)));
        assertThat(schema).isEqualTo(schema(null, false, null, null, null));
    }

    @ParameterizedTest
    @AttributeValueSource.WithoutString
    void deserializeSchemaFromItem_keysHaveWrongTypes_returnsDocumentWithNullFields(AttributeValue attributeValue) {
        Schema value = SchemaAttributeValueSerializer.deserializeSchemaFromItem(singletonMap("hashKey", attributeValue));
        assertThat(value).isEqualTo(schema(null, false, null, null, null));
    }

    @ParameterizedTest
    @CsvSource({
            "key,true,true,a,true,a1,a2,a3",
            "key,true,true,a,true,a1,a2,",
            "key,true,false,,true,a1,a2,",
            "key,true,,,false,,,"
    })
    void putAndGet_symmetricCases_returnsItem(@AggregateWith(SchemaAggregator.class) Schema schema) {
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));
        assertThat(items).containsExactly(schema);
    }

    @PackageScanner.Ignore
    static class SchemaAggregator implements ArgumentsAggregator {
        @Override
        public Schema aggregateArguments(ArgumentsAccessor arguments, ParameterContext context) throws ArgumentsAggregationException {
            return schema(arguments.getString(0), arguments.getBoolean(1), arguments.getBoolean(2), arguments.getString(3),
                    arguments.getBoolean(4) ? document(arguments) : null);
        }
    }

    @PackageScanner.Ignore
    static class SchemaAttributeValueAggregator implements ArgumentsAggregator {
        @Override
        public AttributeValue aggregateArguments(ArgumentsAccessor arguments, ParameterContext context) throws ArgumentsAggregationException {
            return schemaAttributeValue(arguments.getString(0), arguments.getBoolean(1), arguments.getBoolean(2), arguments.getString(3),
                    arguments.getBoolean(4) ? document(arguments) : null);
        }
    }

    @PackageScanner.Ignore
    static class SchemaItemAggregator implements ArgumentsAggregator {
        @Override
        public Map<String, AttributeValue> aggregateArguments(ArgumentsAccessor arguments, ParameterContext context) throws ArgumentsAggregationException {
            return schemaItem(arguments.getString(0), arguments.getBoolean(1), arguments.getBoolean(2), arguments.getString(3),
                    arguments.getBoolean(4) ? document(arguments) : null);
        }
    }

    private static Schema schema(String hashKey, boolean bool, Boolean boolObj, String attribute, Document document) {
        Schema schema = new Schema();
        schema.setHashKey(hashKey);
        schema.setBool(bool);
        schema.setBoolObj(boolObj);
        schema.setDynamoNameIsAttribute(attribute);
        schema.setDocument(document);
        return schema;
    }

    private static Document document(ArgumentsAccessor arguments) {
        return document(arguments.getString(5), arguments.getString(6), arguments.getString(7));
    }

    private static Document document(String attribute1, String attribute2, String attribute3) {
        Document document = new Document();
        document.setAttribute1(attribute1);
        document.setAttribute2(attribute2);
        document.setDynamoNameIsAttribute3(attribute3);
        return document;
    }

    private static AttributeValue schemaAttributeValue(String hashKey, boolean bool, Boolean boolObj, String attribute, Document document) {
        return new AttributeValue().withM(schemaItem(hashKey, bool, boolObj, attribute, document));
    }

    private static Map<String, AttributeValue> schemaItem(String hashKey, boolean bool, Boolean boolObj, String attribute, Document document) {
        Map<String, AttributeValue> item = new TreeMap<>();
        item.put("hashKey", hashKey == null ? new AttributeValue().withNULL(true) : new AttributeValue(hashKey));
        item.put("bool", new AttributeValue().withBOOL(bool));
        item.put("boolObj", boolObj == null ? new AttributeValue().withNULL(true) : new AttributeValue().withBOOL(boolObj));
        item.put("attribute", attribute == null ? new AttributeValue().withNULL(true) : new AttributeValue(attribute));
        item.put("document", document == null ? new AttributeValue().withNULL(true) : documentAttributeValue(document));
        return item;
    }

    private static AttributeValue documentAttributeValue(Document document) {
        String attribute1 = document.getAttribute1();
        String attribute2 = document.getAttribute2();
        String attribute3 = document.getDynamoNameIsAttribute3();
        return new AttributeValue().withM(documentItem(attribute1, attribute2, attribute3));
    }

    private static Map<String, AttributeValue> documentItem(String attribute1, String attribute2, String attribute3) {
        Map<String, AttributeValue> item = new TreeMap<>();
        item.put("attribute1", attribute1 == null ? new AttributeValue().withNULL(true) : new AttributeValue(attribute1));
        item.put("attribute2", attribute2 == null ? new AttributeValue().withNULL(true) : new AttributeValue(attribute2));
        item.put("attribute3", attribute3 == null ? new AttributeValue().withNULL(true) : new AttributeValue(attribute3));
        return item;
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

}
