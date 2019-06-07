package org.dynodao.processor.test.params;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.dynodao.processor.schema.attribute.DynamoAttributeType;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.util.stream.Stream;

import static java.util.Collections.singletonMap;

/**
 * Stores common parameters for {@link AttributeValue} parameterized tests.
 */
@UtilityClass
public class AttributeValueSource {
    
    /**
     * Parameter sources containing an {@link AttributeValue} all types except <tt>B</tt>.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @MethodSource("org.dynodao.processor.test.params.AttributeValueSource#attributeValuesWithoutBinarySource")
    public @interface WithoutBinary { }

    private static Stream<AttributeValue> attributeValuesWithoutBinarySource() {
        return attributeValuesWithoutType(DynamoAttributeType.BINARY);
    }

    /**
     * Parameter sources containing an {@link AttributeValue} all types except <tt>BS</tt>.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @MethodSource("org.dynodao.processor.test.params.AttributeValueSource#attributeValuesWithoutBinarySetSource")
    public @interface WithoutBinarySet { }

    private static Stream<AttributeValue> attributeValuesWithoutBinarySetSource() {
        return attributeValuesWithoutType(DynamoAttributeType.BINARY_SET);
    }

    /**
     * Parameter sources containing an {@link AttributeValue} all types except <tt>BOOL</tt>.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @MethodSource("org.dynodao.processor.test.params.AttributeValueSource#attributeValuesWithoutBooleanSource")
    public @interface WithoutBoolean { }

    private static Stream<AttributeValue> attributeValuesWithoutBooleanSource() {
        return attributeValuesWithoutType(DynamoAttributeType.BOOLEAN);
    }

    /**
     * Parameter sources containing an {@link AttributeValue} all types except <tt>L</tt>.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @MethodSource("org.dynodao.processor.test.params.AttributeValueSource#attributeValuesWithoutListSource")
    public @interface WithoutList { }

    private static Stream<AttributeValue> attributeValuesWithoutListSource() {
        return attributeValuesWithoutType(DynamoAttributeType.LIST);
    }

    /**
     * Parameter sources containing an {@link AttributeValue} all types except <tt>M</tt>.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @MethodSource("org.dynodao.processor.test.params.AttributeValueSource#attributeValuesWithoutMapSource")
    public @interface WithoutMap { }

    private static Stream<AttributeValue> attributeValuesWithoutMapSource() {
        return attributeValuesWithoutType(DynamoAttributeType.MAP);
    }

    /**
     * Parameter sources containing an {@link AttributeValue} all types except <tt>NULL</tt>.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @MethodSource("org.dynodao.processor.test.params.AttributeValueSource#attributeValuesWithoutNullSource")
    public @interface WithoutNull { }

    private static Stream<AttributeValue> attributeValuesWithoutNullSource() {
        return attributeValuesWithoutType(DynamoAttributeType.NULL);
    }

    /**
     * Parameter sources containing an {@link AttributeValue} all types except <tt>N</tt>.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @MethodSource("org.dynodao.processor.test.params.AttributeValueSource#attributeValuesWithoutNumberSource")
    public @interface WithoutNumber { }

    private static Stream<AttributeValue> attributeValuesWithoutNumberSource() {
        return attributeValuesWithoutType(DynamoAttributeType.NUMBER);
    }

    /**
     * Parameter sources containing an {@link AttributeValue} all types except <tt>NS</tt>.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @MethodSource("org.dynodao.processor.test.params.AttributeValueSource#attributeValuesWithoutNumberSetSource")
    public @interface WithoutNumberSet { }

    private static Stream<AttributeValue> attributeValuesWithoutNumberSetSource() {
        return attributeValuesWithoutType(DynamoAttributeType.NUMBER_SET);
    }

    /**
     * Parameter sources containing an {@link AttributeValue} all types except <tt>S</tt>.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @MethodSource("org.dynodao.processor.test.params.AttributeValueSource#attributeValuesWithoutStringSource")
    public @interface WithoutString { }

    private static Stream<AttributeValue> attributeValuesWithoutStringSource() {
        return attributeValuesWithoutType(DynamoAttributeType.STRING);
    }

    /**
     * Parameter sources containing an {@link AttributeValue} all types except <tt>SS</tt>.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @MethodSource("org.dynodao.processor.test.params.AttributeValueSource#attributeValuesWithoutStringSetSource")
    public @interface WithoutStringSet { }

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
