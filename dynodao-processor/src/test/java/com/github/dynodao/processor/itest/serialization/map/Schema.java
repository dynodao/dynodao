package com.github.dynodao.processor.itest.serialization.map;

import com.github.dynodao.annotation.DynoDaoHashKey;
import com.github.dynodao.annotation.DynoDaoSchema;
import com.github.dynodao.processor.test.PackageScanner;
import lombok.Data;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

@Data
@DynoDaoSchema(tableName = "things")
class Schema {

    @DynoDaoHashKey
    private String hashKey;

    /**
     * We should only have to test against one value type as the serializer delegates to other methods
     * which will be tested by other classes.
     */
    // -- typical java maps --
    private Map<String, String> map;
    private HashMap<String, String> hashMap;
    private LinkedHashMap<String, String> linkedHashMap;
    private SortedMap<String, String> sortedMap;
    private NavigableMap<String, String> navigableMap;
    private TreeMap<String, String> treeMap;

    // -- lesser used simple maps --
    private IdentityHashMap<String, String> identityHashMap;
    private WeakHashMap<String, String> weakHashMap;

    // -- java concurrent maps --
    private ConcurrentMap<String, String> concurrentMap;
    private ConcurrentHashMap<String, String> concurrentHashMap;
    private ConcurrentNavigableMap<String, String> concurrentNavigableMap;
    private ConcurrentSkipListMap<String, String> concurrentSkipListMap;

    // -- an unknown map type, we should new this up as the implementation type --
    private Hashtable<String, String> hashtable;

    // -- map implementations with different type arguments --
    private NoTypeArgsMap noTypeArgsMap;
    private KeyTypeArgMap<String> keyTypeArgMap;
    private ValueTypeArgMap<String> valueTypeArgMap;

}

@PackageScanner.Ignore
class NoTypeArgsMap extends HashMap<String, String> {
}

@PackageScanner.Ignore
class KeyTypeArgMap<T> extends HashMap<T, String> {
}

@PackageScanner.Ignore
class ValueTypeArgMap<T> extends HashMap<String, T> {
}
