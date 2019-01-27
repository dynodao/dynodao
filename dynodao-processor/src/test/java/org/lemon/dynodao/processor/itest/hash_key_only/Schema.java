package org.lemon.dynodao.processor.itest.hash_key_only;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import lombok.Data;
import org.lemon.dynodao.DynoDao;

@Data
@DynoDao
class Schema {

    @DynamoDBHashKey
    private String hashKey;

}
