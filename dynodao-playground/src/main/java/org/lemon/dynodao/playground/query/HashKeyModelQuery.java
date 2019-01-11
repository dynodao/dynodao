package org.lemon.dynodao.playground.query;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.lemon.dynodao.DocumentQuery;
import org.lemon.dynodao.playground.Model;

public class HashKeyModelQuery implements DocumentQuery<Model> {

    private final String hashKey;

    public HashKeyModelQuery(String hashKey) {
        this.hashKey = hashKey;
    }

    public HashKeyRangeKeyModelQuery withRangeKey(String rangeKey) {
        return new HashKeyRangeKeyModelQuery(hashKey, rangeKey);
    }

    public HashKeyLsiRangeKeyModelQuery withLsiRangeKey(String lsiRangeKey) {
        return new HashKeyLsiRangeKeyModelQuery(hashKey, lsiRangeKey);
    }

    @Override
    public PaginatedList<Model> query(DynamoDBMapper dynamoDbMapper) {
        DynamoDBQueryExpression<Model> query = new DynamoDBQueryExpression<>();
        query.setKeyConditionExpression("#hashKey = :hashKey");
        query.addExpressionAttributeNamesEntry("#hashKey", "hashKey");
        query.addExpressionAttributeValuesEntry(":hashKey", new AttributeValue().withS(hashKey));
        return dynamoDbMapper.query(Model.class, query);
    }
}
