package org.lemon.dynodao.internal;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.lemon.dynodao.test.AbstractUnitTest;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Spliterator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

class ScanReadResultTest extends AbstractUnitTest {

    @Data
    private static class Pojo {
        String hash;
        int range;
    }

    @Mock private AmazonDynamoDB amazonDynamoDbMock;

    @Test
    void ctor_onlyUseCase_requestCloned() {
        ScanRequest request = initialRequest();
        ScanResult result = result(null);
        ScanReadResult<Pojo> classUnderTest = build(request, result);

        assertThat(classUnderTest).extracting("scanRequest")
                .allSatisfy(r -> assertThat(r).isEqualTo(request).isNotSameAs(request));

        assertThat(classUnderTest).extracting("amazonDynamoDb", "scanResult", "iterationStarted")
                .containsExactly(amazonDynamoDbMock, result, false);
    }

    @Test
    void stream_initialScanResultIsEmpty_returnsEmptyStream() {
        ScanReadResult<Pojo> classUnderTest = build(initialRequest(), result(null));
        Stream<Pojo> scan = classUnderTest.stream();
        assertThat(scan).isEmpty();
        verifyZeroInteractions(amazonDynamoDbMock);
    }

    @Test
    void stream_initialScanResultContainsAllItems_returnsOrderedStream() {
        Pojo[] pojos = pojos("hash", 0, 2);
        ScanReadResult<Pojo> classUnderTest = build(initialRequest(), result(null, pojos));
        Stream<Pojo> scan = classUnderTest.stream();
        assertThat(scan).containsExactly(pojos);
        verifyZeroInteractions(amazonDynamoDbMock);
    }

    @Test
    void stream_scanPaginated_makesNewQueryAndReturnsOrderedStream() {
        Pojo[] initial = pojos("hash", 0, 2);
        Pojo[] middle = pojos("hash", 2, 4);
        Pojo[] last = pojos("hash", 4, 6);

        ScanRequest initialRequest = initialRequest();
        ScanRequest middleRequest = initialRequest().withExclusiveStartKey(serialize(initial[initial.length - 1]));
        ScanRequest lastRequest = initialRequest().withExclusiveStartKey(serialize(middle[middle.length - 1]));

        ScanResult initialResult = result(serialize(initial[initial.length - 1]), initial);
        ScanResult middleResult = result(serialize(middle[middle.length - 1]), middle);
        ScanResult lastResult = result(null, last);

        when(amazonDynamoDbMock.scan(middleRequest)).thenReturn(middleResult);
        when(amazonDynamoDbMock.scan(lastRequest)).thenReturn(lastResult);

        ScanReadResult<Pojo> classUnderTest = build(initialRequest, initialResult);
        Stream<Pojo> scan = classUnderTest.stream();

        assertThat(scan).containsExactly(pojos("hash", 0, 6));
        // strange verification due to mutation of the request; paired with `when` this is still strong verification
        verify(amazonDynamoDbMock, times(2)).scan(lastRequest);
        verifyNoMoreInteractions(amazonDynamoDbMock);
    }

    @Test
    void stream_calledTwice_throwsIllegalStateException() {
        ScanReadResult<Pojo> classUnderTest = build(initialRequest(), result(null));
        classUnderTest.stream();
        assertThatThrownBy(() -> classUnderTest.stream()).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void spliterator_characteristics_nonNull() {
        Spliterator<Pojo> spliterator = build(initialRequest(), result(null)).spliterator();
        assertThat(spliterator.characteristics()).isEqualTo(Spliterator.NONNULL);
    }

    @Test
    void spliterator_trySplit_returnsNull() {
        Spliterator<Pojo> spliterator = build(initialRequest(), result(null)).spliterator().trySplit();
        assertThat(spliterator).isNull();
    }

    @Test
    void spliterator_estimateSize_returnsMaxLong() {
        long size = build(initialRequest(), result(null)).spliterator().estimateSize();
        assertThat(size).isEqualTo(Long.MAX_VALUE);
    }

    private ScanReadResult<Pojo> build(ScanRequest scanRequest, ScanResult scanResult) {
        return new ScanReadResult<Pojo>(amazonDynamoDbMock, scanRequest, scanResult) {
            @Override
            protected Pojo deserialize(AttributeValue attributeValue) {
                return pojo(attributeValue.getM().get("hash").getS(), Integer.parseInt(attributeValue.getM().get("range").getN()));
            }
        };
    }

    private Pojo pojo(String hash, int range) {
        Pojo pojo = new Pojo();
        pojo.setHash(hash);
        pojo.setRange(range);
        return pojo;
    }

    private Pojo[] pojos(String hash, int rangeStartInclusive, int rangeEndExclusive) {
        return IntStream.range(rangeStartInclusive, rangeEndExclusive)
                .mapToObj(range -> pojo(hash, range))
                .toArray(Pojo[]::new);
    }

    private ScanRequest initialRequest() {
        return request(null);
    }

    private ScanRequest request(Map<String, AttributeValue> exclusiveStartKey) {
        return new ScanRequest()
                .withExclusiveStartKey(exclusiveStartKey)
                .withTableName("table-name");
    }

    private ScanResult result(Map<String, AttributeValue> lastEvaluatedKey, Pojo... pojos) {
        return new ScanResult()
                .withLastEvaluatedKey(lastEvaluatedKey)
                .withItems(Arrays.stream(pojos)
                        .map(this::serialize)
                        .collect(toList()));
    }

    private Map<String, AttributeValue> serialize(Pojo pojo) {
        Map<String, AttributeValue> map = new LinkedHashMap<>();
        map.put("hash", new AttributeValue(pojo.getHash()));
        map.put("range", new AttributeValue().withN(String.valueOf(pojo.getRange())));
        return map;
    }

}
