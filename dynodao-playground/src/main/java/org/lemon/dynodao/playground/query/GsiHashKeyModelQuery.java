package org.lemon.dynodao.playground.query;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.lemon.dynodao.playground.Model;

public class GsiHashKeyModelQuery implements ModelQuery {

    private final String gsiHashKey;

    public GsiHashKeyModelQuery(String gsiHashKey) {
        this.gsiHashKey = gsiHashKey;
    }

    public GsiHashKeyGsiRangeKeyModelQuery withGsiRangeKey(String gsiRangeKey) {
        return new GsiHashKeyGsiRangeKeyModelQuery(gsiHashKey, gsiRangeKey);
    }

    @Override
    public PaginatedList<Model> query(DynamoDBMapper dynamoDbMapper) {
        DynamoDBQueryExpression<Model> query = new DynamoDBQueryExpression<>();
        query.setIndexName("global-index-name");
        query.setKeyConditionExpression("#gsiHashKey = :gsiHashKey");
        query.addExpressionAttributeNamesEntry("#gsiHashKey", "gsiHashKey");
        query.addExpressionAttributeValuesEntry(":gsiHashKey", new AttributeValue().withS(gsiHashKey));
        return dynamoDbMapper.query(Model.class, query);
    }
}
