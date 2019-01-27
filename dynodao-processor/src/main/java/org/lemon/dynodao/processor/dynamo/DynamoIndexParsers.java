package org.lemon.dynodao.processor.dynamo;

import org.lemon.dynodao.processor.util.StreamUtil.Streamable;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Stores all of the implementations of {@link DynamoIndexParser} in the appropriate order.
 */
class DynamoIndexParsers implements Streamable<DynamoIndexParser> {

    @Inject TableIndexParser tableIndexParser;
    @Inject LocalSecondaryIndexParser localSecondaryIndexParser;
    @Inject GlobalSecondaryIndexParser globalSecondaryIndexParser;

    private final List<DynamoIndexParser> dynamoIndexParsers = new ArrayList<>();

    @Inject DynamoIndexParsers() { }

    /**
     * Populates the dynamoIndexParsers field. The list is ordered, the first elements are added to the
     * generated class first.
     */
    @Inject void initDynamoIndexParsers() {
        dynamoIndexParsers.add(tableIndexParser);
        dynamoIndexParsers.add(localSecondaryIndexParser);
        dynamoIndexParsers.add(globalSecondaryIndexParser);
    }

    @Override
    public Iterator<DynamoIndexParser> iterator() {
        return dynamoIndexParsers.iterator();
    }
}
