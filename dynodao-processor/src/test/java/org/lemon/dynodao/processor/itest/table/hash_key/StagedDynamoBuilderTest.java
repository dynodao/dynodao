package org.lemon.dynodao.processor.itest.table.hash_key;

import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import org.junit.jupiter.api.Test;
import org.lemon.dynodao.processor.itest.AbstractSourceCompilingTest;

import static org.assertj.core.api.Assertions.assertThat;

class StagedDynamoBuilderTest extends AbstractSourceCompilingTest {

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
    void usingTable_onlyUseCase_returnsIndexIntermediaryNode() {
        TableSchema tableSchema = new SchemaStagedDynamoBuilder().usingTable();
        assertThat(tableSchema).isEqualTo(new TableSchema());
    }

}

class TableSchemaTest extends AbstractSourceCompilingTest {

    @Test
    void withHashKey_onlyUseCase_returnsLoadNode() {
        TableHashKeySchemaDynoDaoLoad load = new SchemaStagedDynamoBuilder()
                .usingTable()
                .withHashKey("value");
        assertThat(load).isEqualTo(new TableHashKeySchemaDynoDaoLoad("value"));
    }

}
