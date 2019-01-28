package org.lemon.dynodao.processor.serialize;

import static org.lemon.dynodao.processor.util.DynamoDbUtil.attributeValue;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import lombok.Builder;
import lombok.Value;

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

}
