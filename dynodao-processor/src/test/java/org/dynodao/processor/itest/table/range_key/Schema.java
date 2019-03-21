package org.dynodao.processor.itest.table.range_key;

import lombok.Data;
import org.dynodao.annotation.DynoDaoHashKey;
import org.dynodao.annotation.DynoDaoRangeKey;
import org.dynodao.annotation.DynoDaoSchema;

@Data
@DynoDaoSchema(tableName = "things")
class Schema {

    @DynoDaoHashKey
    private String hashKey;

    @DynoDaoRangeKey
    private int rangeKey;

}
