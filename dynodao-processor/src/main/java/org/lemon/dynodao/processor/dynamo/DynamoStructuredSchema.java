package org.lemon.dynodao.processor.dynamo;

import lombok.Builder;
import lombok.Value;

import java.util.Set;

import static java.util.Collections.emptySet;

/**
 * The structured schema depicted by an annotated document class.
 */
@Value
@Builder
public class DynamoStructuredSchema {

    private final Set<DynamoIndex> indexes;

    /**
     * @return all of the attributes described in the schema
     */
    public Set<DynamoAttribute> getTableAttributes() {
        return indexes.stream()
                .filter(index -> index.getIndexType().equals(IndexType.TABLE))
                .findAny().map(DynamoIndex::getProjectedAttributes).orElse(emptySet());
    }

}
