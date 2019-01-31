package org.lemon.dynodao.processor.serialize;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import lombok.Builder;
import lombok.Value;

import static org.lemon.dynodao.processor.util.DynamoDbUtil.attributeValue;

/**
 * A method definition which serializes a parameter into an {@link com.amazonaws.services.dynamodbv2.model.AttributeValue}.
 */
@Value
@Builder
public class MarshallMethod {

    private final String methodName;
    private final ParameterSpec parameter;
    private final CodeBlock body;

    /**
     * @return the return type of the method, always AttributeValue
     */
    public TypeName getReturnType() {
        return attributeValue();
    }

}
