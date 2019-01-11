package org.lemon.dynodao.playground.query;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.lemon.dynodao.DocumentQuery;
import org.lemon.dynodao.playground.Model;

public class HashKeyLsiRangeKeyModelQuery implements DocumentQuery<Model> {

    private final String hashKey;
    private final String lsiRangeKey;

    public HashKeyLsiRangeKeyModelQuery(String hashKey, String lsiRangeKey) {
        this.hashKey = hashKey;
        this.lsiRangeKey = lsiRangeKey;
    }

    @Override
    public PaginatedList<Model> query(DynamoDBMapper dynamoDbMapper) {
        DynamoDBQueryExpression<Model> query = new DynamoDBQueryExpression<>();
        query.setIndexName("local-index-name");
        query.setKeyConditionExpression("#hashKey = :hashKey AND #lsiRangeKey = :lsiRangeKey");
        query.addExpressionAttributeNamesEntry("#hashKey", "hashKey");
        query.addExpressionAttributeNamesEntry("#lsiRangeKey", "lsiRangeKey");
        query.addExpressionAttributeValuesEntry(":hashKey", new AttributeValue().withS(hashKey));
        query.addExpressionAttributeValuesEntry(":lsiRangeKey", new AttributeValue().withS(lsiRangeKey));
        return dynamoDbMapper.query(Model.class, query);
    }

}
