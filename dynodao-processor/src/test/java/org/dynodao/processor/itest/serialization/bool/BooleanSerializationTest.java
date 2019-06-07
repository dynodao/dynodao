package org.dynodao.processor.itest.serialization.bool;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractIntegrationTest;
import org.dynodao.processor.test.ParameterizedTestSources;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class BooleanSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @ParameterizedTest
    @NullSource
    void serializeBoolean_nullCases_returnsNullAttributeValue(Boolean bool) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeBoolean(bool);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void serializeBoolean_booleanValue_returnsAttributeValueWithBoolean(Boolean bool) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeBoolean(bool);
        assertThat(value).isEqualTo(new AttributeValue().withBOOL(bool));
    }

    @ParameterizedTest
    @NullSource
    @ParameterizedTestSources.AttributeValuesWithoutBooleanSource
    void deserializeBoolean_nullCases_returnsNull(AttributeValue attributeValue) {
        Boolean value = SchemaAttributeValueSerializer.deserializeBoolean(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void deserializeBoolean_booleanValue_returnsBooleanValue(Boolean bool) {
        Boolean value = SchemaAttributeValueSerializer.deserializeBoolean(new AttributeValue().withBOOL(bool));
        assertThat(value).isEqualTo(bool);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(booleans = { true, false })
    void putAndGet_symmetricCases_returnsItem(Boolean bool) {
        Schema schema = schema(bool);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));
        assertThat(items).containsExactly(schema);
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(Boolean booleanValue) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setBooleanObject(booleanValue);
        return schema;
    }

}
