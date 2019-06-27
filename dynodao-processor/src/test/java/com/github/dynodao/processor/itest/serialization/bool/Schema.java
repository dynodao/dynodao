package com.github.dynodao.processor.itest.serialization.bool;

import lombok.Data;
import com.github.dynodao.annotation.DynoDaoHashKey;
import com.github.dynodao.annotation.DynoDaoSchema;

@Data
@DynoDaoSchema(tableName = "things")
class Schema {

    @DynoDaoHashKey
    private String hashKey;

    private Boolean booleanObject;
    private boolean primitiveBoolean;

}
