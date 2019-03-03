package org.lemon.dynodao.processor.itest.serialization.bool;


import lombok.Data;
import org.lemon.dynodao.annotation.DynoDaoHashKey;
import org.lemon.dynodao.annotation.DynoDaoSchema;

@Data
@DynoDaoSchema(tableName = "things")
class Schema {

    @DynoDaoHashKey
    private String hashKey;

    private Boolean booleanObject;
    private boolean primitiveBoolean;

}
