package org.lemon.dynodao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import java.util.List;

/**
 * A DynamoDB <tt>load</tt> operation, getting a single document of type <tt>T</tt>.
 * @param <T> the type of document stored in DynamoDB
 */
public interface DocumentLoad<T> {

    /**
     * Loads and returns the single document.
     * @param dynamoDbMapper the mapper to use
     * @return a singleton list containing document matching this instance specification, or an empty list if none match
     */
    List<T> load(DynamoDBMapper dynamoDbMapper);
}
