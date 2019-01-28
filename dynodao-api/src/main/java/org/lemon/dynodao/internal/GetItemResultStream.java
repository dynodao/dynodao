package org.lemon.dynodao.internal;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;


@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class GetItemResultStream<T> {

    private final GetItemResult result;
    private final Function<Map<String, AttributeValue>, T> deserializer;

    public Stream<T> stream() {
        if (result.getItem() == null) {
            return Stream.empty();
        } else {
            return Stream.of(deserializer.apply(result.getItem()));
        }
    }

}
