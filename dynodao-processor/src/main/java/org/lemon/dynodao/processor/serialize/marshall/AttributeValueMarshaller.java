package org.lemon.dynodao.processor.serialize.marshall;

import org.lemon.dynodao.processor.serialize.MarshallMethod;
import org.lemon.dynodao.processor.serialize.SerializationContext;

import javax.lang.model.type.TypeMirror;
import java.util.Collection;

/**
 * Builds a method to convert a type to an {@link com.amazonaws.services.dynamodbv2.model.AttributeValue}.
 */
public interface AttributeValueMarshaller {

    /**
     * Returns <tt>true</tt> if this serializer instance applies to the type provided.
     * @param type the type
     * @return <tt>true</tt> if this applies to the type, <tt>false</tt> otherwise
     */
    boolean isApplicableTo(TypeMirror type);

    /**
     * Returns the types that are dependencies of <tt>type</tt> needed in order to serialize <tt>type</tt> itself.
     * For example, this would include type arguments, or nested fields.
     * @param type the type to get serialization dependent types of
     * @return dependencies of <tt>type</tt> needed to serialize <tt>type</tt> itself
     */
    Collection<? extends TypeMirror> getTypeDependencies(TypeMirror type);

    /**
     * Generates the method which converts a field of the specified type to an {@link com.amazonaws.services.dynamodbv2.model.AttributeValue}.
     * This is guaranteed to only be called if {@code isApplicableTo(type)} returns <tt>true</tt>, and all those types
     * returned by {@code getTypeDependencies(type)} have already been evaluated and added to <tt>serializationContext</tt>.
     * @param type the type to serialize
     * @param serializationContext the context in which the serialization occurs
     * @return the method which serializes the type
     */
    MarshallMethod serialize(TypeMirror type, SerializationContext serializationContext);
}
