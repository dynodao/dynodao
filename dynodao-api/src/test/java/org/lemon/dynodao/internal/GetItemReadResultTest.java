package org.lemon.dynodao.internal;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.lemon.dynodao.test.AbstractUnitTest;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Spliterator;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class GetItemReadResultTest extends AbstractUnitTest {

    @Data
    private static class Pojo {
        String hash;
    }

    @Test
    void ctor_onlyUseCase_fieldsSet() {
        GetItemResult result = result(pojo("hash"));
        GetItemReadResult<Pojo> classUnderTest = build(result);

        assertThat(classUnderTest).extracting("getItemResult")
                .containsExactly(result);
    }

    @Test
    void stream_getItemResultIsEmpty_returnsEmptyStream() {
        GetItemReadResult<Pojo> classUnderTest = build(emptyResult());
        Stream<Pojo> getItem = classUnderTest.stream();
        assertThat(getItem).isEmpty();
    }

    @Test
    void stream_getItemResultContainsItem_returnsSingletonStream() {
        Pojo pojo = pojo("hash");
        GetItemReadResult<Pojo> classUnderTest = build(result(pojo));
        Stream<Pojo> getItem = classUnderTest.stream();
        assertThat(getItem).containsExactly(pojo);
    }

    @Test
    void spliterator_characteristics_nonNullSizedSubSizedDistinctAndOrdered() {
        Spliterator<Pojo> spliterator = build(emptyResult()).spliterator();
        assertThat(spliterator.characteristics()).isEqualTo(Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.DISTINCT | Spliterator.ORDERED | Spliterator.NONNULL);
    }

    @Test
    void spliterator_trySplit_returnsNull() {
        Spliterator<Pojo> spliterator = build(emptyResult()).spliterator().trySplit();
        assertThat(spliterator).isNull();
    }

    @Test
    void spliterator_estimateSizeEmpty_returnsZero() {
        long size = build(emptyResult()).spliterator().estimateSize();
        assertThat(size).isZero();
    }

    @Test
    void spliterator_estimateSizeNotConsumed_returnsOne() {
        long size = build(result(pojo("hash"))).spliterator().estimateSize();
        assertThat(size).isOne();
    }

    @Test
    void spliterator_estimateSizeConsumed_returnsZero() {
        Spliterator<Pojo> spliterator = build(result(pojo("hash"))).spliterator();
        spliterator.tryAdvance(item -> {});
        long size = spliterator.estimateSize();
        assertThat(size).isZero();
    }

    private GetItemReadResult<Pojo> build(GetItemResult getItemResult) {
        return new GetItemReadResult<Pojo>(getItemResult) {
            @Override
            protected Pojo deserialize(AttributeValue attributeValue) {
                return pojo(attributeValue.getM().get("hash").getS());
            }
        };
    }

    private Pojo pojo(String hash) {
        Pojo pojo = new Pojo();
        pojo.setHash(hash);
        return pojo;
    }

    private GetItemResult emptyResult() {
        return new GetItemResult();
    }

    private GetItemResult result(Pojo pojo) {
        return new GetItemResult()
                .withItem(serialize(pojo));
    }

    private Map<String, AttributeValue> serialize(Pojo pojo) {
        Map<String, AttributeValue> map = new LinkedHashMap<>();
        map.put("hash", new AttributeValue(pojo.getHash()));
        return map;
    }

}
