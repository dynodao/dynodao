package org.lemon.dynodao.playground.query;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.lemon.dynodao.playground.Model;

public class GsiHashKeyGsiRangeKeyModelQuery implements ModelQuery {

    private final String gsiHashKey;
    private final String gsiRangeKey;

    public GsiHashKeyGsiRangeKeyModelQuery(String gsiHashKey, String gsiRangeKey) {
        this.gsiHashKey = gsiHashKey;
        this.gsiRangeKey = gsiRangeKey;
    }

    @Override
    public PaginatedList<Model> query(DynamoDBMapper dynamoDbMapper) {
        DynamoDBQueryExpression<Model> query = new DynamoDBQueryExpression<>();
        query.setIndexName("global-index-name");
        query.setKeyConditionExpression("#gsiHashKey = :gsiHashKey AND #gsiRangeKey = :gsiRangeKey");
        query.addExpressionAttributeNamesEntry("#gsiHashKey", "gsiHashKey");
        query.addExpressionAttributeNamesEntry("#gsiRangeKey", "gsiRangeKey");
        query.addExpressionAttributeValuesEntry(":gsiHashKey", new AttributeValue().withS(gsiHashKey));
        query.addExpressionAttributeValuesEntry(":gsiRangeKey", new AttributeValue().withS(gsiRangeKey));
        return dynamoDbMapper.query(Model.class, query);
    }

}
