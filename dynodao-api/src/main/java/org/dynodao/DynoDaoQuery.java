package org.dynodao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;

import java.util.stream.Stream;

/**
 * A DynamoDB <tt>query</tt> operation, getting a collection of documents of type <tt>T</tt>.
 * @param <T> the type of document stored in DynamoDB
 * @see DynoDao#get(DynoDaoQuery)
 * @see <a href="https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/API_Query.html">AWS Documentation</a>
 */
public interface DynoDaoQuery<T> {

    /**
     * Queries for and returns a stream of documents.
     * @param amazonDynamoDb the dynamoDb client to use
     * @return a stream of documents matching this instance specification, or an empty stream if none match
     */
    Stream<T> query(AmazonDynamoDB amazonDynamoDb);

    /**
     * Returns the {@link QueryRequest} which will be used in the query operation.
     * @return the {@link QueryRequest}
     */
    QueryRequest asQueryRequest();

}
