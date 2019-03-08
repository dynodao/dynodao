package org.lemon.dynodao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;

import java.util.stream.Stream;

/**
 * A DynamoDB <tt>load</tt> operation, getting a single document of type <tt>T</tt>.
 * @param <T> the type of document stored in DynamoDB
 * @see <a href="https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/API_GetItem.html">AWS Documentation</a>
 */
public interface DynoDaoLoad<T> {

    /**
     * Loads and returns the single document.
     * @param amazonDynamoDb the dynamoDb client to use
     * @return a singleton stream document matching this instance specification, or an empty stream if none match
     */
    Stream<T> load(AmazonDynamoDB amazonDynamoDb);

    /**
     * Returns the {@link GetItemRequest} which will be used in the load operation.
     * @return the {@link GetItemRequest}
     */
    GetItemRequest asGetItemRequest();

}
