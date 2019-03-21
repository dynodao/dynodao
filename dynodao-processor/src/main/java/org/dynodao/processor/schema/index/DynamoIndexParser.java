package org.dynodao.processor.schema.index;

import org.dynodao.processor.schema.attribute.DocumentDynamoAttribute;

import java.util.Set;

/**
 * Parses a document class to pull out dynamo indexes. Implementations should also be responsible for validating
 * the schema indexes.
 */
public interface DynamoIndexParser {

    /**
     * Validates and returns indexes from the schema document.
     * @param document the schema document to parse
     * @return dynamo indexes in the schema document
     */
    Set<DynamoIndex> getIndexesFrom(DocumentDynamoAttribute document);

}
