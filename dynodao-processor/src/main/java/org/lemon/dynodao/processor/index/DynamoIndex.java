package org.lemon.dynodao.processor.index;

import lombok.Builder;
import lombok.Value;

import javax.lang.model.element.VariableElement;
import java.util.Optional;

/**
 * A dynamo index present according to a document class.
 */
@Value
@Builder
public class DynamoIndex {

    private final IndexType indexType;
    private final String name;

    private final VariableElement hashKey;
    private final Optional<VariableElement> rangeKey;

}
