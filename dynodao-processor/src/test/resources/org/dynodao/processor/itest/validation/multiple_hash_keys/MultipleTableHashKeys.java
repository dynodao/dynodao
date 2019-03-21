package org.dynodao.processor.itest.validation.multiple_hash_keys;

import org.dynodao.annotation.DynoDaoHashKey;
import org.dynodao.annotation.DynoDaoSchema;

@DynoDaoSchema(tableName = "things")
public class MultipleTableHashKeys {

    @DynoDaoHashKey
    private String hashKey1;

    @DynoDaoHashKey
    private String hashKey2;

}
