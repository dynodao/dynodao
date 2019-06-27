package com.github.dynodao.processor.itest.serialization.bool;

import com.github.dynodao.annotation.DynoDaoHashKey;
import com.github.dynodao.annotation.DynoDaoSchema;
import lombok.Data;

@Data
@DynoDaoSchema(tableName = "things")
class Schema {

    @DynoDaoHashKey
    private String hashKey;

    private Boolean booleanObject;
    private boolean primitiveBoolean;

}
