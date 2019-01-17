package org.lemon.dynodao.processor.dynamo;

import static java.util.Collections.singleton;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import org.lemon.dynodao.processor.context.ProcessorContext;

import javax.inject.Inject;
import javax.lang.model.element.TypeElement;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Extracts the overall table "index" from the schema document.
 */
class TableIndexParser implements DynamoIndexParser {

    @Inject SchemaFieldsProvider schemaFieldsProvider;
    @Inject ProcessorContext processorContext;

    @Inject TableIndexParser() { }

    @Override
    public Set<DynamoIndex> getIndexesFrom(TypeElement document) {
        Set<DynamoAttribute> hashKeys = new LinkedHashSet<>();
        Set<DynamoAttribute> rangeKeys = new LinkedHashSet<>();
        Set<DynamoAttribute> attributes = new LinkedHashSet<>();
        for (DynamoAttribute attribute : schemaFieldsProvider.getDynamoAttributes(document)) {
            if (attribute.getField().getAnnotation(DynamoDBHashKey.class) != null) {
                hashKeys.add(attribute);
            }
            if (attribute.getField().getAnnotation(DynamoDBRangeKey.class) != null) {
                rangeKeys.add(attribute);
            }
            attributes.add(attribute);
        }

        validate(hashKeys, rangeKeys, attributes);

        return singleton(toIndex(hashKeys, rangeKeys, attributes));
    }

    private void validate(Set<DynamoAttribute> hashKeys, Set<DynamoAttribute> rangeKeys, Set<DynamoAttribute> attributes) {
        if (hashKeys.size() != 1) {
            processorContext.submitErrorMessage("@%s must exist on exactly one field, but found %s", DynamoDBHashKey.class.getSimpleName(), hashKeys);
        }
        if (rangeKeys.size() > 1) {
            processorContext.submitErrorMessage("@%s must exist on at most one field, but found %s", DynamoDBRangeKey.class.getSimpleName(), rangeKeys);
        }
    }

    private DynamoIndex toIndex(Set<DynamoAttribute> hashKeys, Set<DynamoAttribute> rangeKeys, Set<DynamoAttribute> attributes) {
        return DynamoIndex.builder()
                .indexType(IndexType.TABLE)
                .name("<table>")
                .hashKeyAttribute(hashKeys.iterator().next())
                .rangeKeyAttribute(rangeKeys.stream().findAny())
                .projectedAttributes(attributes)
                .build();
    }

}
