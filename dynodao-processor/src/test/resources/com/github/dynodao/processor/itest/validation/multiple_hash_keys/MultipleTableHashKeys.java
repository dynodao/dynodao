package com.github.dynodao.processor.itest.validation.multiple_hash_keys;

import com.github.dynodao.annotation.DynoDaoHashKey;
import com.github.dynodao.annotation.DynoDaoSchema;

@DynoDaoSchema(tableName = "things")
public class MultipleTableHashKeys {

    @DynoDaoHashKey
    private String hashKey1;

    @DynoDaoHashKey
    private String hashKey2;

}
