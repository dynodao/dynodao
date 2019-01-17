package org.lemon.dynodao.processor.dynamo;

import javax.lang.model.element.TypeElement;
import java.util.Set;

/**
 * Parses a document class to pull out dynamo indexes. Implementations should also be responsible for validating
 * the schema document.
 */
interface DynamoIndexParser {

    /**
     * Validates and returns indexes from the schema document.
     * @param document the schema document to parse
     * @return dynamo indexes in the schema document
     */
    Set<DynamoIndex> getIndexesFrom(TypeElement document);
}
