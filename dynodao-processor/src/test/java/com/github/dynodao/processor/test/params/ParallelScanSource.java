package com.github.dynodao.processor.test.params;

import lombok.experimental.UtilityClass;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.stream.IntStream;

/**
 * Parameter sources for use in parallel scan tests.
 */
@UtilityClass
public class ParallelScanSource {

    /**
     * Parameter sources for the total number of segments in parallel scan.
     */
    @Documented
    @Target({ ElementType.ANNOTATION_TYPE, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    @MethodSource("com.github.dynodao.processor.test.params.ParallelScanSource#totalSegmentsSource")
    public @interface TotalSegments { }

    private static int[] totalSegmentsSource() {
        int min = Math.max(2, Runtime.getRuntime().availableProcessors() - 1);
        int max = Math.min(min + 2, Runtime.getRuntime().availableProcessors() + 1);
        return IntStream.rangeClosed(min, max).toArray();
    }

}
