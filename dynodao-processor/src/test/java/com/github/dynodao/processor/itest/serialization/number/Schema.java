package com.github.dynodao.processor.itest.serialization.number;

import com.github.dynodao.annotation.DynoDaoHashKey;
import com.github.dynodao.annotation.DynoDaoSchema;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
@DynoDaoSchema(tableName = "things")
class Schema {

    @DynoDaoHashKey
    private String hashKey;

    private byte primitiveByte;
    private short primitiveShort;
    private int primitiveInt;
    private long primitiveLong;
    private float primitiveFloat;
    private double primitiveDouble;

    private Byte byteObject;
    private Short shortObject;
    private Integer integerObject;
    private Long longObject;
    private Float floatObject;
    private Double doubleObject;

    private BigInteger bigInteger;
    private BigDecimal bigDecimal;

}
