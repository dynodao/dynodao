package org.dynodao.processor.schema.serialize;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import lombok.Builder;
import lombok.Value;

import java.util.Set;

import static java.util.Collections.emptySet;
import static org.dynodao.processor.util.DynamoDbUtil.attributeValue;

/**
 * A utility class for mapping an attribute to {@link com.amazonaws.services.dynamodbv2.model.AttributeValue}.
 */
@Value
@Builder
public class SerializationMappingMethod implements MappingMethod {

    private final String methodName;
    private final ParameterSpec parameter;
    private final CodeBlock coreMethodBody;
    @Builder.Default private final Set<FieldSpec> delegateTypes = emptySet();

    @Override
    public TypeName getReturnType() {
        return attributeValue();
    }

}
