package org.lemon.dynodao.processor.itest.validation.multiple_table_hash_keys_test;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import org.lemon.dynodao.DynoDao;

@DynoDao
public class Schema {

    @DynamoDBHashKey
    private String hashKey1;

    @DynamoDBHashKey
    private String hashKey2;

}
