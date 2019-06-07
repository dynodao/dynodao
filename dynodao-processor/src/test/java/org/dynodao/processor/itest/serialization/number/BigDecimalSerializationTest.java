package org.dynodao.processor.itest.serialization.number;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractIntegrationTest;
import org.dynodao.processor.test.ParameterizedTestSources;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class BigDecimalSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @Test
    void serializeBigDecimal_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeBigDecimal(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @ValueSource(strings = { "0", "1", "1e10", "-1", "1e-10", "-1e-10" })
    void serializeBigDecimal_numberValues_returnsNumberAttributeValueUsingPlainString(BigDecimal bigDecimal) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeBigDecimal(bigDecimal);
        assertThat(value).isEqualTo(new AttributeValue().withN(bigDecimal.toPlainString()));
    }

    @ParameterizedTest
    @NullSource
    @MethodSource(ParameterizedTestSources.ATTRIBUTE_VALUES_WITHOUT_NUMBER_SOURCE)
    void deserializeBigDecimal_nullCases_returnsNull(AttributeValue attributeValue) {
        BigDecimal value = SchemaAttributeValueSerializer.deserializeBigDecimal(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = { "0", "1", "-1", "10.123123123", "-1029381092.123123" })
    void deserializeBigDecimal_numberValue_returnsBigDecimalValue(String numberValue) {
        BigDecimal value = SchemaAttributeValueSerializer.deserializeBigDecimal(new AttributeValue().withN(numberValue));
        assertThat(value).isEqualTo(new BigDecimal(numberValue));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "0", "1", "1e10", "-1", "1e-10", "-1e-10" })
    void putAndGet_symmetricCases_returnsItem(BigDecimal bigDecimal) {
        Schema schema = schema(bigDecimal == null ? null : new BigDecimal(bigDecimal.toPlainString()));
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));
        assertThat(items).containsExactly(schema);
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(BigDecimal bigDecimal) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setBigDecimal(bigDecimal);
        return schema;
    }

}
