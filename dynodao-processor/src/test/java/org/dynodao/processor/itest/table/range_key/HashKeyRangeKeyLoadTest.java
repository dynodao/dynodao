package org.dynodao.processor.itest.table.range_key;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class HashKeyRangeKeyLoadTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY = "hashKey";
    private static final String RANGE_KEY = "rangeKey";

    @Test
    void load_noItems_returnsEmptyStream() {
        Stream<Schema> load = new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey("no hash key")
                .withRangeKey(0)
                .load(amazonDynamoDb);
        assertThat(load).isEmpty();
    }

    @Test
    void load_singleItemMatch_returnsSingletonStream() {
        Schema item = schema("hashKey", 1);
        put(item);

        Stream<Schema> load = new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey("hashKey")
                .withRangeKey(1)
                .load(amazonDynamoDb);
        assertThat(load).containsExactly(item);
    }

    private void put(Schema item) {
        amazonDynamoDb.putItem(TABLE, mapFrom(item));
    }

    private Map<String, AttributeValue> mapFrom(Schema item) {
        Map<String, AttributeValue> map = new LinkedHashMap<>();
        map.put(HASH_KEY, new AttributeValue(item.getHashKey()));
        map.put(RANGE_KEY, new AttributeValue().withN(String.valueOf(item.getRangeKey())));
        return map;
    }

    private Schema schema(String hashKeyValue, int rangeKeyValue) {
        Schema schema = new Schema();
        schema.setHashKey(hashKeyValue);
        schema.setRangeKey(rangeKeyValue);
        return schema;
    }

}
