package org.lemon.dynodao.test.hash_key_only;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import org.lemon.dynodao.test.AbstractIntegrationTest;

public class HashKeyOnlyTest extends AbstractIntegrationTest {

    @Test
    public void stagedDynamoBuilder_equals_success() {
        EqualsVerifier.forClass(SchemaStagedDynamoBuilder.class).verify();
    }

    @Test
    public void tableHashKeySchemaDocumentLoad_equals_success() {
        EqualsVerifier.forClass(TableHashKeySchemaDynoDaoLoad.class).verify();
    }

    @Test
    public void tableSchema_equals_success() {
        EqualsVerifier.forClass(TableSchema.class).verify();
    }

}
