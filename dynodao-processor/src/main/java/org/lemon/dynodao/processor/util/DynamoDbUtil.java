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

    /**
     * @return the {@link com.amazonaws.services.dynamodbv2.model.CreateTableRequest} type
     */
    public static ClassName createTableRequest() {
        return ClassName.get("com.amazonaws.services.dynamodbv2.model", "CreateTableRequest");
    }

    /**
     * @return the {@link com.amazonaws.services.dynamodbv2.model.ScalarAttributeType} type
     */
    public static ClassName scalarAttributeType() {
        return ClassName.get("com.amazonaws.services.dynamodbv2.model", "ScalarAttributeType");
    }

    /**
     * @return the {@link com.amazonaws.services.dynamodbv2.model.KeySchemaElement} type
     */
    public static ClassName keySchemaElement() {
        return ClassName.get("com.amazonaws.services.dynamodbv2.model", "KeySchemaElement");
    }

    /**
     * @return the {@link com.amazonaws.services.dynamodbv2.model.KeyType} type
     */
    public static ClassName keyType() {
        return ClassName.get("com.amazonaws.services.dynamodbv2.model", "KeyType");
    }

    /**
     * @return the {@link com.amazonaws.services.dynamodbv2.model.AttributeDefinition} type
     */
    public static ClassName attributeDefinition() {
        return ClassName.get("com.amazonaws.services.dynamodbv2.model", "AttributeDefinition");
    }

    /**
     * @return the {@link com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput} type
     */
    public static ClassName provisionedThroughput() {
        return ClassName.get("com.amazonaws.services.dynamodbv2.model", "ProvisionedThroughput");
    }

    /**
     * @return the {@link com.amazonaws.services.dynamodbv2.model.Projection} type
     */
    public static ClassName projection() {
        return ClassName.get("com.amazonaws.services.dynamodbv2.model", "Projection");
    }

    /**
     * @return the {@link com.amazonaws.services.dynamodbv2.model.ProjectionType} type
     */
    public static ClassName projectionType() {
        return ClassName.get("com.amazonaws.services.dynamodbv2.model", "ProjectionType");
    }

    /**
     * @return the {@link com.amazonaws.services.dynamodbv2.model.LocalSecondaryIndex} type
     */
    public static ClassName localSecondaryIndex() {
        return ClassName.get("com.amazonaws.services.dynamodbv2.model", "LocalSecondaryIndex");
    }

    /**
     * @return the {@link com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex} type
     */
    public static ClassName globalSecondaryIndex() {
        return ClassName.get("com.amazonaws.services.dynamodbv2.model", "GlobalSecondaryIndex");
    }

}
