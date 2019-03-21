package org.dynodao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;

/**
 * A DynamoDB <tt>createTable</tt> operation for a table which stores a collection of documents of type <tt>T</tt>.
 * @param <T> the type of document stored in DynamoDB
 * @see <a href="https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/API_CreateTable.html">AWS Documentation</a>
 */
public interface DynoDaoCreateTable<T> {

    /**
     * Creates the table, returning the table <tt>arn</tt>. The table and all indexes have a default provisioned
     * throughput of 5 reads and 5 writes.
     * @param amazonDynamoDb the dynamoDb client to use
     * @return the <tt>arn</tt> of the created table
     */
    String createTable(AmazonDynamoDB amazonDynamoDb);

    /**
     * Returns the {@link CreateTableRequest} which will be used in the create table operation.
     * The table and all indexes have a default provisioned throughput of 5 reads and 5 writes.
     * @return the {@link CreateTableRequest}
     */
    CreateTableRequest asCreateTableRequest();

}
