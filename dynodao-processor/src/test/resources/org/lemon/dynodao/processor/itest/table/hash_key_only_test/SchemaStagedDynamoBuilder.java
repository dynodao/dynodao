package org.lemon.dynodao.processor.itest.table.hash_key_only_test;

import javax.annotation.Generated;
import java.util.Objects;

@Generated(
        value = "org.lemon.dynodao.processor",
        comments = "https://github.com/twentylemon/dynodao"
)
public class SchemaStagedDynamoBuilder {

    public TableSchema usingTable() {
        return new TableSchema();
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof SchemaStagedDynamoBuilder;
    }

    @Override
    public int hashCode() {
        return Objects.hash();
    }

    @Override
    public String toString() {
        return "SchemaStagedDynamoBuilder()";
    }
}
