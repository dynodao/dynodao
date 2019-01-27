package org.lemon.dynodao.processor.serialize;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import lombok.Builder;
import lombok.Value;

/**
 * A method definition which serializes a parameter into an {@link com.amazonaws.services.dynamodbv2.model.AttributeValue}.
 */
@Value
@Builder
public class SerializeMethod {

    private final String methodName;
    private final ParameterSpec parameter;
    private final CodeBlock body;

}
