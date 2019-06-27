package com.github.dynodao.processor.schema.attribute;

/**
 * A visitor of attributes.
 * @param <R> the return type of the visitor
 * @param <P> the type of an additional context parameter
 */
public interface DynamoAttributeVisitor<R, P> {

    /**
     * Visit an unknown attribute type.
     * @param attribute the attribute to visit
     * @param arg the additional context parameter
     * @return visitor specified result
     */
    R visit(DynamoAttribute attribute, P arg);

    /**
     * Visit a {@link BinaryDynamoAttribute}.
     * @param binary the attribute to visit
     * @param arg the additional context parameter
     * @return visitor specified result
     */
    R visitBinary(BinaryDynamoAttribute binary, P arg);

    /**
     * Visit a {@link BinarySetDynamoAttribute}.
     * @param binarySet the attribute to visit
     * @param arg the additional context parameter
     * @return visitor specified result
     */
    R visitBinarySet(BinarySetDynamoAttribute binarySet, P arg);

    /**
     * Visit a {@link BooleanDynamoAttribute}.
     * @param bool the attribute to visit
     * @param arg the additional context parameter
     * @return visitor specified result
     */
    R visitBoolean(BooleanDynamoAttribute bool, P arg);

    /**
     * Visit a {@link ListDynamoAttribute}.
     * @param list the attribute to visit
     * @param arg the additional context parameter
     * @return visitor specified result
     */
    R visitList(ListDynamoAttribute list, P arg);

    /**
     * Visit a {@link DocumentDynamoAttribute}.
     * @param document the attribute to visit
     * @param arg the additional context parameter
     * @return visitor specified result
     */
    R visitDocument(DocumentDynamoAttribute document, P arg);

    /**
     * Visit a {@link MapDynamoAttribute}.
     * @param map the attribute to visit
     * @param arg the additional context parameter
     * @return visitor specified result
     */
    R visitMap(MapDynamoAttribute map, P arg);

    /**
     * Visit a {@link NumberDynamoAttribute}.
     * @param number the attribute to visit
     * @param arg the additional context parameter
     * @return visitor specified result
     */
    R visitNumber(NumberDynamoAttribute number, P arg);

    /**
     * Visit a {@link NumberSetDynamoAttribute}.
     * @param numberSet the attribute to visit
     * @param arg the additional context parameter
     * @return visitor specified result
     */
    R visitNumberSet(NumberSetDynamoAttribute numberSet, P arg);

    /**
     * Visit a {@link NullDynamoAttribute}.
     * @param nil the attribute to visit
     * @param arg the additional context parameter
     * @return visitor specified result
     */
    R visitNull(NullDynamoAttribute nil, P arg);

    /**
     * Visit a {@link StringDynamoAttribute}.
     * @param string the attribute to visit
     * @param arg the additional context parameter
     * @return visitor specified result
     */
    R visitString(StringDynamoAttribute string, P arg);

    /**
     * Visit a {@link StringSetDynamoAttribute}.
     * @param stringSet the attribute to visit
     * @param arg the additional context parameter
     * @return visitor specified result
     */
    R visitStringSet(StringSetDynamoAttribute stringSet, P arg);

}
