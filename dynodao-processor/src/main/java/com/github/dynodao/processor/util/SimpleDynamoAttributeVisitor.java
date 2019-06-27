package com.github.dynodao.processor.util;

import com.github.dynodao.processor.schema.attribute.BinaryDynamoAttribute;
import com.github.dynodao.processor.schema.attribute.BinarySetDynamoAttribute;
import com.github.dynodao.processor.schema.attribute.BooleanDynamoAttribute;
import com.github.dynodao.processor.schema.attribute.DocumentDynamoAttribute;
import com.github.dynodao.processor.schema.attribute.DynamoAttribute;
import com.github.dynodao.processor.schema.attribute.DynamoAttributeVisitor;
import com.github.dynodao.processor.schema.attribute.ListDynamoAttribute;
import com.github.dynodao.processor.schema.attribute.MapDynamoAttribute;
import com.github.dynodao.processor.schema.attribute.NullDynamoAttribute;
import com.github.dynodao.processor.schema.attribute.NumberDynamoAttribute;
import com.github.dynodao.processor.schema.attribute.NumberSetDynamoAttribute;
import com.github.dynodao.processor.schema.attribute.StringDynamoAttribute;
import com.github.dynodao.processor.schema.attribute.StringSetDynamoAttribute;
import lombok.RequiredArgsConstructor;

/**
 * A visitor of attribute types. Each method is implemented to provide a default value.
 * @param <R> the visitor return type
 * @param <P> the type of additional arguments to visit methods
 */
@RequiredArgsConstructor
public class SimpleDynamoAttributeVisitor<R, P> implements DynamoAttributeVisitor<R, P> {

    private final R defaultValue;

    /**
     * Sets the default action return value to <tt>null</tt>.
     */
    public SimpleDynamoAttributeVisitor() {
        this(null);
    }

    /**
     * The default action for visit methods.
     * @param attribute the attribute to visit
     * @param arg visitor specific parameter
     * @return <tt>defaultValue</tt>, unless overridden
     */
    protected R defaultAction(DynamoAttribute attribute, P arg) {
        return defaultValue;
    }

    @Override
    public R visit(DynamoAttribute attribute, P arg) {
        return defaultAction(attribute, arg);
    }

    @Override
    public R visitBinary(BinaryDynamoAttribute binary, P arg) {
        return defaultAction(binary, arg);
    }

    @Override
    public R visitBinarySet(BinarySetDynamoAttribute binarySet, P arg) {
        return defaultAction(binarySet, arg);
    }

    @Override
    public R visitBoolean(BooleanDynamoAttribute bool, P arg) {
        return defaultAction(bool, arg);
    }

    @Override
    public R visitList(ListDynamoAttribute list, P arg) {
        return defaultAction(list, arg);
    }

    @Override
    public R visitDocument(DocumentDynamoAttribute document, P arg) {
        return defaultAction(document, arg);
    }

    @Override
    public R visitMap(MapDynamoAttribute map, P arg) {
        return defaultAction(map, arg);
    }

    @Override
    public R visitNumber(NumberDynamoAttribute number, P arg) {
        return defaultAction(number, arg);
    }

    @Override
    public R visitNumberSet(NumberSetDynamoAttribute numberSet, P arg) {
        return defaultAction(numberSet, arg);
    }

    @Override
    public R visitNull(NullDynamoAttribute nil, P arg) {
        return defaultAction(nil, arg);
    }

    @Override
    public R visitString(StringDynamoAttribute string, P arg) {
        return defaultAction(string, arg);
    }

    @Override
    public R visitStringSet(StringSetDynamoAttribute stringSet, P arg) {
        return defaultAction(stringSet, arg);
    }

}
