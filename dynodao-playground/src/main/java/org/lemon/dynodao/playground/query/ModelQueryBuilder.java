package org.lemon.dynodao.playground.query;

public class ModelQueryBuilder {

    public HashKeyModelQuery withHashKey(String hashKey) {
        return new HashKeyModelQuery(hashKey);
    }

    public GsiHashKeyModelQuery withGsiHashKey(String gsiHashKey) {
        return new GsiHashKeyModelQuery(gsiHashKey);
    }



}
