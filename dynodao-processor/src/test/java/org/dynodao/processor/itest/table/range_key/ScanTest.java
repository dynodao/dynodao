package org.dynodao.processor.itest.table.range_key;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ScanTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY = "hashKey";
    private static final String RANGE_KEY = "rangeKey";

    @Test
    void scan_noResults_returnsEmptyStream() {
        Stream<Schema> scan = new SchemaStagedDynamoBuilder()
                .usingTable()
                .scan(amazonDynamoDb);
        assertThat(scan).isEmpty();
    }

    @Test
    void scan_singleItem_returnsSingletonStream() {
        Schema schema = schema("hash", 1);
        put(schema);

        Stream<Schema> scan = new SchemaStagedDynamoBuilder()
                .usingTable()
                .scan(amazonDynamoDb);
        assertThat(scan).containsExactly(schema);
    }

    @Test
    void scan_multipleItems_returnsAllItemsInAnyOrder() {
        Schema schema1 = schema("1", 1);
        Schema schema2 = schema("2", 2);
        put(schema1, schema2);

        Stream<Schema> scan = new SchemaStagedDynamoBuilder()
                .usingTable()
                .scan(amazonDynamoDb);
        assertThat(scan).containsExactlyInAnyOrder(schema1, schema2);
    }

    @Test
    void scan_largeData_returnsAllItemsInAnyOrder() {
        Schema[] items = IntStream.range(0, 1000)
                .mapToObj(i -> schema(String.valueOf(i), i))
                .toArray(Schema[]::new);
        put(items);

        Stream<Schema> scan = new SchemaStagedDynamoBuilder()
                .usingTable()
                .scan(amazonDynamoDb);
        assertThat(scan).containsExactlyInAnyOrder(items);
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
