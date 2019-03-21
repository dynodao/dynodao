package org.lemon.dynodao.internal;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import org.junit.jupiter.api.Test;
import org.lemon.dynodao.test.AbstractUnitTest;
import org.lemon.dynodao.test.Item;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Map;
import java.util.Spliterator;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.lemon.dynodao.test.Item.items;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

class QueryReadResultTest extends AbstractUnitTest {

    @Mock private AmazonDynamoDB amazonDynamoDbMock;

    @Test
    void ctor_onlyUseCase_requestCloned() {
        QueryRequest request = initialRequest();
        QueryResult result = result(null);
        QueryReadResult<Item> classUnderTest = build(request, result);

        assertThat(classUnderTest).extracting("queryRequest")
                .allSatisfy(r -> assertThat(r).isEqualTo(request).isNotSameAs(request));

        assertThat(classUnderTest).extracting("amazonDynamoDb", "queryResult", "iterationStarted")
                .containsExactly(amazonDynamoDbMock, result, false);
    }

    @Test
    void stream_initialQueryResultIsEmpty_returnsEmptyStream() {
        QueryReadResult<Item> classUnderTest = build(initialRequest(), result(null));
        Stream<Item> query = classUnderTest.stream();
        assertThat(query).isEmpty();
        verifyZeroInteractions(amazonDynamoDbMock);
    }

    @Test
    void stream_initialQueryResultContainsAllItems_returnsOrderedStream() {
        Item[] items = items("hash", 0, 2);
        QueryReadResult<Item> classUnderTest = build(initialRequest(), result(null, items));
        Stream<Item> query = classUnderTest.stream();
        assertThat(query).containsExactly(items);
        verifyZeroInteractions(amazonDynamoDbMock);
    }

    @Test
    void stream_queryPaginated_makesNewQueryAndReturnsOrderedStream() {
        Item[] initial = items("hash", 0, 2);
        Item[] middle = items("hash", 2, 4);
        Item[] last = items("hash", 4, 6);

        QueryRequest initialRequest = initialRequest();
        QueryRequest middleRequest = initialRequest().withExclusiveStartKey(initial[initial.length - 1].serialize());
        QueryRequest lastRequest = initialRequest().withExclusiveStartKey(middle[middle.length - 1].serialize());

        QueryResult initialResult = result(initial[initial.length - 1].serialize(), initial);
        QueryResult middleResult = result(middle[middle.length - 1].serialize(), middle);
        QueryResult lastResult = result(null, last);

        when(amazonDynamoDbMock.query(middleRequest)).thenReturn(middleResult);
        when(amazonDynamoDbMock.query(lastRequest)).thenReturn(lastResult);

        QueryReadResult<Item> classUnderTest = build(initialRequest, initialResult);
        Stream<Item> query = classUnderTest.stream();

        assertThat(query).containsExactly(items("hash", 0, 6));
        // strange verification due to mutation of the request; paired with `when` this is still strong verification
        verify(amazonDynamoDbMock, times(2)).query(lastRequest);
        verifyNoMoreInteractions(amazonDynamoDbMock);
    }

    @Test
    void stream_calledTwice_throwsIllegalStateException() {
        QueryReadResult<Item> classUnderTest = build(initialRequest(), result(null));
        classUnderTest.stream();
        assertThatThrownBy(() -> classUnderTest.stream()).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void spliterator_characteristics_nonNullOrderedAndDistinct() {
        Spliterator<Item> spliterator = build(initialRequest(), result(null)).spliterator();
        assertThat(spliterator.characteristics()).isEqualTo(Spliterator.NONNULL | Spliterator.ORDERED | Spliterator.DISTINCT);
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

    private QueryReadResult<Item> build(QueryRequest queryRequest, QueryResult queryResult) {
        return new QueryReadResult<Item>(amazonDynamoDbMock, queryRequest, queryResult) {
            @Override
            protected Item deserialize(Map<String, AttributeValue> item) {
                return Item.deserialize(item);
            }
        };
    }

    private QueryRequest initialRequest() {
        return request(null);
    }

    private QueryRequest request(Map<String, AttributeValue> exclusiveStartKey) {
        return new QueryRequest()
                .withExclusiveStartKey(exclusiveStartKey)
                .withTableName("table-name");
    }

    private QueryResult result(Map<String, AttributeValue> lastEvaluatedKey, Item... items) {
        return new QueryResult()
                .withLastEvaluatedKey(lastEvaluatedKey)
                .withItems(Arrays.stream(items)
                        .map(Item::serialize)
                        .collect(toList()));
    }

}
