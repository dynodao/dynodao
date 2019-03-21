package org.dynodao.internal;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import org.dynodao.annotation.DynoDaoSchema;

import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * The result of a {@code scan} operation to DynamoDb. This class abstracts the pagination behaviour
 * of a scan, automatically calling for additional items when required. Due ot the pagination,
 * the result of a scan can only be iterated a single time, subsequent calls to {@link ScanReadResult#spliterator() spliterator()}
 * will fail.
 * @param <T> the type of item stored in DynamoDb, a {@link DynoDaoSchema @DynoDaoSchema} class.
 * @see <a href="https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/API_Scan.html">AWS Documentation</a>
 */
public abstract class ScanReadResult<T> extends AbstractReadResult<T> {

    private final AmazonDynamoDB amazonDynamoDb;
    private final ScanRequest scanRequest;
    private ScanResult scanResult;
    private boolean iterationStarted = false;

    /**
     * Sole ctor.
     * @param amazonDynamoDb the DynamoDb client to use to make subsequent requests for paginated results
     * @param scanRequest the original request made which resulted in <tt>scanResult</tt>
     * @param scanResult the first result of the query operation
     */
    protected ScanReadResult(AmazonDynamoDB amazonDynamoDb, ScanRequest scanRequest, ScanResult scanResult) {
        this.amazonDynamoDb = amazonDynamoDb;
        this.scanRequest = scanRequest.clone();
        this.scanResult = scanResult;
    }

    @Override
    protected Spliterator<T> spliterator() {
        if (iterationStarted) {
            throw new IllegalStateException("ScanReadResult can only be iterated once.");
        }
        iterationStarted = true;
        return new ScanReadResultSpliterator();
    }

    private class ScanReadResultSpliterator implements Spliterator<T> {

        private Iterator<Map<String, AttributeValue>> iterator = scanResult.getItems().iterator();

        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            if (iterator.hasNext() || ((iterator = loadNextPage()) != null && iterator.hasNext())) {
                action.accept(deserialize(iterator.next()));
                return true;
            } else {
                return false;
            }
        }

        @Override
        public Spliterator<T> trySplit() {
            return null;
        }

        @Override
        public long estimateSize() {
            return Long.MAX_VALUE;
        }

        @Override
        public int characteristics() {
            return Spliterator.NONNULL;
        }

    }

    private Iterator<Map<String, AttributeValue>> loadNextPage() {
        Map<String, AttributeValue> lastEvaluatedKey = scanResult.getLastEvaluatedKey();
        if (lastEvaluatedKey != null) {
            scanRequest.setExclusiveStartKey(lastEvaluatedKey);
            scanResult = amazonDynamoDb.scan(scanRequest);
            return scanResult.getItems().iterator();
        } else {
            return null;
        }
    }

}
