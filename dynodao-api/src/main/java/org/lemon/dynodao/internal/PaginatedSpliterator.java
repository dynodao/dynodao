package org.lemon.dynodao.internal;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Spliterator;
import java.util.function.Consumer;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
abstract class PaginatedSpliterator<T> implements Spliterator<T> {

    private Spliterator<T> items;

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if (items.tryAdvance(action)) {
            return true;
        } else if ((items = loadNextPage()) != null) {
            return items.tryAdvance(action);
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
        return Long.MAX_VALUE;
    }

    @Override
    public int characteristics() {
        return Spliterator.NONNULL | Spliterator.ORDERED;
    }

    abstract Spliterator<T> loadNextPage();

}
