package org.lemon.dynodao.processor.serialize.unmarshall;

import org.lemon.dynodao.processor.serialize.SerializationContext;
import org.lemon.dynodao.processor.serialize.UnmarshallMethod;

import javax.lang.model.type.TypeMirror;
import java.util.Collection;

/**
 * Builds a method to an {@link com.amazonaws.services.dynamodbv2.model.AttributeValue} into a type.
 */
public interface AttributeValueUnmarshaller {

    /**
     * Returns <tt>true</tt> if this deserializer instance applies to the type provided.
     * @param type the type
     * @return <tt>true</tt> if this applies to the type, <tt>false</tt> otherwise
     */
    boolean isApplicableTo(TypeMirror type);

    /**
     * Returns the types that are dependencies of <tt>type</tt> needed in order to deserialize <tt>type</tt> itself.
     * For example, this would include type arguments, or nested fields.
     * @param type the type to get deserialization dependent types of
     * @return dependencies of <tt>type</tt> needed to deserialize <tt>type</tt> itself
     */
    Collection<? extends TypeMirror> getTypeDependencies(TypeMirror type);

    /**
     * Generates the method which converts an {@link com.amazonaws.services.dynamodbv2.model.AttributeValue} into a <tt>type</tt>.
     * This is guaranteed to only be called if {@code isApplicableTo(type)} returns <tt>true</tt>, and all those types
     * returned by {@code getTypeDependencies(type)} have already been evaluated and added to <tt>serializationContext</tt>.
     * @param type the type to serialize
     * @param serializationContext the context in which the deserialization occurs
     * @return the method which deserializes the type
     */
    UnmarshallMethod serialize(TypeMirror type, SerializationContext serializationContext);
}
