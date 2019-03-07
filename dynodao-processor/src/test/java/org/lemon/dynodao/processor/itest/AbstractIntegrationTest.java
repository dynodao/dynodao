package org.lemon.dynodao.processor.itest;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;

/**
 * Base class to provide access to in memory DynamoDB operations.
 */
@Disabled
public abstract class AbstractIntegrationTest extends AbstractSourceCompilingTest {

    private static DynamoDBProxyServer dynamoDbProxyServer;

    protected AmazonDynamoDB amazonDynamoDb;

    @BeforeAll
    static void startSqlServer() throws Exception {
        System.setProperty("sqlite4java.library.path", "dependencies");
        dynamoDbProxyServer = ServerRunner.createServerFromCommandLineArgs(new String[]{ "-inMemory", "-port", "8000" });
        dynamoDbProxyServer.start();
    }

    @AfterAll
    static void stopSqlServer() throws Exception {
        dynamoDbProxyServer.stop();
    }

    /**
     * Returns the {@link CreateTableRequest} for the table that should be created before each test.
     * @return the {@link CreateTableRequest} for the table to create
     */
    protected abstract CreateTableRequest getCreateTableRequest();

    @BeforeEach
    void createTable() {
        amazonDynamoDb = DynamoDBEmbedded.create().amazonDynamoDB();
        CreateTableRequest createTableRequest = getCreateTableRequest()
                .withProvisionedThroughput(new ProvisionedThroughput(1000L, 1000L));
        amazonDynamoDb.createTable(createTableRequest);
    }

    @AfterEach
    void destroyTable() {
        amazonDynamoDb.shutdown();
    }

}
