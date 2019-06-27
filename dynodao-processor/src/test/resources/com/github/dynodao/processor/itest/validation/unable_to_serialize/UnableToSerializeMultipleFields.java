package com.github.dynodao.processor.itest.validation.unable_to_serialize;

import com.github.dynodao.annotation.DynoDaoHashKey;
import com.github.dynodao.annotation.DynoDaoSchema;

import java.util.Map;

@DynoDaoSchema(tableName = "things")
public class UnableToSerializeMultipleFields {

    @DynoDaoHashKey
    private String hashKey;

    private Object unknownType1;
    private Map<Object, String> unknownType2;

}
