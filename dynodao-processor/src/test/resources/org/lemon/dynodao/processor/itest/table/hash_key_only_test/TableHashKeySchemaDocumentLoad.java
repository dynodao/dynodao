package org.lemon.dynodao.processor.itest.table.hash_key_only_test;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import org.lemon.dynodao.DocumentLoad;

import javax.annotation.Generated;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Generated(
        value = "org.lemon.dynodao.processor",
        comments = "https://github.com/twentylemon/dynodao"
)
public class TableHashKeySchemaDocumentLoad implements DocumentLoad<Schema> {
    private final String hashKey;

    TableHashKeySchemaDocumentLoad(String hashKey) {
        this.hashKey = hashKey;
    }

    @Override
    public List<Schema> load(DynamoDBMapper dynamoDbMapper) {
        return Collections.singletonList(dynamoDbMapper.load(Schema.class, hashKey));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof TableHashKeySchemaDocumentLoad) {
            TableHashKeySchemaDocumentLoad rhs = (TableHashKeySchemaDocumentLoad) obj;
            return Objects.equals(this.hashKey, rhs.hashKey);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(hashKey);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TableHashKeySchemaDocumentLoad(");
        sb.append("hashKey=").append(hashKey);
        sb.append(")");
        return sb.toString();
    }
}
