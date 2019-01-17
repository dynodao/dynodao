package org.lemon.dynodao.processor.dynamo;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

import java.util.Set;

/**
 * The structured schema depicted by an annotated document class.
 */
@Value
@Builder
public class DynamoStructuredSchema {

    @Singular private final Set<DynamoIndex> indexes;

}
