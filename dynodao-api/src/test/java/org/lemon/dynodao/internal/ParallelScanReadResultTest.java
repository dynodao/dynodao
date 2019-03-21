package org.lemon.dynodao.internal;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.lemon.dynodao.test.AbstractUnitTest;
import org.lemon.dynodao.test.Item;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.lemon.dynodao.test.Item.concat;
import static org.lemon.dynodao.test.Item.item;
import static org.lemon.dynodao.test.Item.items;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class ParallelScanReadResultTest extends AbstractUnitTest {

    private static final int MIN_SEGMENTS = 2;
    private static final int MAX_SEGMENTS = 32;

    @TestFactory
    @SuppressWarnings("unchecked")
    Stream<DynamicTest> ctor_onlyUseCase_requestCloned() {
        return IntStream.rangeClosed(MIN_SEGMENTS, MAX_SEGMENTS)
                .mapToObj(segments -> dynamicTest(testName(segments, "ctor"), () ->{
                    AmazonDynamoDB amazonDynamoDbMock = mock(AmazonDynamoDB.class);
                    ScanRequest request = initialRequest(segments);
                    ParallelScanReadResult<Item> classUnderTest = build(amazonDynamoDbMock, request);

                    assertThat(classUnderTest).extracting("scanRequest")
                            .allSatisfy(r -> assertThat(r).isEqualTo(request).isNotSameAs(request));

                    assertThat(classUnderTest).extracting("amazonDynamoDb", "iterationStarted")
                            .containsExactly(amazonDynamoDbMock, false);

                    assertThat(classUnderTest).extracting("splitSegments").element(0)
                            // Queue inherits equals() from Object, assert on contents instead of equality
                            .isInstanceOf(ConcurrentLinkedQueue.class)
                            .satisfies(queue -> assertThat((Queue<Integer>) queue).containsExactly(IntStream.range(0, segments)
                                    .boxed()
                                    .toArray(Integer[]::new)));
                }));
    }

    @TestFactory
    Stream<DynamicTest> stream_allSegmentsHaveEmptyFirstPage_returnsEmptyStream() {
        return IntStream.rangeClosed(MIN_SEGMENTS, MAX_SEGMENTS)
                .mapToObj(segments -> dynamicTest(testName(segments, "empty result"), () -> {
                    AmazonDynamoDB amazonDynamoDbMock = mock(AmazonDynamoDB.class);
                    Item[][] pages = IntStream.range(0, segments)
                            .mapToObj(segment -> new Item[0])
                            .toArray(Item[][]::new);
                    ScanRequest[] requests = IntStream.range(0, segments)
                            .mapToObj(segment -> request(segment, segments))
                            .toArray(ScanRequest[]::new);

                    IntStream.range(0, segments)
                            .forEach(i -> when(amazonDynamoDbMock.scan(requests[i])).thenReturn(result(null, pages[i])));

                    ParallelScanReadResult<Item> classUnderTest = build(amazonDynamoDbMock, initialRequest(segments));
                    Stream<Item> scan = classUnderTest.stream();

                    assertThat(scan.collect(toList())).isEmpty();

                    IntStream.range(0, segments)
                            .forEach(i -> verify(amazonDynamoDbMock).scan(requests[i]));
                    verifyNoMoreInteractions(amazonDynamoDbMock);
                }));
    }

    @TestFactory
    Stream<DynamicTest> stream_allSegmentsHaveOnePage_scansEachPageAndReturnsStream() {
        return IntStream.rangeClosed(MIN_SEGMENTS, MAX_SEGMENTS)
                .mapToObj(segments -> dynamicTest(testName(segments, "single page"), () -> {
                    AmazonDynamoDB amazonDynamoDbMock = mock(AmazonDynamoDB.class);
                    Item[][] pages = IntStream.range(0, segments)
                            .mapToObj(segment -> items(String.format("segment%03d", segment), 0, 2))
                            .toArray(Item[][]::new);
                    ScanRequest[] requests = IntStream.range(0, segments)
                            .mapToObj(segment -> request(segment, segments))
                            .toArray(ScanRequest[]::new);

                    IntStream.range(0, segments)
                            .forEach(i -> when(amazonDynamoDbMock.scan(requests[i])).thenReturn(result(null, pages[i])));

                    ParallelScanReadResult<Item> classUnderTest = build(amazonDynamoDbMock, initialRequest(segments));
                    Stream<Item> scan = classUnderTest.stream();

                    assertThat(scan.collect(toList())).containsExactlyInAnyOrder(concat(pages));

                    IntStream.range(0, segments)
                            .forEach(i -> verify(amazonDynamoDbMock).scan(requests[i]));
                    verifyNoMoreInteractions(amazonDynamoDbMock);
                }));
    }

    @TestFactory
    Stream<DynamicTest> stream_segmentsHaveMultiplePages_scansEachPageAndReturnsStreamWithAllResults() {
        return IntStream.rangeClosed(MIN_SEGMENTS, MAX_SEGMENTS)
                .mapToObj(segments -> dynamicTest(testName(segments, "multiple pages"), () -> {
                    AmazonDynamoDB amazonDynamoDbMock = mock(AmazonDynamoDB.class);
                    Item[][] firstItems = IntStream.range(0, segments)
                            .mapToObj(segment -> items(String.format("segment%03d", segment), 0, 2))
                            .toArray(Item[][]::new);
                    Item[][] middleItems = IntStream.range(0, segments)
                            .mapToObj(segment -> items(String.format("segment%03d", segment), 2, 4))
                            .toArray(Item[][]::new);
                    Item[][] lastItems = IntStream.range(0, segments)
                            .mapToObj(segment -> items(String.format("segment%03d", segment), 4, 6))
                            .toArray(Item[][]::new);

                    ScanRequest[] firstRequests = IntStream.range(0, segments)
                            .mapToObj(segment -> request(segment, segments))
                            .toArray(ScanRequest[]::new);
                    ScanRequest[] middleRequests = IntStream.range(0, segments)
                            .mapToObj(segment -> request(segment, segments, firstItems[segment][firstItems[segment].length - 1].serialize()))
                            .toArray(ScanRequest[]::new);
                    ScanRequest[] lastRequests = IntStream.range(0, segments)
                            .mapToObj(segment -> request(segment, segments, middleItems[segment][middleItems[segment].length - 1].serialize()))
                            .toArray(ScanRequest[]::new);

                    ScanResult[] firstResults = IntStream.range(0, segments)
                            .mapToObj(segment -> result(firstItems[segment][firstItems[segment].length - 1].serialize(), firstItems[segment]))
                            .toArray(ScanResult[]::new);
                    ScanResult[] emptyResults = IntStream.range(0, segments)
                            .mapToObj(segment -> result(middleItems[segment][middleItems[segment].length - 1].serialize(), middleItems[segment]))
                            .toArray(ScanResult[]::new);
                    ScanResult[] lastResults = IntStream.range(0, segments)
                            .mapToObj(segment -> result(null, lastItems[segment]))
                            .toArray(ScanResult[]::new);

                    for (int i = 0; i < segments; ++i) {
                        when(amazonDynamoDbMock.scan(firstRequests[i])).thenReturn(firstResults[i]);
                        when(amazonDynamoDbMock.scan(middleRequests[i])).thenReturn(emptyResults[i]);
                        when(amazonDynamoDbMock.scan(lastRequests[i])).thenReturn(lastResults[i]);
                    }

                    ParallelScanReadResult<Item> classUnderTest = build(amazonDynamoDbMock, initialRequest(segments));
                    Stream<Item> stream = classUnderTest.stream();

                    assertThat(stream.collect(toList())).containsExactlyInAnyOrder(concat(concat(firstItems), concat(middleItems), concat(lastItems)));

                    // strange verification due to mutation of the request; paired with `when` this is still strong verification
                    for (int i = 0; i < segments; ++i) {
                        verify(amazonDynamoDbMock, times(3)).scan(lastRequests[i]);
                    }
                    verifyNoMoreInteractions(amazonDynamoDbMock);
                }));
    }

    @TestFactory
    @SuppressWarnings("unchecked")
    Stream<DynamicTest> stream_segmentsHaveEmptyPages_scansEachPageAndReturnsStreamWithAllResults() {
        return IntStream.rangeClosed(MIN_SEGMENTS, MAX_SEGMENTS)
                .mapToObj(segments -> dynamicTest(testName(segments, "with empty pages"), () -> {
                    AmazonDynamoDB amazonDynamoDbMock = mock(AmazonDynamoDB.class);
                    Item[][] firstItems = IntStream.range(0, segments)
                            .mapToObj(segment -> items(String.format("segment%03d", segment), 0, 2))
                            .toArray(Item[][]::new);

                    Map<String, AttributeValue>[] lastEvalKeyForEmpty = IntStream.range(0, segments)
                            .mapToObj(segment -> item(String.format("segment%03d", segment), 2))
                            .map(Item::serialize)
                            .toArray(Map[]::new);

                    Item[][] lastItems = IntStream.range(0, segments)
                            .mapToObj(segment -> items(String.format("segment%03d", segment), 3, 5))
                            .toArray(Item[][]::new);

                    ScanRequest[] firstRequests = IntStream.range(0, segments)
                            .mapToObj(segment -> request(segment, segments))
                            .toArray(ScanRequest[]::new);
                    ScanRequest[] middleRequests = IntStream.range(0, segments)
                            .mapToObj(segment -> request(segment, segments, firstItems[segment][firstItems[segment].length - 1].serialize()))
                            .toArray(ScanRequest[]::new);
                    ScanRequest[] lastRequests = IntStream.range(0, segments)
                            .mapToObj(segment -> request(segment, segments, lastEvalKeyForEmpty[segment]))
                            .toArray(ScanRequest[]::new);

                    ScanResult[] firstResults= IntStream.range(0, segments)
                            .mapToObj(segment -> result(firstItems[segment][firstItems[segment].length - 1].serialize(), firstItems[segment]))
                            .toArray(ScanResult[]::new);
                    ScanResult[] emptyResults = IntStream.range(0, segments)
                            .mapToObj(segment -> result(lastEvalKeyForEmpty[segment]))
                            .toArray(ScanResult[]::new);
                    ScanResult[] lastResults= IntStream.range(0, segments)
                            .mapToObj(segment -> result(null, lastItems[segment]))
                            .toArray(ScanResult[]::new);

                    for (int i = 0; i < segments; ++i) {
                        when(amazonDynamoDbMock.scan(firstRequests[i])).thenReturn(firstResults[i]);
                        when(amazonDynamoDbMock.scan(middleRequests[i])).thenReturn(emptyResults[i]);
                        when(amazonDynamoDbMock.scan(lastRequests[i])).thenReturn(lastResults[i]);
                    }

                    ParallelScanReadResult<Item> classUnderTest = build(amazonDynamoDbMock, initialRequest(segments));
                    Stream<Item> stream = classUnderTest.stream();

                    assertThat(stream.collect(toList())).containsExactlyInAnyOrder(concat(concat(firstItems), concat(lastItems)));

                    // strange verification due to mutation of the request; paired with `when` this is still strong verification
                    for (int i = 0; i < segments; ++i) {
                        verify(amazonDynamoDbMock, times(3)).scan(lastRequests[i]);
                    }
                    verifyNoMoreInteractions(amazonDynamoDbMock);
                }));
    }

    @TestFactory
    Stream<DynamicTest> stream_sequential_streamContainsOnlyFirstSegment() {
        return IntStream.rangeClosed(MIN_SEGMENTS, MAX_SEGMENTS)
                .mapToObj(segments -> dynamicTest(testName(segments, "sequential"), () ->{
                    AmazonDynamoDB amazonDynamoDbMock = mock(AmazonDynamoDB.class);
                    Item[][] pages = IntStream.range(0, segments)
                            .mapToObj(segment -> items(String.format("segment%03d", segment), 0, 2))
                            .toArray(Item[][]::new);
                    ScanRequest[] requests = IntStream.range(0, segments)
                            .mapToObj(segment -> request(segment, segments))
                            .toArray(ScanRequest[]::new);

                    IntStream.range(0, segments)
                            .forEach(i -> when(amazonDynamoDbMock.scan(requests[i])).thenReturn(result(null, pages[i])));

                    ParallelScanReadResult<Item> classUnderTest = build(amazonDynamoDbMock, initialRequest(segments));
                    Stream<Item> scan = classUnderTest.stream().sequential();
                    List<Item> results = scan.collect(toList());

                    assertThat(results).containsExactlyInAnyOrder(pages[0]);

                    verify(amazonDynamoDbMock).scan(requests[0]);
                    verifyNoMoreInteractions(amazonDynamoDbMock);
                }));
    }

    @Test
    void stream_calledTwice_throwsIllegalStateException() {
        ParallelScanReadResult<Item> classUnderTest = build(mock(AmazonDynamoDB.class), initialRequest(MAX_SEGMENTS));
        classUnderTest.stream();
        assertThatThrownBy(() -> classUnderTest.stream()).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void spliterator_characteristics_nonNull() {
        Spliterator<Item> spliterator = build(mock(AmazonDynamoDB.class), initialRequest(MAX_SEGMENTS)).spliterator();
        assertThat(spliterator.characteristics()).isEqualTo(Spliterator.NONNULL);
    }

    @Test
    void spliterator_estimateSize_returnsMaxLong() {
        long size = build(mock(AmazonDynamoDB.class), initialRequest(MAX_SEGMENTS)).spliterator().estimateSize();
        assertThat(size).isEqualTo(Long.MAX_VALUE);
    }

    private ParallelScanReadResult<Item> build(AmazonDynamoDB amazonDynamoDb, ScanRequest scanRequest) {
        return new ParallelScanReadResult<Item>(amazonDynamoDb, scanRequest) {
            @Override
            protected Item deserialize(Map<String, AttributeValue> item) {
                return Item.deserialize(item);
            }
        };
    }

    private ScanRequest initialRequest(int totalSegments) {
        return request(0, totalSegments);
    }

    private ScanRequest request(int segment, int total) {
        return request(segment, total, null);
    }

    private ScanRequest request(int segment, int total, Map<String, AttributeValue> exclusiveStartKey) {
        return new ScanRequest()
                .withTableName("table-name")
                .withExclusiveStartKey(exclusiveStartKey)
                .withSegment(segment)
                .withTotalSegments(total);
    }

    private ScanResult result(Map<String, AttributeValue> lastEvaluatedKey, Item... items) {
        return new ScanResult()
                .withLastEvaluatedKey(lastEvaluatedKey)
                .withItems(Arrays.stream(items)
                        .map(Item::serialize)
                        .collect(toList()));
    }

    private String testName(int segment, String description) {
        return String.format("segment(%d) %s", segment, description);
    }

}
