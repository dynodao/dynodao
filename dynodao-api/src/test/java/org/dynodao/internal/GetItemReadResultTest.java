package org.dynodao.internal;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import org.dynodao.test.AbstractUnitTest;
import org.dynodao.test.Item;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Spliterator;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.dynodao.test.Item.item;

class GetItemReadResultTest extends AbstractUnitTest {

    @Test
    void ctor_onlyUseCase_fieldsSet() {
        GetItemResult result = result(item("hash", 0));
        GetItemReadResult<Item> classUnderTest = build(result);

        assertThat(classUnderTest).extracting("getItemResult")
                .containsExactly(result);
    }

    @Test
    void stream_getItemResultIsEmpty_returnsEmptyStream() {
        GetItemReadResult<Item> classUnderTest = build(emptyResult());
        Stream<Item> getItem = classUnderTest.stream();
        assertThat(getItem).isEmpty();
    }

    @Test
    void stream_getItemResultContainsItem_returnsSingletonStream() {
        Item item = item("hash", 0);
        GetItemReadResult<Item> classUnderTest = build(result(item));
        Stream<Item> getItem = classUnderTest.stream();
        assertThat(getItem).containsExactly(item);
    }

    @Test
    void spliterator_characteristics_nonNullSizedSubSizedDistinctAndOrdered() {
        Spliterator<Item> spliterator = build(emptyResult()).spliterator();
        assertThat(spliterator.characteristics()).isEqualTo(Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.DISTINCT | Spliterator.ORDERED | Spliterator.NONNULL);
    }

    @Test
    void spliterator_trySplit_returnsNull() {
        Spliterator<Item> spliterator = build(emptyResult()).spliterator().trySplit();
        assertThat(spliterator).isNull();
    }

    @Test
    void spliterator_estimateSizeEmpty_returnsZero() {
        long size = build(emptyResult()).spliterator().estimateSize();
        assertThat(size).isZero();
    }

    @Test
    void spliterator_estimateSizeNotConsumed_returnsOne() {
        long size = build(result(item("hash", 0))).spliterator().estimateSize();
        assertThat(size).isOne();
    }

    @Test
    void spliterator_estimateSizeConsumed_returnsZero() {
        Spliterator<Item> spliterator = build(result(item("hash", 0))).spliterator();
        spliterator.tryAdvance(item -> {});
        long size = spliterator.estimateSize();
        assertThat(size).isZero();
    }

    private GetItemReadResult<Item> build(GetItemResult getItemResult) {
        return new GetItemReadResult<Item>(getItemResult) {
            @Override
            protected Item deserialize(Map<String, AttributeValue> item) {
                return Item.deserialize(item);
            }
        };
    }

    private GetItemResult emptyResult() {
        return new GetItemResult();
    }

    private GetItemResult result(Item item) {
        return new GetItemResult()
                .withItem(item.serialize());
    }

}
