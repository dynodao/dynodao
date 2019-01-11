package org.lemon.dynodao;

import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

/**
 * A simple DynamoDB dao for retrieving documents via indexes.
 */
public class DocumentDao<T> {

    private final DynamoDBMapper dynamoDbMapper;

    /**
     * Sole ctor. Initializes the dynamoDbMapper field.
     * @param dynamoDbMapper the mapper this dao should use
     */
    public DocumentDao(DynamoDBMapper dynamoDbMapper) {
        this.dynamoDbMapper = dynamoDbMapper;
    }

    /**
     * Returns the single document matching the full key of table.
     * @param loader the load specification to use
     * @param <T> the type of document to get
     * @return the document with the given key
     */
    public <T> T get(DocumentLoad<T> loader) {
        return loader.load(dynamoDbMapper);
    }

    /**
     * Returns the collection of documents matching the query.
     * @param loader the query specification to use
     * @param <T> the type of documents to get
     * @return the documents matching the query
     */
    public <T> List<T> get(DocumentQuery<T> loader) {
        return loader.query(dynamoDbMapper);
    }

}
