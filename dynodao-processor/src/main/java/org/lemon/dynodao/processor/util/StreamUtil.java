package org.lemon.dynodao.processor.util;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

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
}
