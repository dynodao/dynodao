package org.dynodao.processor.itest.table.hash_key;

import lombok.Data;
import org.dynodao.annotation.DynoDaoHashKey;
import org.dynodao.annotation.DynoDaoSchema;

@Data
@DynoDaoSchema(tableName = "things")
class Schema {

    @DynoDaoHashKey
    private String hashKey;

}
