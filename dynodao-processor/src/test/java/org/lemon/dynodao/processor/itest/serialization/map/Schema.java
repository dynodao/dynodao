package org.lemon.dynodao.processor.itest.serialization.map;

import lombok.Data;
import org.lemon.dynodao.annotation.DynoDaoHashKey;
import org.lemon.dynodao.annotation.DynoDaoSchema;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

@Data
@DynoDaoSchema(tableName = "things")
class Schema {

    @DynoDaoHashKey
    private String hashKey;

    /**
     * We should only have to test against one value type as the serializer delegates to other methods
     * which will be tested by other classes.
     */
    private Map<String, String> map;
    private HashMap<String, String> hashMap;
    private LinkedHashMap<String, String> linkedHashMap;
    private TreeMap<String, String> treeMap;

}

