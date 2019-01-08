package org.lemon.dynodao.playground.query;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.lemon.dynodao.playground.Model;

public class HashKeyRangeKeyModelQuery implements ModelLoad {

    private final String hashKey;
    private final String rangeKey;

    public HashKeyRangeKeyModelQuery(String hashKey, String rangeKey) {
        this.hashKey = hashKey;
        this.rangeKey = rangeKey;
    }

    @Override
    public Model load(DynamoDBMapper dynamoDbMapper) {
        return dynamoDbMapper.load(Model.class, hashKey, rangeKey);
    }
}
