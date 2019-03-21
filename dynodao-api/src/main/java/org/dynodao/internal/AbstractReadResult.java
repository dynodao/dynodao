package org.dynodao.internal;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.dynodao.annotation.DynoDaoSchema;

import java.util.Map;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Base class for abstractions of read operation results from DynamoDb.
 * @param <T> the type of item stored in DynamoDb, a {@link DynoDaoSchema @DynoDaoSchema} class.
 */
abstract class AbstractReadResult<T> {
    /**
     * Deserializes the item into the schema class <tt>T</tt>.
     * @param item the item from dynamo to convert to a class of type <tt>T</tt>
     * @return the schema class
     */
    protected abstract T deserialize(Map<String, AttributeValue> item);

    /**
     * Returns a spliterator over the read operation result. If the operation requires pagination,
     * the spliterator will handle the pagination internally.
     * @return a spliterator over the read result
     * @throws IllegalStateException if called more than once
     */
    protected abstract Spliterator<T> spliterator();

    /**
     * Returns a stream over all elements returned by the read operation.
     * @return a stream over the read operation results
     * @throws IllegalStateException if called more than once
     */
    public Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

}
