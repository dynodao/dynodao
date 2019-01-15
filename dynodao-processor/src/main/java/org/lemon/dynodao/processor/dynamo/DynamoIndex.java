package org.lemon.dynodao.processor.dynamo;

import java.util.Optional;

import javax.lang.model.element.VariableElement;

import lombok.Builder;
import lombok.Value;

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
