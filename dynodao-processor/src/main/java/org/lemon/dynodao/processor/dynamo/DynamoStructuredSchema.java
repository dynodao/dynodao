package org.lemon.dynodao.processor.dynamo;

import java.util.Set;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

/**
 * The structured schema depicted by an annotated document class.
 */
@Data
@Builder
public class DynamoStructuredSchema {

    @Singular private final Set<DynamoIndex> indexes;

}
