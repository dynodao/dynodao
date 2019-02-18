package org.lemon.dynodao.processor.schema.attribute;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The data type of an attribute in dynamo.
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum DynamoAttributeType {

    BINARY("B", true, true),
    BINARY_SET("BS", false, false),
    BOOLEAN("BOOL", true, false),
    LIST("L", false, false),
    MAP("M", false, false),
    NUMBER("N", true, true),
    NUMBER_SET("NS", false, false),
    NULL("NULL", true, false),
    STRING("S", true, true),
    STRING_SET("SS", false, false);

    /**
     * The corresponding data type name in dynamo, the short stupid name they use.
     */
    private final String dataTypeName;

    /**
     * Whether or not this type is considered "scalar." Scalar types can be used as range keys.
     */
    private final boolean scalar;

    /**
     * Whether or not this type is allowed to be a hash key.
     */
    private final boolean viableHashKey;

}
