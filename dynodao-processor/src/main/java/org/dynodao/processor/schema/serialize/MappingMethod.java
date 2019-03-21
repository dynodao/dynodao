package org.dynodao.processor.schema.serialize;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;

import java.util.Set;

/**
 * Serializes or deserializes a type from {@link com.amazonaws.services.dynamodbv2.model.AttributeValue}.
 */
public interface MappingMethod {

    /**
     * Returns the name of the method.
     * @return the name of the method
     */
    String getMethodName();

    /**
     * Returns the return type of this method.
     * @return the return type of this method
     */
    TypeName getReturnType();

    /**
     * Returns the single parameter to the method which is to be converted to the return type.
     * @return the parameter to the method
     */
    ParameterSpec getParameter();

    /**
     * Returns the method body, any null checks or other filters applied elsewhere. This code block should assume all fields
     * the method requires are non-null and available.
     * <p>
     * For example, for converting {@link String} to {@link com.amazonaws.services.dynamodbv2.model.AttributeValue}, this
     * would only include {@code return new AttributeValue().withS(string)}.
     * @return the method body
     */
    CodeBlock getCoreMethodBody();

    /**
     * Returns any additional fields the serialization class requires, such as custom serializers.
     * @return additional fields the serialization class requires
     */
    Set<FieldSpec> getDelegateTypes();

}
