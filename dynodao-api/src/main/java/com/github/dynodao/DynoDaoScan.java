package com.github.dynodao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;

import java.util.stream.Stream;

/**
 * A DynamoDB <tt>scan</tt> operation, getting a collection of documents of type <tt>T</tt>.
 * @param <T> the type of document stored in DynamoDB
 * @see DynoDao#get(DynoDaoScan)
 * @see DynoDao#get(DynoDaoScan, int)
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

    /**
     * Scans the total in parallel using <tt>totalSegments</tt> segments, returning the stream of documents.
     * @param amazonDynamoDb the dynamoDb client to use
     * @param totalSegments the number of segments the parallel scan should use
     * @return a parallel stream of documents matching this instance specification
     */
    Stream<T> parallelScan(AmazonDynamoDB amazonDynamoDb, int totalSegments);

    /**
     * Returns the {@link ScanRequest} which will be used the the parallel scan operation. The request returned is
     * only for the first segment of the parallel scan.
     * @param totalSegments the number of segments the parallel scan should use
     * @return the {@link ScanRequest}
     */
    ScanRequest asParallelScanRequest(int totalSegments);

}
