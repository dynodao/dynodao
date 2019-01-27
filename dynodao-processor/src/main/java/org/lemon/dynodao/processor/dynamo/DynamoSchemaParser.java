package org.lemon.dynodao.processor.dynamo;

import javax.inject.Inject;
import javax.lang.model.element.TypeElement;
import java.util.LinkedHashSet;
import java.util.Set;

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
        Set<DynamoIndex> indexes = getIndexes(document);
        return DynamoStructuredSchema.builder()
                .indexes(indexes)
                .build();
    }

    private Set<DynamoIndex> getIndexes(TypeElement document) {
        Set<DynamoIndex> indexes = new LinkedHashSet<>();
        indexes.addAll(tableIndexParser.getIndexesFrom(document));
        indexes.addAll(localSecondaryIndexParser.getIndexesFrom(document));
        indexes.addAll(globalSecondaryIndexParser.getIndexesFrom(document));
        return indexes;
    }

}
