package org.lemon.dynodao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;

import java.util.stream.Stream;

/**
 * A simplified DynamoDB data access object.
 */
public class DynoDao {

    private final AmazonDynamoDB dynamoDb;

    /**
     * Sole ctor. Initializes the dynamoDb field.
     * @param dynamoDb the client this dao should use
     */
    public DynoDao(AmazonDynamoDB dynamoDb) {
        this.dynamoDb = dynamoDb;
    }

    /**
     * Returns a stream containing the the single document matching the full key of table, or an empty
     * stream if no such document exists.
     * @param load the load specification to use
     * @param <T> the type of document to get
     * @return a stream containing the document with the given key, or an empty stream if no such document exists
     */
    public <T> Stream<T> get(DynoDaoLoad<T> load) {
        return load.load(dynamoDb);
    }

    /**
     * Returns a stream containing all of the documents matching the query.
     * @param query the query specification to use
     * @param <T> the type of documents to get
     * @return a stream of all of the documents matching the query
     */
    public <T> Stream<T> get(DynoDaoQuery<T> query) {
        return query.query(dynamoDb);
    }

}
