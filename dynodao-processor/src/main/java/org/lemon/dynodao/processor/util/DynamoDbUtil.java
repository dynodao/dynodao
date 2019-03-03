package org.lemon.dynodao.processor.util;

import com.squareup.javapoet.ClassName;
import lombok.experimental.UtilityClass;

/**
 * Utility methods related to dynamoDb itself.
 */
@UtilityClass
public class DynamoDbUtil {

    /**
     * @return the {@link com.amazonaws.services.dynamodbv2.model.AttributeValue} type
     */
    public static ClassName attributeValue() {
        return ClassName.get("com.amazonaws.services.dynamodbv2.model", "AttributeValue");
    }

    /**
     * @return the {@link com.amazonaws.services.dynamodbv2.AmazonDynamoDB} type
     */
    public static ClassName amazonDynamoDb() {
        return ClassName.get("com.amazonaws.services.dynamodbv2", "AmazonDynamoDB");
    }

    /**
     * @return the {@link com.amazonaws.services.dynamodbv2.model.GetItemRequest} type
     */
    public static ClassName getItemRequest() {
        return ClassName.get("com.amazonaws.services.dynamodbv2.model", "GetItemRequest");
    }

    /**
     * @return the {@link com.amazonaws.services.dynamodbv2.model.GetItemResult} type
     */
    public static ClassName getItemResult() {
        return ClassName.get("com.amazonaws.services.dynamodbv2.model", "GetItemResult");
    }

    /**
     * @return the {@link com.amazonaws.services.dynamodbv2.model.QueryRequest} type
     */
    public static ClassName queryRequest() {
        return ClassName.get("com.amazonaws.services.dynamodbv2.model", "QueryRequest");
    }

    /**
     * @return the {@link com.amazonaws.services.dynamodbv2.model.QueryResult} type
     */
    public static ClassName queryResult() {
        return ClassName.get("com.amazonaws.services.dynamodbv2.model", "QueryResult");
    }

}
