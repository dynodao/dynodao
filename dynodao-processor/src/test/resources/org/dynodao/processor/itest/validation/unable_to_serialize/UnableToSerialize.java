package org.dynodao.processor.itest.validation.unable_to_serialize;

import org.dynodao.annotation.DynoDaoHashKey;
import org.dynodao.annotation.DynoDaoSchema;

@DynoDaoSchema(tableName = "things")
public class UnableToSerialize {

    @DynoDaoHashKey
    private String hashKey;

    private Object unknownType;

}
