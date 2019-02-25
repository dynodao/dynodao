package org.lemon.dynodao.processor.util;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import lombok.experimental.UtilityClass;

import javax.lang.model.element.TypeElement;

/**
 * Utility methods related to dynamoDb itself.
 */
@UtilityClass
public class DynamoDbUtil {

    private static final ClassName PAGINATED_LIST = ClassName.get("com.amazonaws.services.dynamodbv2.datamodeling", "PaginatedList");
    private static final ClassName DYNAMO_DB_MAPPER = ClassName.get("com.amazonaws.services.dynamodbv2.datamodeling", "DynamoDBMapper");
    private static final ClassName DYNAMO_DB_QUERY_EXPRESSION = ClassName.get("com.amazonaws.services.dynamodbv2.datamodeling", "DynamoDBQueryExpression");

    /**
     * @param document the document type
     * @return the type name for {@link com.amazonaws.services.dynamodbv2.datamodeling.PaginatedList} with the document as the template type
     */
    public static ParameterizedTypeName paginatedList(TypeElement document) {
        return ParameterizedTypeName.get(PAGINATED_LIST, TypeName.get(document.asType()));
    }

    /**
     * @return the {@link com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper} type
     */
    public static ClassName dynamoDbMapper() {
        return DYNAMO_DB_MAPPER;
    }

    /**
     * @param document the document type
     * @return the type name for {@link com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression} with the document as the template type
     */
    public static ParameterizedTypeName dynamoDbQueryExpression(TypeElement document) {
        return ParameterizedTypeName.get(DYNAMO_DB_QUERY_EXPRESSION, TypeName.get(document.asType()));
    }

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
