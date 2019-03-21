package org.dynodao.processor.itest.table.hash_key;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;

class ScanTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY = "hashKey";

    @Test
    void scan_noResults_returnsEmptyStream() {
        Stream<Schema> scan = new SchemaStagedDynamoBuilder()
                .usingTable()
                .scan(amazonDynamoDb);
        assertThat(scan).isEmpty();
    }

    @Test
    void scan_singleItem_returnsSingletonStream() {
        Schema schema = schema("hash");
        put(schema);

        Stream<Schema> scan = new SchemaStagedDynamoBuilder()
                .usingTable()
                .scan(amazonDynamoDb);
        assertThat(scan).containsExactly(schema);
    }

    @Test
    void scan_multipleItems_returnsAllItemsInAnyOrder() {
        Schema schema1 = schema("1");
        Schema schema2 = schema("2");
        put(schema1, schema2);

        Stream<Schema> scan = new SchemaStagedDynamoBuilder()
                .usingTable()
                .scan(amazonDynamoDb);
        assertThat(scan).containsExactlyInAnyOrder(schema1, schema2);
    }

    @Test
    void scan_largeData_returnsAllItemsInAnyOrder() {
        Schema[] items = IntStream.range(0, 1000)
                .mapToObj(i -> schema(String.valueOf(i)))
                .toArray(Schema[]::new);
        put(items);

        Stream<Schema> scan = new SchemaStagedDynamoBuilder()
                .usingTable()
                .scan(amazonDynamoDb);
        assertThat(scan).containsExactlyInAnyOrder(items);
    }

    private void put(Schema... items) {
        Arrays.stream(items).forEach(item -> amazonDynamoDb.putItem(TABLE, singletonMap(HASH_KEY, new AttributeValue(item.getHashKey()))));
    }

    private Schema schema(String hashKeyValue) {
        Schema schema = new Schema();
        schema.setHashKey(hashKeyValue);
        return schema;
    }

}
