package com.github.dynodao.playground;

import com.github.dynodao.annotation.DynoDaoAttribute;
import com.github.dynodao.annotation.DynoDaoDocument;
import com.github.dynodao.annotation.DynoDaoHashKey;
import com.github.dynodao.annotation.DynoDaoIndexHashKey;
import com.github.dynodao.annotation.DynoDaoIndexRangeKey;
import com.github.dynodao.annotation.DynoDaoRangeKey;
import com.github.dynodao.annotation.DynoDaoSchema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

@Data
@NoArgsConstructor
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
    private SortedMap<String, Integer> sortedMapOfInt;
    private HashMap<String, Map<String, String>> hashMapOfMaps;

    private NestedModel nestedModel;
//    private Map<String, NestedModel> nestedModelMap;
//    private List<NestedModel> nestedModelList;

}

@Data
@NoArgsConstructor
@DynoDaoDocument
class NestedModel {

    private String string;

}
