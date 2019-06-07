package org.dynodao.processor.itest.serialization.number;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractIntegrationTest;
import org.dynodao.processor.test.ParameterizedTestSources;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigInteger;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class BigIntegerSerializationTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY_VALUE = "hashKey";

    @Test
    void serializeBigDecimal_null_returnsNullAttributeValue() {
        AttributeValue value = SchemaAttributeValueSerializer.serializeBigDecimal(null);
        assertThat(value).isEqualTo(new AttributeValue().withNULL(true));
    }

    @ParameterizedTest
    @ValueSource(strings = { "0", "1", "-1982739812739817223894", "-1", "19823791827312393849728342" })
    void serializeBigInteger_numberValues_returnsNumberAttributeValue(BigInteger bigInteger) {
        AttributeValue value = SchemaAttributeValueSerializer.serializeBigInteger(bigInteger);
        assertThat(value).isEqualTo(new AttributeValue().withN(bigInteger.toString()));
    }

    @ParameterizedTest
    @NullSource
    @MethodSource(ParameterizedTestSources.ATTRIBUTE_VALUES_WITHOUT_NUMBER_SOURCE)
    void deserializeBigInteger_nullCases_returnsNull(AttributeValue attributeValue) {
        BigInteger value = SchemaAttributeValueSerializer.deserializeBigInteger(attributeValue);
        assertThat(value).isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = { "0", "1", "-1982739812739817223894", "-1", "19823791827312393849728342" })
    void deserializeBigInteger_numberValue_returnsBigIntegerValue(String numberValue) {
        BigInteger value = SchemaAttributeValueSerializer.deserializeBigInteger(new AttributeValue().withN(numberValue));
        assertThat(value).isEqualTo(new BigInteger(numberValue));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "0", "1", "-1982739812739817223894", "-1", "19823791827312393849728342" })
    void putAndGet_symmetricCases_returnsItem(BigInteger bigInteger) {
        Schema schema = schema(bigInteger);
        put(schema);
        Stream<Schema> items = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey(HASH_KEY_VALUE));
        assertThat(items).containsExactly(schema);
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, SchemaAttributeValueSerializer.serializeSchemaAsItem(item));
    }

    private Schema schema(BigInteger bigInteger) {
        Schema schema = new Schema();
        schema.setHashKey(HASH_KEY_VALUE);
        schema.setBigInteger(bigInteger);
        return schema;
    }

}
