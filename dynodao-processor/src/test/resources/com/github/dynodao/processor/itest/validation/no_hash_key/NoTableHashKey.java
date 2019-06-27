package com.github.dynodao.processor.itest.validation.no_hash_key;

import com.github.dynodao.annotation.DynoDaoRangeKey;
import com.github.dynodao.annotation.DynoDaoSchema;

@DynoDaoSchema(tableName = "things")
public class NoTableHashKey {

    private String hashKey;

    @DynoDaoRangeKey
    private String rangeKey;

}
