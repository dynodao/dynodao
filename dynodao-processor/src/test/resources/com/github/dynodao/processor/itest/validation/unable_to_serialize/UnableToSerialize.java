package com.github.dynodao.processor.itest.validation.unable_to_serialize;

import com.github.dynodao.annotation.DynoDaoHashKey;
import com.github.dynodao.annotation.DynoDaoSchema;

@DynoDaoSchema(tableName = "things")
public class UnableToSerialize {

    @DynoDaoHashKey
    private String hashKey;

    private Object unknownType;

}
