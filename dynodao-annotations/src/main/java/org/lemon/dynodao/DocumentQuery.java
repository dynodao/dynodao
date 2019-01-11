package org.lemon.dynodao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedList;

/**
 * A DynamoDB <tt>query</tt> operation, getting a collection of documents of type <tt>T</tt>.
 * @param <T> the type of document stored in DynamoDB
 */
public interface DocumentQuery<T> {

    /**
     * Queries and returns the collection of documents.
     * @param dynamoDbMapper the mapper to use
     * @return the documents matching this instance specification
     */
    PaginatedList<T> query(DynamoDBMapper dynamoDbMapper);
}
