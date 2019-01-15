package org.lemon.dynodao.playground;

import java.util.List;

import org.lemon.dynodao.DocumentDao;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

public class DynoDaoPlayground {

    public static void main(String[] args) {
        List<Model> models = new DocumentDao(null).get(new ModelStagedDynamoBuilder()
                .usingLocalIndexName()
                .withHashKey("hashKey")
                .withLsiRangeKey("lsiRangeKey"));
    }

    public static class ModelAttributeValueFactory {

        private static final Object o = new Object();

        public static AttributeValue hashKeyToAttributeValue(String hashKey) {
            return new AttributeValue().withS(hashKey);
        }

    }

}
