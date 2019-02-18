package org.lemon.dynodao.processor.schema.serialize;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import lombok.Builder;
import lombok.Value;

import java.util.Set;

import static java.util.Collections.emptySet;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.attributeValue;

/**
 * A utility class for mapping an attribute back from {@link com.amazonaws.services.dynamodbv2.model.AttributeValue}.
 */
@Value
@Builder
public class DeserializationMappingMethod implements MappingMethod {

    private static final ParameterSpec ATTRIBUTE_VALUE_PARAMETER = ParameterSpec.builder(attributeValue(), "attributeValue").build();

    private final String methodName;
    private final TypeName returnType;
    private final CodeBlock coreMethodBody;
    @Builder.Default private final Set<FieldSpec> delegateTypes = emptySet();

    @Override
    public ParameterSpec getParameter() {
        return ATTRIBUTE_VALUE_PARAMETER;
    }

    /**
     * Returns the {@link com.amazonaws.services.dynamodbv2.model.AttributeValue} parameter to any deserialization method.
     * @return the {@link com.amazonaws.services.dynamodbv2.model.AttributeValue} parameter
     */
    public static ParameterSpec parameter() {
        return ATTRIBUTE_VALUE_PARAMETER;
    }

}
