package org.lemon.dynodao.processor.itest.validation.unable_to_serialize;

import org.lemon.dynodao.annotation.DynoDaoHashKey;
import org.lemon.dynodao.annotation.DynoDaoSchema;

@DynoDaoSchema(tableName = "things")
public class UnableToSerialize {

    @DynoDaoHashKey
    private String hashKey;

    private Object unknownType;

}
