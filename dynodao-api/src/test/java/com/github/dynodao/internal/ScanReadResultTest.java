package com.github.dynodao.internal;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.github.dynodao.test.AbstractUnitTest;
import com.github.dynodao.test.Item;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Map;
import java.util.Spliterator;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static com.github.dynodao.test.Item.items;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

class ScanReadResultTest extends AbstractUnitTest {

    @Mock private AmazonDynamoDB amazonDynamoDbMock;

    @Test
    void ctor_onlyUseCase_requestCloned() {
        ScanRequest request = initialRequest();
        ScanResult result = result(null);
        ScanReadResult<Item> classUnderTest = build(request, result);

        assertThat(classUnderTest).extracting("scanRequest")
                .allSatisfy(r -> assertThat(r).isEqualTo(request).isNotSameAs(request));

        assertThat(classUnderTest).extracting("amazonDynamoDb", "scanResult", "iterationStarted")
                .containsExactly(amazonDynamoDbMock, result, false);
    }

    @Test
    void stream_initialScanResultIsEmpty_returnsEmptyStream() {
        ScanReadResult<Item> classUnderTest = build(initialRequest(), result(null));
        Stream<Item> scan = classUnderTest.stream();
        assertThat(scan).isEmpty();
        verifyZeroInteractions(amazonDynamoDbMock);
    }

    @Test
    void stream_initialScanResultContainsAllItems_returnsOrderedStream() {
        Item[] items = items("hash", 0, 2);
        ScanReadResult<Item> classUnderTest = build(initialRequest(), result(null, items));
        Stream<Item> scan = classUnderTest.stream();
        assertThat(scan).containsExactly(items);
        verifyZeroInteractions(amazonDynamoDbMock);
    }

    @Test
    void stream_scanPaginated_makesNewQueryAndReturnsOrderedStream() {
        Item[] initial = items("hash", 0, 2);
        Item[] middle = items("hash", 2, 4);
        Item[] last = items("hash", 4, 6);

        ScanRequest initialRequest = initialRequest();
        ScanRequest middleRequest = initialRequest().withExclusiveStartKey(initial[initial.length - 1].serialize());
        ScanRequest lastRequest = initialRequest().withExclusiveStartKey(middle[middle.length - 1].serialize());

        ScanResult initialResult = result(initial[initial.length - 1].serialize(), initial);
        ScanResult middleResult = result(middle[middle.length - 1].serialize(), middle);
        ScanResult lastResult = result(null, last);

        when(amazonDynamoDbMock.scan(middleRequest)).thenReturn(middleResult);
        when(amazonDynamoDbMock.scan(lastRequest)).thenReturn(lastResult);

        ScanReadResult<Item> classUnderTest = build(initialRequest, initialResult);
        Stream<Item> scan = classUnderTest.stream();

        assertThat(scan).containsExactly(items("hash", 0, 6));
        // strange verification due to mutation of the request; paired with `when` this is still strong verification
        verify(amazonDynamoDbMock, times(2)).scan(lastRequest);
        verifyNoMoreInteractions(amazonDynamoDbMock);
    }

    @Test
    void stream_calledTwice_throwsIllegalStateException() {
        ScanReadResult<Item> classUnderTest = build(initialRequest(), result(null));
        classUnderTest.stream();
        assertThatThrownBy(() -> classUnderTest.stream()).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void spliterator_characteristics_nonNull() {
        Spliterator<Item> spliterator = build(initialRequest(), result(null)).spliterator();
        assertThat(spliterator.characteristics()).isEqualTo(Spliterator.NONNULL);
    }

    @Test
    void spliterator_trySplit_returnsNull() {
        Spliterator<Item> spliterator = build(initialRequest(), result(null)).spliterator().trySplit();
        assertThat(spliterator).isNull();
    }

    @Test
    void spliterator_estimateSize_returnsMaxLong() {
        long size = build(initialRequest(), result(null)).spliterator().estimateSize();
        assertThat(size).isEqualTo(Long.MAX_VALUE);
    }

    private ScanReadResult<Item> build(ScanRequest scanRequest, ScanResult scanResult) {
        return new ScanReadResult<Item>(amazonDynamoDbMock, scanRequest, scanResult) {
            @Override
            protected Item deserialize(Map<String, AttributeValue> item) {
                return Item.deserialize(item);
            }
        };
    }

    private ScanRequest initialRequest() {
        return request(null);
    }

    private ScanRequest request(Map<String, AttributeValue> exclusiveStartKey) {
        return new ScanRequest()
                .withExclusiveStartKey(exclusiveStartKey)
                .withTableName("table-name");
    }

    private ScanResult result(Map<String, AttributeValue> lastEvaluatedKey, Item... items) {
        return new ScanResult()
                .withLastEvaluatedKey(lastEvaluatedKey)
                .withItems(Arrays.stream(items)
                        .map(Item::serialize)
                        .collect(toList()));
    }

}
