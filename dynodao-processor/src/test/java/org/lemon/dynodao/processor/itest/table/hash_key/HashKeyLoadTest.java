package org.lemon.dynodao.processor.itest.table.hash_key;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.junit.jupiter.api.Test;
import org.lemon.dynodao.processor.itest.AbstractIntegrationTest;

import java.util.stream.Stream;

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

class HashKeyLoadTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY = "hashKey";

    @Test
    void load_itemExists_returnsDeserializedItem() {
        Schema item = schema("value");
        put(item);

        Stream<Schema> load = new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey("value")
                .load(amazonDynamoDb);
        assertThat(load).containsExactly(item);
    }

    @Test
    void load_itemDoesNotExist_returnsEmptyStream() {
        Stream<Schema> load = new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey("no-such-value")
                .load(amazonDynamoDb);
        assertThat(load).isEmpty();
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, singletonMap(HASH_KEY, new AttributeValue(item.getHashKey())));
    }

    private Schema schema(String hashKeyValue) {
        Schema schema = new Schema();
        schema.setHashKey(hashKeyValue);
        return schema;
    }

}
