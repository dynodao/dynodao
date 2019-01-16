package org.lemon.dynodao.processor.dynamo;

import java.util.Set;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

/**
 * The structured schema depicted by an annotated document class.
 */
@Value
@Builder
public class DynamoStructuredSchema {

    @Singular private final Set<DynamoIndex> indexes;

}
