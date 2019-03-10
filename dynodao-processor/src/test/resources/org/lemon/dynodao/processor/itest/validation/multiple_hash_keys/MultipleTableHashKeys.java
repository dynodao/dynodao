package org.lemon.dynodao.processor.itest.validation.multiple_hash_keys;

import org.lemon.dynodao.annotation.DynoDaoHashKey;
import org.lemon.dynodao.annotation.DynoDaoSchema;

@DynoDaoSchema(tableName = "things")
public class MultipleTableHashKeys {

    @DynoDaoHashKey
    private String hashKey1;

    @DynoDaoHashKey
    private String hashKey2;

}
