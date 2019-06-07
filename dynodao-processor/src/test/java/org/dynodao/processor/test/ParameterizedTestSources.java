package org.dynodao.processor.test;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.dynodao.processor.schema.attribute.DynamoAttributeType;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.ByteBuffer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Collections.singletonMap;

/**
 * Stores common parameters for parameterized tests.
 */
@UtilityClass
public class ParameterizedTestSources {

    /**
     * Parameter sources containing the total number of segments for parallel scan tests.
     */
    @MethodSource("org.dynodao.processor.test.ParameterizedTestSources#totalSegmentsSource")
    public @interface ParallelScanTotalSegmentsSource { }

    private static int[] totalSegmentsSource() {
        int min = Math.max(2, Runtime.getRuntime().availableProcessors() - 1);
        int max = Math.min(min + 2, Runtime.getRuntime().availableProcessors() + 1);
        return IntStream.rangeClosed(min, max).toArray();
    }

    /**
     * Parameter sources containing an {@link AttributeValue} all types except <tt>B</tt>.
     */
    @MethodSource("org.dynodao.processor.test.ParameterizedTestSources#attributeValuesWithoutBinarySource")
    public @interface AttributeValuesWithoutBinarySource { }

    private static Stream<AttributeValue> attributeValuesWithoutBinarySource() {
        return attributeValuesWithoutType(DynamoAttributeType.BINARY);
    }

    /**
     * Parameter sources containing an {@link AttributeValue} all types except <tt>BS</tt>.
     */
    @MethodSource("org.dynodao.processor.test.ParameterizedTestSources#attributeValuesWithoutBinarySetSource")
    public @interface AttributeValuesWithoutBinarySetSource { }

    private static Stream<AttributeValue> attributeValuesWithoutBinarySetSource() {
        return attributeValuesWithoutType(DynamoAttributeType.BINARY_SET);
    }

    /**
     * Parameter sources containing an {@link AttributeValue} all types except <tt>BOOL</tt>.
     */
    @MethodSource("org.dynodao.processor.test.ParameterizedTestSources#attributeValuesWithoutBooleanSource")
    public @interface AttributeValuesWithoutBooleanSource { }

    private static Stream<AttributeValue> attributeValuesWithoutBooleanSource() {
        return attributeValuesWithoutType(DynamoAttributeType.BOOLEAN);
    }

    /**
     * Parameter sources containing an {@link AttributeValue} all types except <tt>L</tt>.
     */
    @MethodSource("org.dynodao.processor.test.ParameterizedTestSources#attributeValuesWithoutListSource")
    public @interface AttributeValuesWithoutListSource { }

    private static Stream<AttributeValue> attributeValuesWithoutListSource() {
        return attributeValuesWithoutType(DynamoAttributeType.LIST);
    }

    /**
     * Parameter sources containing an {@link AttributeValue} all types except <tt>M</tt>.
     */
    @MethodSource("org.dynodao.processor.test.ParameterizedTestSources#attributeValuesWithoutMapSource")
    public @interface AttributeValuesWithoutMapSource { }

    private static Stream<AttributeValue> attributeValuesWithoutMapSource() {
        return attributeValuesWithoutType(DynamoAttributeType.MAP);
    }

    /**
     * Parameter sources containing an {@link AttributeValue} all types except <tt>NULL</tt>.
     */
    @MethodSource("org.dynodao.processor.test.ParameterizedTestSources#attributeValuesWithoutNullSource")
    public @interface AttributeValuesWithoutNullSource { }

    private static Stream<AttributeValue> attributeValuesWithoutNullSource() {
        return attributeValuesWithoutType(DynamoAttributeType.NULL);
    }

    /**
     * Parameter sources containing an {@link AttributeValue} all types except <tt>N</tt>.
     */
    @MethodSource("org.dynodao.processor.test.ParameterizedTestSources#attributeValuesWithoutNumberSource")
    public @interface AttributeValuesWithoutNumberSource { }

    private static Stream<AttributeValue> attributeValuesWithoutNumberSource() {
        return attributeValuesWithoutType(DynamoAttributeType.NUMBER);
    }

    /**
     * Parameter sources containing an {@link AttributeValue} all types except <tt>NS</tt>.
     */
    @MethodSource("org.dynodao.processor.test.ParameterizedTestSources#attributeValuesWithoutNumberSetSource")
    public @interface AttributeValuesWithoutNumberSetSource { }

    private static Stream<AttributeValue> attributeValuesWithoutNumberSetSource() {
        return attributeValuesWithoutType(DynamoAttributeType.NUMBER_SET);
    }

    /**
     * Parameter sources containing an {@link AttributeValue} all types except <tt>S</tt>.
     */
    @MethodSource("org.dynodao.processor.test.ParameterizedTestSources#attributeValuesWithoutStringSource")
    public @interface AttributeValuesWithoutStringSource { }

    private static Stream<AttributeValue> attributeValuesWithoutStringSource() {
        return attributeValuesWithoutType(DynamoAttributeType.STRING);
    }

    /**
     * Parameter sources containing an {@link AttributeValue} all types except <tt>SS</tt>.
     */
    @MethodSource("org.dynodao.processor.test.ParameterizedTestSources#attributeValuesWithoutStringSetSource")
    public @interface AttributeValuesWithoutStringSetSource { }

    private static Stream<AttributeValue> attributeValuesWithoutStringSetSource() {
        return attributeValuesWithoutType(DynamoAttributeType.STRING_SET);
    }

    private static Stream<AttributeValue> attributeValuesWithoutType(DynamoAttributeType dynamoAttributeType) {
        return Stream.of(
                new AttributeValue().withB(ByteBuffer.wrap("binary".getBytes())),
                new AttributeValue().withBS(ByteBuffer.wrap("binary".getBytes()), ByteBuffer.wrap("set".getBytes())),
                new AttributeValue().withBOOL(true),
                new AttributeValue().withL(new AttributeValue().withS("list")),
                new AttributeValue().withM(singletonMap("key", new AttributeValue().withS("map"))),
                new AttributeValue().withNULL(true),
                new AttributeValue().withN("0.0"),
                new AttributeValue().withNS("-1", "0", "1"),
                new AttributeValue().withS("string"),
                new AttributeValue().withSS("string", "set")
        )
                .filter(attributeValue -> !attributeValueIsType(attributeValue, dynamoAttributeType));
    }

    @SneakyThrows(ReflectiveOperationException.class)
    private static boolean attributeValueIsType(AttributeValue attributeValue, DynamoAttributeType dynamoAttributeType) {
        String methodName = "get" + dynamoAttributeType.getDataTypeName();
        return AttributeValue.class.getMethod(methodName).invoke(attributeValue) != null;
    }

}
