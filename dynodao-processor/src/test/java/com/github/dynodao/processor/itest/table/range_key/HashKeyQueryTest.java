package com.github.dynodao.processor.itest.table.range_key;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.github.dynodao.processor.itest.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class HashKeyQueryTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY = "hashKey";
    private static final String RANGE_KEY = "rangeKey";

    @Test
    void query_noItems_returnsEmptyStream() {
        Stream<Schema> query = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey("no hash key"));
        assertThat(query).isEmpty();
    }

    @Test
    void query_singleItemMatch_returnsSingletonStream() {
        Schema item = schema("hashKey", 1);
        put(item);

        Stream<Schema> query = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey("hashKey"));
        assertThat(query).containsExactly(item);
    }

    @Test
    void query_multipleItemsMatch_returnsStreamInOrder() {
        Schema item1 = schema("hashKey", 1);
        Schema item2 = schema("hashKey", 2);
        put(item1, item2);

        Stream<Schema> query = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey("hashKey"));
        assertThat(query).containsExactly(item1, item2);
    }

    @Test
    void query_largeData_returnsStreamInOrder() {
        Schema[] items = IntStream.range(0, 1000)
                .mapToObj(i -> schema("hashKey", i))
                .toArray(Schema[]::new);
        put(items);

        Stream<Schema> query = dynoDao.get(new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey("hashKey"));
        assertThat(query).containsExactly(items);
    }

    private void put(Schema... items) {
        Arrays.stream(items).forEach(item -> amazonDynamoDb.putItem(TABLE, mapFrom(item)));
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
