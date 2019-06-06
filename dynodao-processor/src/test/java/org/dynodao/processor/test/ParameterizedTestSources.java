package org.dynodao.processor.test;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.dynodao.processor.schema.attribute.DynamoAttributeType;

import java.nio.ByteBuffer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Collections.singletonMap;

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

    private static final AttributeValue BINARY_ATTRIBUTE_VALUE = new AttributeValue().withB(ByteBuffer.wrap("binary".getBytes()));
    private static final AttributeValue BINARY_SET_ATTRIBUTE_VALUE = new AttributeValue().withBS(ByteBuffer.wrap("binary".getBytes()), ByteBuffer.wrap("set".getBytes()));
    private static final AttributeValue BOOLEAN_ATTRIBUTE_VALUE = new AttributeValue().withBOOL(true);
    private static final AttributeValue LIST_ATTRIBUTE_VALUE = new AttributeValue().withL(new AttributeValue().withS("list"));
    private static final AttributeValue MAP_ATTRIBUTE_VALUE = new AttributeValue().withM(singletonMap("key", new AttributeValue().withS("map")));
    private static final AttributeValue NULL_ATTRIBUTE_VALUE = new AttributeValue().withNULL(true);
    private static final AttributeValue NUMBER_ATTRIBUTE_VALUE = new AttributeValue().withN("0.0");
    private static final AttributeValue NUMBER_SET_ATTRIBUTE_VALUE = new AttributeValue().withNS("-1", "0", "1");
    private static final AttributeValue STRING_ATTRIBUTE_VALUE = new AttributeValue().withS("string");
    private static final AttributeValue STRING_SET_ATTRIBUTE_VALUE = new AttributeValue().withSS("string", "set");

    public static final String ATTRIBUTE_VALUES_WITHOUT_BINARY_SOURCE = "org.dynodao.processor.test.ParameterizedTestSources#attributeValuesWithoutBinarySource";
    public static final String ATTRIBUTE_VALUES_WITHOUT_BINARY_SET_SOURCE = "org.dynodao.processor.test.ParameterizedTestSources#attributeValuesWithoutBinarySetSource";
    public static final String ATTRIBUTE_VALUES_WITHOUT_BOOLEAN_SOURCE = "org.dynodao.processor.test.ParameterizedTestSources#attributeValuesWithoutBooleanSource";
    public static final String ATTRIBUTE_VALUES_WITHOUT_LIST_SOURCE = "org.dynodao.processor.test.ParameterizedTestSources#attributeValuesWithoutListSource";
    public static final String ATTRIBUTE_VALUES_WITHOUT_MAP_SOURCE = "org.dynodao.processor.test.ParameterizedTestSources#attributeValuesWithoutMapSource";
    public static final String ATTRIBUTE_VALUES_WITHOUT_NULL_SOURCE = "org.dynodao.processor.test.ParameterizedTestSources#attributeValuesWithoutNullSource";
    public static final String ATTRIBUTE_VALUES_WITHOUT_NUMBER_SOURCE = "org.dynodao.processor.test.ParameterizedTestSources#attributeValuesWithoutNumberSource";
    public static final String ATTRIBUTE_VALUES_WITHOUT_NUMBER_SET_SOURCE = "org.dynodao.processor.test.ParameterizedTestSources#attributeValuesWithoutNumberSetSource";
    public static final String ATTRIBUTE_VALUES_WITHOUT_STRING_SOURCE = "org.dynodao.processor.test.ParameterizedTestSources#attributeValuesWithoutStringSource";
    public static final String ATTRIBUTE_VALUES_WITHOUT_STRING_SET_SOURCE = "org.dynodao.processor.test.ParameterizedTestSources#attributeValuesWithoutStringSetSource";

    /**
     * Returns {@link AttributeValue} parameter sources of all types except <tt>B</tt>.
     * @return AttributeValue parameters of all types exception <tt>B</tt>
     */
    public static Stream<AttributeValue> attributeValuesWithoutBinarySource() {
        return attributeValuesWithoutType(DynamoAttributeType.BINARY);
    }

    /**
     * Returns {@link AttributeValue} parameter sources of all types except <tt>BS</tt>.
     * @return AttributeValue parameters of all types exception <tt>BS</tt>
     */
    public static Stream<AttributeValue> attributeValuesWithoutBinarySetSource() {
        return attributeValuesWithoutType(DynamoAttributeType.BINARY_SET);
    }

    /**
     * Returns {@link AttributeValue} parameter sources of all types except <tt>BOOL</tt>.
     * @return AttributeValue parameters of all types exception <tt>BOOL</tt>
     */
    public static Stream<AttributeValue> attributeValuesWithoutBooleanSource() {
        return attributeValuesWithoutType(DynamoAttributeType.BOOLEAN);
    }

    /**
     * Returns {@link AttributeValue} parameter sources of all types except <tt>L</tt>.
     * @return AttributeValue parameters of all types exception <tt>L</tt>
     */
    public static Stream<AttributeValue> attributeValuesWithoutListSource() {
        return attributeValuesWithoutType(DynamoAttributeType.LIST);
    }

    /**
     * Returns {@link AttributeValue} parameter sources of all types except <tt>M</tt>.
     * @return AttributeValue parameters of all types exception <tt>M</tt>
     */
    public static Stream<AttributeValue> attributeValuesWithoutMapSource() {
        return attributeValuesWithoutType(DynamoAttributeType.MAP);
    }

    /**
     * Returns {@link AttributeValue} parameter sources of all types except <tt>NULL</tt>.
     * @return AttributeValue parameters of all types exception <tt>NULL</tt>
     */
    public static Stream<AttributeValue> attributeValuesWithoutNullSource() {
        return attributeValuesWithoutType(DynamoAttributeType.NULL);
    }

    /**
     * Returns {@link AttributeValue} parameter sources of all types except <tt>N</tt>.
     * @return AttributeValue parameters of all types exception <tt>N</tt>
     */
    public static Stream<AttributeValue> attributeValuesWithoutNumberSource() {
        return attributeValuesWithoutType(DynamoAttributeType.NUMBER);
    }

    /**
     * Returns {@link AttributeValue} parameter sources of all types except <tt>NS</tt>.
     * @return AttributeValue parameters of all types exception <tt>NS</tt>
     */
    public static Stream<AttributeValue> attributeValuesWithoutNumberSetSource() {
        return attributeValuesWithoutType(DynamoAttributeType.NUMBER_SET);
    }

    /**
     * Returns {@link AttributeValue} parameter sources of all types except <tt>S</tt>.
     * @return AttributeValue parameters of all types exception <tt>S</tt>
     */
    public static Stream<AttributeValue> attributeValuesWithoutStringSource() {
        return attributeValuesWithoutType(DynamoAttributeType.STRING);
    }

    /**
     * Returns {@link AttributeValue} parameter sources of all types except <tt>SS</tt>.
     * @return AttributeValue parameters of all types exception <tt>SS</tt>
     */
    public static Stream<AttributeValue> attributeValuesWithoutStringSetSource() {
        return attributeValuesWithoutType(DynamoAttributeType.STRING_SET);
    }

    private static Stream<AttributeValue> attributeValuesWithoutType(DynamoAttributeType dynamoAttributeType) {
        return Stream.of(
                BINARY_ATTRIBUTE_VALUE, BINARY_SET_ATTRIBUTE_VALUE, BOOLEAN_ATTRIBUTE_VALUE, LIST_ATTRIBUTE_VALUE, MAP_ATTRIBUTE_VALUE,
                NULL_ATTRIBUTE_VALUE, NUMBER_ATTRIBUTE_VALUE, NUMBER_SET_ATTRIBUTE_VALUE, STRING_ATTRIBUTE_VALUE, STRING_SET_ATTRIBUTE_VALUE
        )
                .filter(attributeValue -> !attributeValueIsType(attributeValue, dynamoAttributeType));
    }

    @SneakyThrows(ReflectiveOperationException.class)
    private static boolean attributeValueIsType(AttributeValue attributeValue, DynamoAttributeType dynamoAttributeType) {
        String methodName = "get" + dynamoAttributeType.getDataTypeName();
        return AttributeValue.class.getMethod(methodName).invoke(attributeValue) != null;
    }

}
