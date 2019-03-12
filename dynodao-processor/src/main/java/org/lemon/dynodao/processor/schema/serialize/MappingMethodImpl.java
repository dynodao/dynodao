package org.lemon.dynodao.processor.schema.serialize;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import lombok.Builder;
import lombok.Value;

import java.util.Set;

import static java.util.Collections.emptySet;

/**
 * A basic implementation of {@link MappingMethod}.
 */
@Value
@Builder
public class MappingMethodImpl implements MappingMethod {

    private final String methodName;
    private final TypeName returnType;
    private final ParameterSpec parameter;
    private final CodeBlock coreMethodBody;
    @Builder.Default private final Set<FieldSpec> delegateTypes = emptySet();

}
