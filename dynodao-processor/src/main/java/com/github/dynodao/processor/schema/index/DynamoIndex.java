package com.github.dynodao.processor.schema.index;

import com.github.dynodao.processor.schema.attribute.DynamoAttribute;
import lombok.Builder;
import lombok.Value;
import com.github.dynodao.processor.stage.KeyLengthType;

import java.util.List;
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

    /**
     * Returns the keys present in this index.
     * @return the keys in this index
     */
    public List<DynamoAttribute> getKeys() {
        return KeyLengthType.lengthOf(this).getKeyAttributes(this);
    }

}
