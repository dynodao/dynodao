package org.dynodao.processor.itest.validation.no_hash_key;

import org.dynodao.annotation.DynoDaoRangeKey;
import org.dynodao.annotation.DynoDaoSchema;

@DynoDaoSchema(tableName = "things")
public class NoTableHashKey {

    private String hashKey;

    @DynoDaoRangeKey
    private String rangeKey;

}
