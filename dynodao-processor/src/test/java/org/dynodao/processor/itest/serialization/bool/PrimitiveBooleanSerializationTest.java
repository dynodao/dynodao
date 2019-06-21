package org.dynodao.processor.itest.serialization.bool;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractIntegrationTest;
import org.dynodao.processor.test.params.AttributeValueSource;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class PrimitiveBooleanSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void serializePrimitiveBoolean_booleanValue_returnsAttributeValueWithBoolean(boolean bool) {
        AttributeValue value = SchemaAttributeValueSerializer.serializePrimitiveBoolean(bool);
        assertThat(value).isEqualTo(new AttributeValue().withBOOL(bool));
    }

    @ParameterizedTest
    @NullSource
    @AttributeValueSource.WithoutBoolean
    void deserializePrimitiveBoolean_nullCases_returnsFalse(AttributeValue attributeValue) {
        boolean value = SchemaAttributeValueSerializer.deserializePrimitiveBoolean(attributeValue);
        assertThat(value).isFalse();
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void deserializePrimitiveBoolean_booleanValue_returnsBooleanValue(boolean bool) {
        boolean value = SchemaAttributeValueSerializer.deserializePrimitiveBoolean(new AttributeValue().withBOOL(bool));
        assertThat(value).isEqualTo(bool);
    }

    @ParameterizedTest
    @ValueSource(booleans = { true, false })
    void putAndGet_symmetricCases_returnsItem(boolean bool) {
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

    private Schema schema(boolean booleanValue) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setPrimitiveBoolean(booleanValue);
        return schema;
    }

}
