package org.lemon.dynodao.playground;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import lombok.Builder;
import lombok.Data;
import org.lemon.dynodao.DynoDao;

@Data
@Builder
@DynoDao
public class Model {

    @DynamoDBHashKey
    private String hashKey;
    @DynamoDBRangeKey
    private String rangeKey;

    @DynamoDBIndexRangeKey(localSecondaryIndexNames = "local-index-name")
    private String lsiRangeKey;

    @DynamoDBIndexHashKey(globalSecondaryIndexName = "global-index-name")
    private String gsiHashKey;
    @DynamoDBIndexRangeKey(globalSecondaryIndexName = "global-index-name")
    private String gsiRangeKey;

    private String attribute;

}
