package org.lemon.dynodao.internal;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;

public abstract class QueryReadResult<T> extends AbstractReadResult<T> {

    private final AmazonDynamoDB amazonDynamoDb;
    private final QueryRequest queryRequest;
    private QueryResult queryResult;
    private boolean iterationStarted = false;

    public QueryReadResult(AmazonDynamoDB amazonDynamoDb, QueryRequest queryRequest, QueryResult queryResult) {
        this.amazonDynamoDb = amazonDynamoDb;
        this.queryRequest = queryRequest;
        this.queryResult = queryResult;
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
            return Spliterator.NONNULL | Spliterator.ORDERED;
        }

    }

    @Override
    public Spliterator<T> spliterator() {
        if (iterationStarted) {
            throw new IllegalStateException("QueryReadResult can only be iterated once.");
        }
        iterationStarted = true;
        return new QueryReadResultSpliterator();
    }

}
