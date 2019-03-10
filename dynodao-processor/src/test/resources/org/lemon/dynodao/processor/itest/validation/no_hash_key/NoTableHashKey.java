package org.lemon.dynodao.processor.itest.validation.no_hash_key;

import org.lemon.dynodao.annotation.DynoDaoRangeKey;
import org.lemon.dynodao.annotation.DynoDaoSchema;

@DynoDaoSchema(tableName = "things")
public class NoTableHashKey {

    private String hashKey;

    @DynoDaoRangeKey
    private String rangeKey;

}
