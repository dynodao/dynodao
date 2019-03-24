package org.dynodao.processor.test;

import lombok.experimental.UtilityClass;

import java.util.stream.IntStream;

/**
 * Stores common parameters for parameterized tests.
 */
@UtilityClass
@SuppressWarnings("unused")
public class ParameterizedTestSources {

    public static final String TOTAL_SEGMENTS_METHOD_SOURCE = "org.dynodao.processor.test.ParameterizedTestSources#totalSegmentsSource";

    private static final int MIN_SEGMENTS = Math.max(2, Runtime.getRuntime().availableProcessors() - 1);
    private static final int MAX_SEGMENTS = Math.min(MIN_SEGMENTS + 2, Runtime.getRuntime().availableProcessors() + 1);
    private static final int[] TOTAL_SEGMENTS = IntStream.rangeClosed(MIN_SEGMENTS, MAX_SEGMENTS).toArray();

    /**
     * Returns the parameters for the number of total segments in parallel scan tests.
     * @return the parameters for total segments
     */
    public static int[] totalSegmentsSource() {
        return TOTAL_SEGMENTS;
    }

}
