package org.lemon.dynodao.processor.schema.index;

import lombok.Builder;
import lombok.Value;
import org.lemon.dynodao.processor.schema.attribute.DynamoAttribute;

import java.util.Optional;
import java.util.Set;

/**
 * A dynamo index present according to a schema.
 */
@Value
@Builder
public class DynamoIndex {

    private final IndexType indexType;
    private final String name;

    private final DynamoAttribute hashKey;
    private final Optional<DynamoAttribute> rangeKey;
    private final Set<DynamoAttribute> projectedAttributes;

}
