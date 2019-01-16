package org.lemon.dynodao.processor.dynamo;

import javax.inject.Inject;
import javax.lang.model.element.TypeElement;

/**
 * Parses a dynamo document class and extracts the schema.
 */
public class DynamoSchemaParser {

    @Inject TableIndexParser tableIndexParser;
    @Inject LocalSecondaryIndexParser localSecondaryIndexParser;
    @Inject GlobalSecondaryIndexParser globalSecondaryIndexParser;

    @Inject DynamoSchemaParser() { }

    /**
     * Returns the dynamo schema the document object represents.
     * @param document the document representing the schema
     */
    public DynamoStructuredSchema getSchema(TypeElement document) {
        return DynamoStructuredSchema.builder()
                .indexes(tableIndexParser.getIndexesFrom(document))
                .indexes(localSecondaryIndexParser.getIndexesFrom(document))
                .indexes(globalSecondaryIndexParser.getIndexesFrom(document))
                .build();
    }

}
