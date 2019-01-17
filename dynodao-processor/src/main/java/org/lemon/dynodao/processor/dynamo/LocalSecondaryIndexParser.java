package org.lemon.dynodao.processor.dynamo;

import static java.util.stream.Collectors.toSet;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import org.lemon.dynodao.processor.context.ProcessorContext;

import javax.inject.Inject;
import javax.lang.model.element.TypeElement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Extracts local secondary indexes from the schema document.
 */
class LocalSecondaryIndexParser implements DynamoIndexParser {

    @Inject SchemaFieldsProvider schemaFieldsProvider;
    @Inject ProcessorContext processorContext;

    @Inject LocalSecondaryIndexParser() { }

    @Override
    public Set<DynamoIndex> getIndexesFrom(TypeElement document) {
        Set<DynamoAttribute> hashKeys = new LinkedHashSet<>();
        Set<DynamoAttribute> rangeKeys = new LinkedHashSet<>();
        Set<DynamoAttribute> attributes = new LinkedHashSet<>();
        for (DynamoAttribute attribute : schemaFieldsProvider.getDynamoAttributes(document)) {
            if (attribute.getField().getAnnotation(DynamoDBHashKey.class) != null) {
                hashKeys.add(attribute);
            }
            if (attribute.getField().getAnnotation(DynamoDBIndexRangeKey.class) != null) {
                rangeKeys.add(attribute);
            }
            attributes.add(attribute);
        }

        validate(hashKeys, rangeKeys, attributes);

        return toIndexes(hashKeys, rangeKeys, attributes);
    }

    private void validate(Set<DynamoAttribute> hashKeys, Set<DynamoAttribute> rangeKeys, Set<DynamoAttribute> attributes) {
        if (hashKeys.size() != 1) {
            processorContext.submitErrorMessage("@%s must exist on exactly one field, but found %s", DynamoDBHashKey.class.getSimpleName(), hashKeys);
        }

        Map<String, Set<DynamoAttribute>> rangeKeyByIndex = new HashMap<>();
        for (DynamoAttribute rangeKey : rangeKeys) {
            getIndexNames(rangeKey).forEach(index -> rangeKeyByIndex.computeIfAbsent(index, k -> new HashSet<>()).add(rangeKey));
        }
        rangeKeyByIndex.forEach((index, keys) -> {
            if (keys.size() != 1) {
                processorContext.submitErrorMessage("@%s must exist on exactly one field for LSI[%s], but found %s", DynamoDBIndexRangeKey.class.getSimpleName(), index, keys);
            }
        });
    }

    private Set<String> getIndexNames(DynamoAttribute rangeKey) {
        DynamoDBIndexRangeKey key = rangeKey.getField().getAnnotation(DynamoDBIndexRangeKey.class);
        return Stream.concat(Stream.of(key.localSecondaryIndexName()), Arrays.stream(key.localSecondaryIndexNames()))
                .filter(Objects::nonNull)
                .filter(str -> !str.isEmpty())
                .collect(toSet());
    }


    private Set<DynamoIndex> toIndexes(Set<DynamoAttribute> hashKeys, Set<DynamoAttribute> rangeKeys, Set<DynamoAttribute> attributes) {
        Set<DynamoIndex> indexes = new HashSet<>();
        for (DynamoAttribute rangeKey : rangeKeys) {
            for (String indexName : getIndexNames(rangeKey)) {
                indexes.add(DynamoIndex.builder()
                        .indexType(IndexType.LOCAL_SECONDARY_INDEX)
                        .name(indexName)
                        .hashKeyAttribute(hashKeys.iterator().next())
                        .rangeKeyAttribute(Optional.of(rangeKey))
                        .projectedAttributes(attributes)
                        .build());
            }
        }
        return indexes;
    }

}
