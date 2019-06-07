package org.dynodao.processor.itest.table.hash_key;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.processor.itest.AbstractIntegrationTest;
import org.dynodao.processor.test.params.ParallelScanTotalSegmentsSource;
import org.junit.jupiter.params.ParameterizedTest;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

class ParallelScanTest extends AbstractIntegrationTest {

    private static final String TABLE = "things";
    private static final String HASH_KEY = "hashKey";

    @ParameterizedTest
    @ParallelScanTotalSegmentsSource
    void parallelScan_noResults_returnsEmptyStream(int totalSegments) {
        Stream<Schema> parallelScan = dynoDao.get(new SchemaStagedDynamoBuilder().usingTable(), totalSegments);
        assertThat(parallelScan.collect(toList())).isEmpty();
    }

    @ParameterizedTest
    @ParallelScanTotalSegmentsSource
    void parallelScan_singleItem_returnsSingletonStream(int totalSegments) {
        Schema schema = schema("hash");
        put(schema);

        Stream<Schema> parallelScan = dynoDao.get(new SchemaStagedDynamoBuilder().usingTable(), totalSegments);
        assertThat(parallelScan.collect(toList())).containsExactly(schema);
    }

    @ParameterizedTest
    @ParallelScanTotalSegmentsSource
    void parallelScan_multipleItems_returnsAllItemsInAnyOrder(int totalSegments) {
        Schema schema1 = schema("1");
        Schema schema2 = schema("2");
        put(schema1, schema2);

        Stream<Schema> parallelScan = dynoDao.get(new SchemaStagedDynamoBuilder().usingTable(), totalSegments);
        assertThat(parallelScan.collect(toList())).containsExactlyInAnyOrder(schema1, schema2);
    }

    @ParameterizedTest
    @ParallelScanTotalSegmentsSource
    void parallelScan_largeData_returnsAllItemsInAnyOrder(int totalSegments) {
        Schema[] items = IntStream.range(0, 1000)
                .mapToObj(i -> schema(String.valueOf(i)))
                .toArray(Schema[]::new);
        put(items);

        Stream<Schema> parallelScan = dynoDao.get(new SchemaStagedDynamoBuilder().usingTable(), totalSegments);
        assertThat(parallelScan.collect(toList())).containsExactlyInAnyOrder(items);
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
