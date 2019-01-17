package org.lemon.dynodao.processor.itest.table.hash_key_only_test;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import org.lemon.dynodao.DynoDao;

@DynoDao
public class Schema {
    @DynamoDBHashKey
    private String hashKey;
}
