package com.github.dynodao.processor.itest.table.range_key;

import lombok.Data;
import com.github.dynodao.annotation.DynoDaoHashKey;
import com.github.dynodao.annotation.DynoDaoRangeKey;
import com.github.dynodao.annotation.DynoDaoSchema;

@Data
@DynoDaoSchema(tableName = "things")
class Schema {

    @DynoDaoHashKey
    private String hashKey;

    @DynoDaoRangeKey
    private int rangeKey;

}
