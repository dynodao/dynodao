package org.dynodao.processor.itest;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded;
import com.amazonaws.services.dynamodbv2.local.shared.access.AmazonDynamoDBLocal;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.file.Paths;

/**
 * Base class to provide access to in memory DynamoDB operations.
 */
@Disabled
public abstract class AbstractIntegrationTest extends AbstractSourceCompilingTest {

    private static final ThreadLocal<AmazonDynamoDBLocal> DYNAMO_DB_LOCAL = new ThreadLocal<>();

    protected AmazonDynamoDB amazonDynamoDb;

    @BeforeAll
    static void startDynamoDbLocal() {
        String userDir = System.getProperty("user.dir");
        if (userDir != null && !userDir.contains("dynodao-processor")) {
            System.setProperty("sqlite4java.library.path", Paths.get(userDir, "dynodao-processor", "dependencies").toAbsolutePath().toString());
        } else {
            System.setProperty("sqlite4java.library.path", "dependencies");
        }

        DYNAMO_DB_LOCAL.set(DynamoDBEmbedded.create());
    }

    @AfterAll
    static void shutdownDynamoDbLocal() {
        DYNAMO_DB_LOCAL.get().shutdown();
        DYNAMO_DB_LOCAL.remove();
    }

    @BeforeEach
    void createTable() {
        amazonDynamoDb = DYNAMO_DB_LOCAL.get().amazonDynamoDB();
        CreateTableRequest createTableRequest = getCreateTableRequest()
                .withProvisionedThroughput(new ProvisionedThroughput(40000L, 40000L));
        amazonDynamoDb.createTable(createTableRequest);
    }

    @AfterEach
    void destroyTable() {
        amazonDynamoDb.deleteTable(getCreateTableRequest().getTableName());
    }

    /**
     * Returns the {@link CreateTableRequest} for the table that should be created before each test and destroyed after.
     * By default, the staged dynamo builder for {@link AbstractSourceCompilingTest#getCompilationUnitUnderTest()}
     * is reflectively created, and the {@code asCreateTableRequest} is used.
     * @return the {@link CreateTableRequest} for the table to create
     */
    protected CreateTableRequest getCreateTableRequest() {
        return createTableRequest();
    }

    @SneakyThrows(ReflectiveOperationException.class)
    private CreateTableRequest createTableRequest() {
        Class<?> clazz = getCompilationUnitUnderTest();
        Class<?> stagedBuilder = Class.forName(clazz.getCanonicalName() + "StagedDynamoBuilder");
        Constructor<?> ctor = stagedBuilder.getDeclaredConstructor();
        Method asCreateTableRequest = stagedBuilder.getDeclaredMethod("asCreateTableRequest");
        ctor.setAccessible(true);
        asCreateTableRequest.setAccessible(true);
        return (CreateTableRequest) asCreateTableRequest.invoke(ctor.newInstance());
    }

}
