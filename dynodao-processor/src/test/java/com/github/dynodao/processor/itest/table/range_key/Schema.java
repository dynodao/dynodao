package com.github.dynodao.processor.itest.table.range_key;

import com.github.dynodao.annotation.DynoDaoHashKey;
import com.github.dynodao.annotation.DynoDaoRangeKey;
import com.github.dynodao.annotation.DynoDaoSchema;
import lombok.Data;

@Data
@DynoDaoSchema(tableName = "things")
class Schema {

    @DynoDaoHashKey
    private String hashKey;

    @DynoDaoRangeKey
    private int rangeKey;

}
