package org.lemon.dynodao.playground;

import org.lemon.dynodao.DynoDao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@DynoDao
public class Model {

    @DynamoDBHashKey
    private String hashKey;
    @DynamoDBRangeKey
    private String rangeKey;

    @DynamoDBIndexRangeKey(localSecondaryIndexName = "local-index-name")
    private String lsiRangeKey;

    @DynamoDBIndexHashKey(globalSecondaryIndexNames = { "global-index-name", "global-index-2" })
    private String gsiHashKey;

    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "global-index-name")
    private String gsiRangeKey;

    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "global-index-2")
    private String gsiRangeKey2;

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "global-solo-index")
    private String soloGsiHashKey;

    private String attribute;

}
