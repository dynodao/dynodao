package org.lemon.dynodao.processor.dynamo;

import javax.inject.Inject;
import javax.lang.model.element.TypeElement;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Parses a dynamo document class and extracts the schema.
 */
public class DynamoSchemaParser {

    private final DynamoIndexParsers dynamoIndexParsers;

    @Inject DynamoSchemaParser(DynamoIndexParsers dynamoIndexParsers) {
        this.dynamoIndexParsers = dynamoIndexParsers;
    }

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
        dynamoIndexParsers.forEach(parser -> indexes.addAll(parser.getIndexesFrom(document)));
        return indexes;
    }

}
