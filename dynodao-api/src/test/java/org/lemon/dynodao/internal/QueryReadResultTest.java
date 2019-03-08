package org.lemon.dynodao.internal;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.lemon.dynodao.test.AbstractUnitTest;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyZeroInteractions;

class QueryReadResultTest extends AbstractUnitTest {

    @Data
    private static class Pojo {
        String hash;
        int range;
    }

    @Mock private AmazonDynamoDB amazonDynamoDbMock;

    @Test
    void stream_initialQueryResultIsEmpty_returnsEmptyStream() {
        QueryReadResult<Pojo> classUnderTest = build(initialRequest(), result(null));
        Stream<Pojo> query = classUnderTest.stream();
        assertThat(query).isEmpty();
        verifyZeroInteractions(amazonDynamoDbMock);
    }

    private QueryReadResult<Pojo> build(QueryRequest queryRequest, QueryResult queryResult) {
        return new QueryReadResult<Pojo>(amazonDynamoDbMock, queryRequest, queryResult) {
            @Override
            protected Pojo deserialize(AttributeValue attributeValue) {
                Pojo pojo = new Pojo();
                pojo.setHash(attributeValue.getM().get("hash").getS());
                pojo.setRange(Integer.parseInt(attributeValue.getM().get("range").getN()));
                return pojo;
            }
        };
    }

    private QueryRequest initialRequest() {
        return request(null);
    }

    private QueryRequest request(Map<String, AttributeValue> exclusiveStartKey) {
        return new QueryRequest()
                .withExclusiveStartKey(exclusiveStartKey)
                .withTableName("table-name");
    }

    private QueryResult result(Map<String, AttributeValue> lastEvaluatedKey, Pojo... pojos) {
        return new QueryResult()
                .withLastEvaluatedKey(lastEvaluatedKey)
                .withItems(Arrays.stream(pojos)
                        .map(this::serialize)
                        .collect(toList()));
    }

    private Map<String, AttributeValue> serialize(Pojo pojo) {
        Map<String, AttributeValue> map = new LinkedHashMap<>();
        map.put("hash", new AttributeValue(pojo.getHash()));
        map.put("range", new AttributeValue().withN(String.valueOf(pojo.getRange())));
        return map;
    }

}
