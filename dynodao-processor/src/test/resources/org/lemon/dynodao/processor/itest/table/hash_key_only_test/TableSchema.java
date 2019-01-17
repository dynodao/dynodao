package org.lemon.dynodao.processor.itest.table.hash_key_only_test;

import javax.annotation.Generated;
import java.util.Objects;

@Generated(
        value = "org.lemon.dynodao.processor",
        comments = "https://github.com/twentylemon/dynodao"
)
public class TableSchema {

    public TableHashKeySchemaDocumentLoad withHashKey(String hashKey) {
        return new TableHashKeySchemaDocumentLoad(hashKey);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj instanceof TableSchema;
    }

    @Override
    public int hashCode() {
        return Objects.hash();
    }

    @Override
    public String toString() {
        return "TableSchema()";
    }
}
