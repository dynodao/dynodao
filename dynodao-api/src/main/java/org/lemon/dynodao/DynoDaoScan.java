package org.lemon.dynodao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;

import java.util.stream.Stream;

/**
 * A DynamoDB <tt>scan</tt> operation, getting a collection of documents of type <tt>T</tt>.
 * @param <T> the type of document stored in DynamoDB
 * @see <a href="https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/API_Scan.html">AWS Documentation</a>
 */
public interface DynoDaoScan<T> {

    /**
     * Scans the table and returns a stream of documents.
     * @param amazonDynamoDb the dynamoDb client to use
     * @return a stream of documents matching this instance specification, or an empty stream if none match
     */
    Stream<T> scan(AmazonDynamoDB amazonDynamoDb);

    /**
     * Returns the {@link ScanRequest} which will be used in the scan operation.
     * @return the {@link ScanRequest}
     */
    ScanRequest asScanRequest();

}
