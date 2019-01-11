package org.lemon.dynodao.processor.generate;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterSpec;

import lombok.experimental.UtilityClass;

@UtilityClass
class GenerateConstants {

    static final ClassName PAGINATED_LIST = ClassName.get("com.amazonaws.services.dynamodbv2.datamodeling", "PaginatedList");
    static final ClassName ATTRIBUTE_VALUE = ClassName.get("com.amazonaws.services.dynamodbv2.model", "AttributeValue");
    static final ClassName DYNAMO_DB_MAPPER = ClassName.get("com.amazonaws.services.dynamodbv2.datamodeling", "DynamoDBMapper");
    static final ClassName DYNAMO_DB_QUERY_EXPRESSION = ClassName.get("com.amazonaws.services.dynamodbv2.datamodeling", "DynamoDBQueryExpression");

    static final ParameterSpec DYNAMO_DB_MAPPER_PARAM = ParameterSpec.builder(DYNAMO_DB_MAPPER, "dynamoDbMapper").build();


}
