package org.lemon.dynodao.processor.itest.table.range_key;

import lombok.Data;
import org.lemon.dynodao.annotation.DynoDaoHashKey;
import org.lemon.dynodao.annotation.DynoDaoRangeKey;
import org.lemon.dynodao.annotation.DynoDaoSchema;

@Data
@DynoDaoSchema(tableName = "things")
class Schema {

    @DynoDaoHashKey
    private String hashKey;

    @DynoDaoRangeKey
    private int rangeKey;

}
