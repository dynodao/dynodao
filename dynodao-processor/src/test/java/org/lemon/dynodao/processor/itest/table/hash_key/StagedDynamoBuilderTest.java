package org.lemon.dynodao.processor.itest.table.hash_key;

import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import org.junit.jupiter.api.Test;
import org.lemon.dynodao.processor.test.AbstractUnitTest;

import static org.assertj.core.api.Assertions.assertThat;

class StagedDynamoBuilderTest extends AbstractUnitTest {

    @Test
    void asCreateTableRequest_onlyUseCase_returnsCorrectRequest() {
        CreateTableRequest request = new SchemaStagedDynamoBuilder().asCreateTableRequest();
        assertThat(request).isEqualTo(new CreateTableRequest()
                .withTableName("things")
                .withAttributeDefinitions(new AttributeDefinition("hashKey", ScalarAttributeType.S))
                .withKeySchema(new KeySchemaElement("hashKey", KeyType.HASH))
                .withProvisionedThroughput(new ProvisionedThroughput(5L, 5L)));
    }

    @Test
    void usingTable_onlyUseCase_returnsIndexIntermediaryStage() {
        TableSchema tableSchema = new SchemaStagedDynamoBuilder().usingTable();
        assertThat(tableSchema).isEqualTo(new TableSchema());
    }

}

class TableSchemaTest extends AbstractUnitTest {

    @Test
    void asScanRequest_onlyUseCase_returnsCorrectRequest() {
        ScanRequest request = new SchemaStagedDynamoBuilder()
                .usingTable()
                .asScanRequest();
        assertThat(request).isEqualTo(new ScanRequest()
                .withTableName("things"));
    }

    @Test
    void withHashKey_onlyUseCase_returnsLoadStage() {
        TableHashKeySchema load = new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey("value");
        assertThat(load).isEqualTo(new TableHashKeySchema("value"));
    }

}

class TableHashKeySchemaTest extends AbstractUnitTest {

    @Test
    void asGetItemRequest_onlyUseCase_returnsCorrectRequest() {
        GetItemRequest request = new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey("value")
                .asGetItemRequest();
        assertThat(request).isEqualTo(new GetItemRequest()
                .withTableName("things")
                .addKeyEntry("hashKey", new AttributeValue("value")));
    }

}
