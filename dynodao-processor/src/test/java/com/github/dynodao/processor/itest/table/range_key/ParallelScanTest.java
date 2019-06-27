package com.github.dynodao.processor.itest.table.range_key;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.github.dynodao.processor.itest.AbstractIntegrationTest;
import com.github.dynodao.processor.test.params.ParallelScanSource;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

class ParallelScanTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY = "hashKey";
    private static final String RANGE_KEY = "rangeKey";

    @ParameterizedTest
    @ParallelScanSource.TotalSegments
    void parallelScan_noResults_returnsEmptyStream(int totalSegments) {
        Stream<Schema> parallelScan = dynoDao.get(new SchemaStagedDynamoBuilder().usingTable(), totalSegments);
        assertThat(parallelScan.collect(toList())).isEmpty();
    }

    @ParameterizedTest
    @ParallelScanSource.TotalSegments
    void parallelScan_singleItem_returnsSingletonStream(int totalSegments) {
        Schema schema = schema("hash", 1);
        put(schema);

        Stream<Schema> parallelScan = dynoDao.get(new SchemaStagedDynamoBuilder().usingTable(), totalSegments);
        assertThat(parallelScan.collect(toList())).containsExactly(schema);
    }

    @ParameterizedTest
    @ParallelScanSource.TotalSegments
    void parallelScan_multipleItems_returnsAllItemsInAnyOrder(int totalSegments) {
        Schema schema1 = schema("1", 1);
        Schema schema2 = schema("2", 2);
        put(schema1, schema2);

        Stream<Schema> parallelScan = dynoDao.get(new SchemaStagedDynamoBuilder().usingTable(), totalSegments);
        assertThat(parallelScan.collect(toList())).containsExactlyInAnyOrder(schema1, schema2);
    }

    @ParameterizedTest
    @ParallelScanSource.TotalSegments
    void parallelScan_largeData_returnsAllItemsInAnyOrder(int totalSegments) {
        Schema[] items = IntStream.range(0, 1000)
                .mapToObj(i -> schema(String.valueOf(i), i))
                .toArray(Schema[]::new);
        put(items);

        Stream<Schema> parallelScan = dynoDao.get(new SchemaStagedDynamoBuilder().usingTable(), totalSegments);
        assertThat(parallelScan.collect(toList())).containsExactlyInAnyOrder(items);
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
