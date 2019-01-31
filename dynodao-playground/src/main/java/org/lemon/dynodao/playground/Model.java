package org.lemon.dynodao.playground;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.lemon.dynodao.annotation.DynoDaoAttribute;
import org.lemon.dynodao.annotation.DynoDaoHashKey;
import org.lemon.dynodao.annotation.DynoDaoIndexHashKey;
import org.lemon.dynodao.annotation.DynoDaoIndexRangeKey;
import org.lemon.dynodao.annotation.DynoDaoRangeKey;
import org.lemon.dynodao.annotation.DynoDaoSchema;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynoDaoSchema(tableName = "models")
public class Model {

    @DynoDaoHashKey
    private String hashKey;
    @DynoDaoRangeKey
    private String rangeKey;

    @DynoDaoIndexRangeKey(lsiNames = "local-index-name")
    private String lsiRangeKey;

    @DynoDaoIndexHashKey(gsiNames = { "global-index-name", "global-index-2" })
    private String gsiHashKey;

    @DynoDaoIndexRangeKey(gsiNames = "global-index-name")
    private long gsiRangeKey;

    @DynoDaoIndexRangeKey(gsiNames = "global-index-2")
    private String gsiRangeKey2;

    @DynoDaoIndexHashKey(gsiNames = "global-solo-index")
    private String soloGsiHashKey;

    @DynoDaoAttribute("ATTRIBUTE")
    private int attribute;
    private long attribute2;
    private BigDecimal attribute3;
    private Integer attribute4;

    private Map<String, BigInteger> mapOfBigInt;

}
