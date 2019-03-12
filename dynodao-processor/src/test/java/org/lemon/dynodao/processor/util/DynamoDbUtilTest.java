package org.lemon.dynodao.processor.util;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.LocalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import org.junit.jupiter.api.Test;
import org.lemon.dynodao.processor.test.AbstractUnitTest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DynamoDbUtilTest extends AbstractUnitTest {

    @Test
    void attributeValue_onlyUseCase_returnsAttributeValue() {
        ClassName attributeValue = DynamoDbUtil.attributeValue();
        assertThat(attributeValue).isEqualTo(TypeName.get(AttributeValue.class));
    }

    @Test
    void item_onlyUseCase_returnsMapOfStringToAttributeValue() {
        ParameterizedTypeName item = DynamoDbUtil.item();
        assertThat(item).isEqualTo(ParameterizedTypeName.get(ClassName.get(Map.class), TypeName.get(String.class), TypeName.get(AttributeValue.class)));
    }

    @Test
    void amazonDynamoDb_onlyUseCase_returnsAmazonDynamoDB() {
        ClassName amazonDynamoDb = DynamoDbUtil.amazonDynamoDb();
        assertThat(amazonDynamoDb).isEqualTo(TypeName.get(AmazonDynamoDB.class));
    }

    @Test
    void getItemRequest_onlyUseCase_returnsGetItemRequest() {
        ClassName getItemRequest = DynamoDbUtil.getItemRequest();
        assertThat(getItemRequest).isEqualTo(TypeName.get(GetItemRequest.class));
    }

    @Test
    void getItemResult_onlyUseCase_returnsGetItemResult() {
        ClassName getItemResult = DynamoDbUtil.getItemResult();
        assertThat(getItemResult).isEqualTo(TypeName.get(GetItemResult.class));
    }

    @Test
    void queryRequest_onlyUseCase_returnsQueryRequest() {
        ClassName queryRequest = DynamoDbUtil.queryRequest();
        assertThat(queryRequest).isEqualTo(TypeName.get(QueryRequest.class));
    }

    @Test
    void queryResult_onlyUseCase_returnsQueryResult() {
        ClassName queryResult = DynamoDbUtil.queryResult();
        assertThat(queryResult).isEqualTo(TypeName.get(QueryResult.class));
    }

    @Test
    void createTableRequest_onlyUseCase_returnsCreateTableRequest() {
        ClassName createTableRequest = DynamoDbUtil.createTableRequest();
        assertThat(createTableRequest).isEqualTo(TypeName.get(CreateTableRequest.class));
    }

    @Test
    void scalarAttributeType_onlyUseCase_returnsScalarAttributeType() {
        ClassName scalarAttributeType = DynamoDbUtil.scalarAttributeType();
        assertThat(scalarAttributeType).isEqualTo(TypeName.get(ScalarAttributeType.class));
    }

    @Test
    void keySchemaElement_onlyUseCase_returnsKeySchemaElement() {
        ClassName keySchemaElement = DynamoDbUtil.keySchemaElement();
        assertThat(keySchemaElement).isEqualTo(TypeName.get(KeySchemaElement.class));
    }

    @Test
    void keyType_onlyUseCase_returnsKeyType() {
        ClassName keyType = DynamoDbUtil.keyType();
        assertThat(keyType).isEqualTo(TypeName.get(KeyType.class));
    }

    @Test
    void attributeDefinition_onlyUseCase_returnsAttributeDefinition() {
        ClassName attributeDefinition = DynamoDbUtil.attributeDefinition();
        assertThat(attributeDefinition).isEqualTo(TypeName.get(AttributeDefinition.class));
    }

    @Test
    void provisionedThroughput_onlyUseCase_returnsProvisionedThroughput() {
        ClassName provisionedThroughput = DynamoDbUtil.provisionedThroughput();
        assertThat(provisionedThroughput).isEqualTo(TypeName.get(ProvisionedThroughput.class));
    }

    @Test
    void projection_onlyUseCase_returnsProjection() {
        ClassName projection = DynamoDbUtil.projection();
        assertThat(projection).isEqualTo(TypeName.get(Projection.class));
    }

    @Test
    void projectionType_onlyUseCase_returnsProjectionType() {
        ClassName projectionType = DynamoDbUtil.projectionType();
        assertThat(projectionType).isEqualTo(TypeName.get(ProjectionType.class));
    }

    @Test
    void localSecondaryIndex_onlyUseCase_returnsLocalSecondaryIndex() {
        ClassName localSecondaryIndex = DynamoDbUtil.localSecondaryIndex();
        assertThat(localSecondaryIndex).isEqualTo(TypeName.get(LocalSecondaryIndex.class));
    }

    @Test
    void globalSecondaryIndex_onlyUseCase_returnsGlobalSecondaryIndex() {
        ClassName globalSecondaryIndex = DynamoDbUtil.globalSecondaryIndex();
        assertThat(globalSecondaryIndex).isEqualTo(TypeName.get(GlobalSecondaryIndex.class));
    }

}
