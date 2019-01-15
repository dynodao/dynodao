package org.lemon.dynodao.playground;

import java.util.List;

import org.lemon.dynodao.DocumentDao;

public class DynoDaoPlayground {

    public static void main(String[] args) {
        List<Model> models = new DocumentDao(null).get(new ModelStagedDynamoBuilder()
                .usingLocalIndexName()
                .withHashKey("hashKey")
                .withLsiRangeKey("lsiRangeKey"));
    }

}
