package org.lemon.dynodao.internal;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * The result of a {@code query} operation to DynamoDb. This class abstracts the pagination behaviour
 * of a query, automatically calling for additional items when required. Due ot the pagination,
 * the result of a query can only be iterated a single time, subsequent calls to {@link QueryReadResult#spliterator() spliterator()}
 * will fail.
 * <p>
 * Also due to pagination, the {@link Spliterator} returned cannot be made parallel.
 * @param <T> the type of item stored in DynamoDb, a {@link org.lemon.dynodao.annotation.DynoDaoSchema @DynoDaoSchema} class.
 * @see <a href="https://docs.aws.amazon.com/amazondynamodb/latest/APIReference/API_Query.html">AWS Documentation</a>
 */
public abstract class QueryReadResult<T> extends AbstractReadResult<T> {

    private final AmazonDynamoDB amazonDynamoDb;
    private final QueryRequest queryRequest;
    private QueryResult queryResult;
    private boolean iterationStarted = false;

    /**
     * Sole ctor.
     * @param amazonDynamoDb the DynamoDb client to use to make subsequent requests for paginated results
     * @param queryRequest the original request made which resulted in <tt>queryResult</tt>
     * @param queryResult the first result of the query operation
     */
    protected QueryReadResult(AmazonDynamoDB amazonDynamoDb, QueryRequest queryRequest, QueryResult queryResult) {
        this.amazonDynamoDb = amazonDynamoDb;
        this.queryRequest = queryRequest.clone();
        this.queryResult = queryResult;
    }

    @Override
    public Spliterator<T> spliterator() {
        if (iterationStarted) {
            throw new IllegalStateException("QueryReadResult can only be iterated once.");
        }
        iterationStarted = true;
        return new QueryReadResultSpliterator();
    }

    private class QueryReadResultSpliterator implements Spliterator<T> {

        private Iterator<Map<String, AttributeValue>> iterator = queryResult.getItems().iterator();

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
            return Spliterator.NONNULL | Spliterator.ORDERED | Spliterator.DISTINCT;
        }

    }

    private Iterator<Map<String, AttributeValue>> loadNextPage() {
        Map<String, AttributeValue> lastEvaluatedKey = queryResult.getLastEvaluatedKey();
        if (lastEvaluatedKey != null) {
            queryRequest.setExclusiveStartKey(lastEvaluatedKey);
            queryResult = amazonDynamoDb.query(queryRequest);
            return queryResult.getItems().iterator();
        } else {
            return null;
        }
    }

}
