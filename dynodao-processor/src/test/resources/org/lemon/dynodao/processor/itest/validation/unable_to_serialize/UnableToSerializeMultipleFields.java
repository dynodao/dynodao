package org.lemon.dynodao.processor.itest.validation.unable_to_serialize;

import org.lemon.dynodao.annotation.DynoDaoHashKey;
import org.lemon.dynodao.annotation.DynoDaoSchema;

import java.util.Map;

@DynoDaoSchema(tableName = "things")
public class UnableToSerializeMultipleFields {

    @DynoDaoHashKey
    private String hashKey;

    private Object unknownType1;
    private Map<Object, String> unknownType2;

}
