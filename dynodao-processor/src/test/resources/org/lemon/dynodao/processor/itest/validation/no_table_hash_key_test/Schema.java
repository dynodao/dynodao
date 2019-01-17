package org.lemon.dynodao.processor.itest.validation.no_table_hash_key_test;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import org.lemon.dynodao.DynoDao;

@DynoDao
public class Schema {

    private String hashKey;

    @DynamoDBRangeKey
    private String rangeKey;
}
