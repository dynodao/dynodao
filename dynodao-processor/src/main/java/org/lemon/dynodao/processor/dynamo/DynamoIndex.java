package org.lemon.dynodao.processor.dynamo;

import lombok.Builder;
import lombok.Value;

import java.util.Optional;
import java.util.Set;

/**
 * A dynamo index present according to a document class.
 */
@Value
@Builder
public class DynamoIndex {

    private final IndexType indexType;
    private final String name;

    private final DynamoAttribute hashKeyAttribute;
    private final Optional<DynamoAttribute> rangeKeyAttribute;
    private final Set<DynamoAttribute> projectedAttributes;

}
