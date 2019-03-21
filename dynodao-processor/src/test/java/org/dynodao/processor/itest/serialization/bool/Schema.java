package org.dynodao.processor.itest.serialization.bool;


import lombok.Data;
import org.dynodao.annotation.DynoDaoHashKey;
import org.dynodao.annotation.DynoDaoSchema;

@Data
@DynoDaoSchema(tableName = "things")
class Schema {

    @DynoDaoHashKey
    private String hashKey;

    private Boolean booleanObject;
    private boolean primitiveBoolean;

}
