package org.lemon.dynodao.processor.dynamo;

import javax.inject.Inject;
import javax.lang.model.element.TypeElement;

/**
 * Parses a dynamo document class and extracts the schema.
 */
public class DynamoSchemaParser {

    @Inject DynamoSchemaParser() { }

    /**
     * Returns the dynamo schema the document object represents.
     * @param document the document representing the schema
     */
    public DynamoStructuredSchema getSchema(TypeElement document) {
        DocumentFieldParseBuilder fields = getKeyFields(document);

        return DynamoStructuredSchema.builder()
                .index(fields.getTable())
                .indexes(fields.getLocalSecondaryIndexes())
                .indexes(fields.getGlobalSecondaryIndexes())
                .build();
    }

    private DocumentFieldParseBuilder getKeyFields(TypeElement document) {
        DocumentFieldParseBuilder fields = new DocumentFieldParseBuilder();
        document.getEnclosedElements().forEach(fields::append);
        return fields;
    }

}
