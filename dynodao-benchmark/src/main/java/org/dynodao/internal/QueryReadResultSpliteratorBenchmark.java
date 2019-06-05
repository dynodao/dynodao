package org.dynodao.internal;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import org.mockito.Mockito;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

public class QueryReadResultSpliteratorBenchmark {

    private static final List<Map<String, AttributeValue>> LIST_0 = generateList(0);
    private static final List<Map<String, AttributeValue>> LIST_1 = generateList(1);
    private static final List<Map<String, AttributeValue>> LIST_2 = generateList(2);
    private static final List<Map<String, AttributeValue>> LIST_3 = generateList(3);
    private static final List<Map<String, AttributeValue>> LIST_4 = generateList(4);
    private static final List<Map<String, AttributeValue>> LIST_5 = generateList(5);
    private static final QueryRequest QUERY_REQUEST = new QueryRequest();

    private static List<Map<String, AttributeValue>> generateList(int callNumber) {
        int listSize = 10000;
        int start = listSize * callNumber;
        int end = listSize * (callNumber + 1);
        return IntStream.range(start, end)
                .mapToObj(i -> new AttributeValue().withS(String.valueOf(i)))
                .map(value -> singletonMap("key", value))
                .collect(toList());
    }

    private static QueryResult buildQueryResult(List<Map<String, AttributeValue>> items, boolean last) {
        return new QueryResult()
                .withItems(items)
                .withLastEvaluatedKey(last ? null : items.get(items.size() - 1));
    }

    @State(Scope.Thread)
    public static class MyState {
        private AmazonDynamoDB amazonDynamoDBMock = Mockito.mock(AmazonDynamoDB.class);

        public MyState() {
            when(amazonDynamoDBMock.query(any()))
                    .thenReturn(buildQueryResult(LIST_1, false))
                    .thenReturn(buildQueryResult(LIST_2, false))
                    .thenReturn(buildQueryResult(LIST_3, false))
                    .thenReturn(buildQueryResult(LIST_4, false))
                    .thenReturn(buildQueryResult(LIST_5, true));
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public long testSpliterator(MyState state) {
        QueryReadResult result = new QueryReadResult<Schema>(state.amazonDynamoDBMock, QUERY_REQUEST, buildQueryResult(LIST_0, false)) {
            @Override
            protected Schema deserialize(Map<String, AttributeValue> item) {
                return new Schema();
            }
        };
        return result.stream().count();
    }

    private static class Schema {
        String value;
    }

}
