package org.dynodao.internal;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import org.dynodao.annotation.DynoDaoSchema;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Spliterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Collections.emptyIterator;

/**
 * The result of a {@code parallel scan} operation to DynamoDb. This class abstracts the pagination behaviour
 * of a scan, automatically calling for additional items when required. Due ot the pagination,
 * the result of a scan can only be iterated a single time, subsequent calls to {@link ParallelScanReadResult#spliterator() spliterator()}
 * will fail.
 * <p>
 * This class is the parallel version of {@link ScanReadResult}, with a few caveats. The {@link Stream} returned by
 * {@link ParallelScanReadResult#stream() stream()} <b>must</b> remain parallel. No errors are raised if the stream is accessed
 * sequentially (either through {@link Stream#sequential()} or {@link Stream#iterator()}), but all items will not be contained
 * in the stream when accessed sequentially.
 * @param <T> the type of item stored in DynamoDb, a {@link DynoDaoSchema @DynoDaoSchema} class.
 * @see <a href="https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/API_Scan.html">AWS Documentation</a>
 * @see <a href="https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Scan.html#Scan.ParallelScan">Developer Guide</a>
 */
public abstract class ParallelScanReadResult<T> extends AbstractReadResult<T> {

    private final AmazonDynamoDB amazonDynamoDb;
    private final ScanRequest scanRequest;
    private boolean iterationStarted = false;
    private final Queue<Integer> splitSegments;

    /**
     * Sole ctor.
     * @param amazonDynamoDb the DynamoDb client to use to make subsequent requests for paginated results
     * @param scanRequest the original request made which resulted in <tt>scanResult</tt>
     */
    protected ParallelScanReadResult(AmazonDynamoDB amazonDynamoDb, ScanRequest scanRequest) {
        if (scanRequest.getTotalSegments() == null || scanRequest.getTotalSegments() < 2) {
            throw new IllegalArgumentException("ScanRequest#totalSegments must be greater than 1");
        }
        this.amazonDynamoDb = amazonDynamoDb;
        this.scanRequest = scanRequest.clone();
        this.splitSegments = IntStream.range(0, scanRequest.getTotalSegments())
                .collect(ConcurrentLinkedQueue::new, Queue::add, Queue::addAll);
    }

    @Override
    public Stream<T> stream() {
        return super.stream().parallel();
    }

    @Override
    protected Spliterator<T> spliterator() {
        if (iterationStarted) {
            throw new IllegalStateException("ParallelScanReadResult can only be iterated once.");
        }
        iterationStarted = true;
        return nextSplit(scanRequest);
    }

    private Spliterator<T> nextSplit(ScanRequest scanRequest) {
        Integer segment = splitSegments.poll();
        if (segment == null) {
            return null;
        } else {
            return new ParallelScanReadResultSpliterator(scanRequest.clone().withSegment(segment));
        }
    }

    private class ParallelScanReadResultSpliterator implements Spliterator<T> {

        private final ScanRequest scanRequest;
        private ScanResult scanResult;
        private Iterator<Map<String, AttributeValue>> iterator = emptyIterator();

        private ParallelScanReadResultSpliterator(ScanRequest scanRequest) {
            this.scanRequest = scanRequest;
        }

        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            if (iterator.hasNext() || ((iterator = loadNextPage()) != null && iterator.hasNext())) {
                action.accept(deserialize(iterator.next()));
                return true;
            } else {
                return false;
            }
        }

        private Iterator<Map<String, AttributeValue>> loadNextPage() {
            if (scanResult == null) {
                scanResult = amazonDynamoDb.scan(scanRequest);
                return asIterator(scanResult.getItems());
            } else if (scanResult.getLastEvaluatedKey() != null) {
                scanRequest.setExclusiveStartKey(scanResult.getLastEvaluatedKey());
                scanResult = amazonDynamoDb.scan(scanRequest);
                return asIterator(scanResult.getItems());
            } else {
                return emptyIterator();
            }
        }

        private Iterator<Map<String, AttributeValue>> asIterator(List<Map<String, AttributeValue>> items) {
            if (items.isEmpty()) {
                return loadNextPage();
            } else {
                return items.iterator();
            }
        }

        @Override
        public Spliterator<T> trySplit() {
            return nextSplit(scanRequest);
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

}
