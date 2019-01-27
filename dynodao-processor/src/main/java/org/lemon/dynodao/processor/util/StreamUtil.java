package org.lemon.dynodao.processor.util;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Spliterator;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toCollection;

/**
 * Utility methods for basic stream operations.
 */
@UtilityClass
public class StreamUtil {

    /**
     * Concatenates all of the objects into a single stream. If the passed object is a stream or collection, then
     * it is flattened into the returned stream.
     * @param objs the objects, or collections to be in the stream
     * @return the flattened single stream
     */
    @SuppressWarnings("unchecked")
    public static Stream<Object> concat(Object... objs) {
        return Arrays.stream(objs)
                .flatMap(obj -> {
                    if (obj instanceof Stream) {
                        return (Stream) obj;
                    } else if (obj instanceof Object[]) {
                        return Arrays.stream((Object[]) obj);
                    } else if (obj instanceof Collection) {
                        return ((Collection) obj).stream();
                    } else {
                        return Stream.of(obj);
                    }
                });
    }

    /**
     * Returns a {@link Collector} that accumulates the input elements into a {@link LinkedHashSet}.
     * @param <T> the type of the input elements
     * @return a {@link Collector} that accumulates the input elements into a {@link LinkedHashSet}
     */
    public static <T> Collector<T, ?, LinkedHashSet<T>> toLinkedHashSet() {
        return toCollection(LinkedHashSet::new);
    }

    /**
     * A utility interface to add a <tt>stream()</tt> method to the {@link Iterable} interface.
     * @param <T> the type of element in the stream
     */
    public interface Streamable<T> extends Iterable<T> {

        /**
         * A stream over this {@link Iterable}.
         * @return a stream
         * @see Collection#stream()
         * @see StreamSupport#stream(Spliterator, boolean)
         */
        default Stream<T> stream() {
            return StreamSupport.stream(spliterator(), false);
        }
    }

}
