package org.lemon.dynodao.processor.serialize;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import lombok.Builder;
import lombok.Value;

import static org.lemon.dynodao.processor.util.DynamoDbUtil.attributeValue;

/**
 * A method definition which deserializes an {@link com.amazonaws.services.dynamodbv2.model.AttributeValue}
 * into another type.
 */
@Value
@Builder
public class UnmarshallMethod {

    private static final ParameterSpec ATTRIBUTE_VALUE_PARAMETER = ParameterSpec.builder(attributeValue(), "attributeValue").build();

    private final String methodName;
    private final CodeBlock body;

    /**
     * @return the parameter to the unmarshalling method
     */
    public ParameterSpec getParameter() {
        return ATTRIBUTE_VALUE_PARAMETER;
    }

    /**
     * @return the parameter to the unmarshalling method
     */
    public static ParameterSpec parameter() {
        return ATTRIBUTE_VALUE_PARAMETER;
    }
}
