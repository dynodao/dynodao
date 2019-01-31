package org.lemon.dynodao.internal;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import lombok.RequiredArgsConstructor;

import java.util.Spliterator;
import java.util.function.Consumer;

@RequiredArgsConstructor
public abstract class GetItemReadResult<T> extends AbstractReadResult<T> {

    private final GetItemResult getItemResult;

    private class GetItemReadResultSpliterator implements Spliterator<T> {

        private long size = getItemResult.getItem() == null ? 0 : 1;

        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            if (size > 0) {
                --size;
                action.accept(deserialize(getItemResult.getItem()));
                return true;
            } else {
                return false;
            }
        }

        @Override
        public Spliterator<T> trySplit() {
            return null;
        }

        @Override
        public long estimateSize() {
            return size;
        }

        @Override
        public int characteristics() {
            return Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.IMMUTABLE | Spliterator.DISTINCT | Spliterator.ORDERED | Spliterator.NONNULL;
        }
    }

    @Override
    public Spliterator<T> spliterator() {
        return new GetItemReadResultSpliterator();
    }

}
