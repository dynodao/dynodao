package org.dynodao.internal;

import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import org.dynodao.annotation.DynoDaoSchema;

import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * The result of a {@code getItem} operation to DynamoDb.
 * @param <T> the type of item stored in DynamoDb, a {@link DynoDaoSchema @DynoDaoSchema} class.
 * @see <a href="https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/API_GetItem.html">AWS Documentation</a>
 */
public abstract class GetItemReadResult<T> extends AbstractReadResult<T> {

    private final GetItemResult getItemResult;

    /**
     * Sole ctor.
     * @param getItemResult the result of the getItem operation
     */
    protected GetItemReadResult(GetItemResult getItemResult) {
        this.getItemResult = getItemResult;
    }

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
            return Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.DISTINCT | Spliterator.ORDERED | Spliterator.NONNULL;
        }

    }

    @Override
    protected Spliterator<T> spliterator() {
        return new GetItemReadResultSpliterator();
    }

}
