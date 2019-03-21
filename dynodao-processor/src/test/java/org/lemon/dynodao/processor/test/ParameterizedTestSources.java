package org.lemon.dynodao.processor.test;

import lombok.experimental.UtilityClass;

import java.util.stream.IntStream;

/**
 * Stores common parameters for parameterized tests.
 */
@UtilityClass
@SuppressWarnings("unused")
public class ParameterizedTestSources {

    public static final String TOTAL_SEGMENTS_METHOD_SOURCE = "org.lemon.dynodao.processor.test.ParameterizedTestSources#totalSegmentsSource";

    private static final int[] TOTAL_SEGMENTS = IntStream.rangeClosed(
            Runtime.getRuntime().availableProcessors() - 1, Runtime.getRuntime().availableProcessors() + 1)
            .toArray();

    /**
     * Returns the parameters for the number of total segments in parallel scan tests.
     * @return the parameters for total segments
     */
    public static int[] totalSegmentsSource() {
        return TOTAL_SEGMENTS;
    }

}
