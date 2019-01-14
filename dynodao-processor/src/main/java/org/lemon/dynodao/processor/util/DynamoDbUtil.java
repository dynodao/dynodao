package org.lemon.dynodao.processor.util;

import javax.lang.model.element.TypeElement;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DynamoDbUtil {

    private static final ClassName PAGINATED_LIST = ClassName.get("com.amazonaws.services.dynamodbv2.datamodeling", "PaginatedList");
    private static final ClassName DYNAMO_DB_MAPPER = ClassName.get("com.amazonaws.services.dynamodbv2.datamodeling", "DynamoDBMapper");
    private static final ClassName DYNAMO_DB_QUERY_EXPRESSION = ClassName.get("com.amazonaws.services.dynamodbv2.datamodeling", "DynamoDBQueryExpression");
    private static final ClassName ATTRIBUTE_VALUE = ClassName.get("com.amazonaws.services.dynamodbv2.model", "AttributeValue");

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
        return ATTRIBUTE_VALUE;
    }

}
