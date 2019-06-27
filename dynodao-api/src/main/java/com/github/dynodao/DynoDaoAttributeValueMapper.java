package com.github.dynodao;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.github.dynodao.annotation.DynoDaoValueMapped;

/**
 * Converts between a type <tt>T</tt> and {@link AttributeValue}. This is used in conjunction with
 * {@link DynoDaoValueMapped} to serialize and deserialize
 * types which are not supported by DynoDao, or require different serialization than what is built-in.
 * <p>
 * Implementations require a <tt>public</tt> default constructor. Instances of this class are initialized
 * in a static block in the generated serializer.
 * @param <T> the type to convert to and from {@link AttributeValue}
 */
public interface DynoDaoAttributeValueMapper<T> {

    /**
     * Converts <tt>value</tt> into an {@link AttributeValue}. Will never be called with a <tt>value</tt> of <tt>null</tt>.
     * @param value the value to convert to an {@link AttributeValue}, never <tt>null</tt>
     * @return the {@link AttributeValue} representation of <tt>value</tt>
     */
    AttributeValue toAttributeValue(T value);

    /**
     * Converts <tt>attributeValue</tt> into the type <tt>T</tt>. Will never be called with an <tt>attributeValue</tt>
     * of <tt>null</tt>.
     * @param attributeValue the value to convert to <tt>T</tt>, never <tt>null</tt>
     * @return the <tt>T</tt>
     */
    T fromAttributeValue(AttributeValue attributeValue);
}
