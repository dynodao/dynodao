package org.dynodao.processor.test.params;

import org.junit.jupiter.params.provider.MethodSource;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.stream.IntStream;

/**
 * Parameter sources containing the total number of segments for parallel scan tests.
 */
@Retention(RetentionPolicy.RUNTIME)
@MethodSource("org.dynodao.processor.test.params.ParallelScanTotalSegmentsSourceHolder#totalSegmentsSource")
public @interface ParallelScanTotalSegmentsSource {

}

@SuppressWarnings("unused")
class ParallelScanTotalSegmentsSourceHolder {

    private static int[] totalSegmentsSource() {
        int min = Math.max(2, Runtime.getRuntime().availableProcessors() - 1);
        int max = Math.min(min + 2, Runtime.getRuntime().availableProcessors() + 1);
        return IntStream.rangeClosed(min, max).toArray();
    }

}
