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
import org.junit.jupiter.api.Test;
import org.lemon.dynodao.processor.test.AbstractUnitTest;

import static org.assertj.core.api.Assertions.assertThat;

class DynamoDbUtilTest extends AbstractUnitTest {

    @Test
    void attributeValue_onlyUseCase_returnsAttributeValue() {
        ClassName attributeValue = DynamoDbUtil.attributeValue();
        assertThat(attributeValue).isEqualTo(ClassName.get(AttributeValue.class));
    }

    @Test
    void amazonDynamoDb_onlyUseCase_returnsAmazonDynamoDB() {
        ClassName amazonDynamoDb = DynamoDbUtil.amazonDynamoDb();
        assertThat(amazonDynamoDb).isEqualTo(ClassName.get(AmazonDynamoDB.class));
    }

    @Test
    void getItemRequest_onlyUseCase_returnsGetItemRequest() {
        ClassName getItemRequest = DynamoDbUtil.getItemRequest();
        assertThat(getItemRequest).isEqualTo(ClassName.get(GetItemRequest.class));
    }

    @Test
    void getItemResult_onlyUseCase_returnsGetItemResult() {
        ClassName getItemResult = DynamoDbUtil.getItemResult();
        assertThat(getItemResult).isEqualTo(ClassName.get(GetItemResult.class));
    }

    @Test
    void queryRequest_onlyUseCase_returnsQueryRequest() {
        ClassName queryRequest = DynamoDbUtil.queryRequest();
        assertThat(queryRequest).isEqualTo(ClassName.get(QueryRequest.class));
    }

    @Test
    void queryResult_onlyUseCase_returnsQueryResult() {
        ClassName queryResult = DynamoDbUtil.queryResult();
        assertThat(queryResult).isEqualTo(ClassName.get(QueryResult.class));
    }

    @Test
    void createTableRequest_onlyUseCase_returnsCreateTableRequest() {
        ClassName createTableRequest = DynamoDbUtil.createTableRequest();
        assertThat(createTableRequest).isEqualTo(ClassName.get(CreateTableRequest.class));
    }

    @Test
    void scalarAttributeType_onlyUseCase_returnsScalarAttributeType() {
        ClassName scalarAttributeType = DynamoDbUtil.scalarAttributeType();
        assertThat(scalarAttributeType).isEqualTo(ClassName.get(ScalarAttributeType.class));
    }

    @Test
    void keySchemaElement_onlyUseCase_returnsKeySchemaElement() {
        ClassName keySchemaElement = DynamoDbUtil.keySchemaElement();
        assertThat(keySchemaElement).isEqualTo(ClassName.get(KeySchemaElement.class));
    }

    @Test
    void keyType_onlyUseCase_returnsKeyType() {
        ClassName keyType = DynamoDbUtil.keyType();
        assertThat(keyType).isEqualTo(ClassName.get(KeyType.class));
    }

    @Test
    void attributeDefinition_onlyUseCase_returnsAttributeDefinition() {
        ClassName attributeDefinition = DynamoDbUtil.attributeDefinition();
        assertThat(attributeDefinition).isEqualTo(ClassName.get(AttributeDefinition.class));
    }

    @Test
    void provisionedThroughput_onlyUseCase_returnsProvisionedThroughput() {
        ClassName provisionedThroughput = DynamoDbUtil.provisionedThroughput();
        assertThat(provisionedThroughput).isEqualTo(ClassName.get(ProvisionedThroughput.class));
    }

    @Test
    void projection_onlyUseCase_returnsProjection() {
        ClassName projection = DynamoDbUtil.projection();
        assertThat(projection).isEqualTo(ClassName.get(Projection.class));
    }

    @Test
    void projectionType_onlyUseCase_returnsProjectionType() {
        ClassName projectionType = DynamoDbUtil.projectionType();
        assertThat(projectionType).isEqualTo(ClassName.get(ProjectionType.class));
    }

    @Test
    void localSecondaryIndex_onlyUseCase_returnsLocalSecondaryIndex() {
        ClassName localSecondaryIndex = DynamoDbUtil.localSecondaryIndex();
        assertThat(localSecondaryIndex).isEqualTo(ClassName.get(LocalSecondaryIndex.class));
    }

    @Test
    void globalSecondaryIndex_onlyUseCase_returnsGlobalSecondaryIndex() {
        ClassName globalSecondaryIndex = DynamoDbUtil.globalSecondaryIndex();
        assertThat(globalSecondaryIndex).isEqualTo(ClassName.get(GlobalSecondaryIndex.class));
    }

}
