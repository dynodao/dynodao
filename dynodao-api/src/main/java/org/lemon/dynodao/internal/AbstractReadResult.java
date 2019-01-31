package org.lemon.dynodao.internal;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.Map;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class AbstractReadResult<T> {

    protected abstract T deserialize(AttributeValue attributeValue);

    protected final T deserialize(Map<String, AttributeValue> attributeValueMap) {
        return deserialize(new AttributeValue().withM(attributeValueMap));
    }

    public abstract Spliterator<T> spliterator();

    public final Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
}
