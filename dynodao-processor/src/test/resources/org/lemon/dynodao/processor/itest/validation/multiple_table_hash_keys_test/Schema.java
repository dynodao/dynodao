package org.lemon.dynodao.processor.itest.validation.multiple_table_hash_keys_test;

import org.lemon.dynodao.annotation.DynoDaoHashKey;
import org.lemon.dynodao.annotation.DynoDaoSchema;

@DynoDaoSchema(tableName = "things")
public class Schema {

    @DynoDaoHashKey
    private String hashKey1;

    @DynoDaoHashKey
    private String hashKey2;

}
