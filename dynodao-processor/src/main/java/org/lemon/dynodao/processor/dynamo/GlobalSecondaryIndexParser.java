package org.lemon.dynodao.processor.dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import org.lemon.dynodao.processor.context.ProcessorMessager;

import javax.inject.Inject;
import javax.lang.model.element.TypeElement;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

/**
 * Extracts global secondary indexes from the schema document.
 */
class GlobalSecondaryIndexParser implements DynamoIndexParser {

    @Inject ProcessorMessager processorMessager;
    @Inject SchemaFieldsProvider schemaFieldsProvider;

    @Inject GlobalSecondaryIndexParser() { }

    @Override
    public Set<DynamoIndex> getIndexesFrom(TypeElement document) {
        Set<DynamoAttribute> hashKeys = new LinkedHashSet<>();
        Set<DynamoAttribute> rangeKeys = new LinkedHashSet<>();
        Set<DynamoAttribute> attributes = new LinkedHashSet<>();
        for (DynamoAttribute attribute : schemaFieldsProvider.getDynamoAttributes(document)) {
            if (attribute.getField().getAnnotation(DynamoDBIndexHashKey.class) != null) {
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
        validateIndex(hashKeys, DynamoDBIndexHashKey.class, this::getHashKeyIndexNames);
        validateIndex(rangeKeys, DynamoDBIndexRangeKey.class, this::getRangeKeyIndexNames);
    }

    private void validateIndex(Set<DynamoAttribute> keys, Class<?> annotation, Function<DynamoAttribute, Set<String>> getIndexNames) {
        Map<String, Set<DynamoAttribute>> keysByIndex = new HashMap<>();
        for (DynamoAttribute key : keys) {
            getIndexNames.apply(key).forEach(index -> keysByIndex.computeIfAbsent(index, k -> new HashSet<>()).add(key));
        }
        keysByIndex.forEach((index, indexKeys) -> {
            if (indexKeys.size() != 1) { // for range key, no entry would be created, it is sufficient to check size=1
                processorMessager.submitError("@%s must exist on exactly one field for GSI[%s], but found %s", annotation.getSimpleName(), index, indexKeys);
            }
        });
    }

    private Set<String> getHashKeyIndexNames(DynamoAttribute hashKey) {
        DynamoDBIndexHashKey key = hashKey.getField().getAnnotation(DynamoDBIndexHashKey.class);
        return Stream.concat(Stream.of(key.globalSecondaryIndexName()), Arrays.stream(key.globalSecondaryIndexNames()))
                .filter(Objects::nonNull)
                .filter(str -> !str.isEmpty())
                .collect(toSet());
    }

    private Set<String> getRangeKeyIndexNames(DynamoAttribute rangeKey) {
        DynamoDBIndexRangeKey key = rangeKey.getField().getAnnotation(DynamoDBIndexRangeKey.class);
        return Stream.concat(Stream.of(key.globalSecondaryIndexName()), Arrays.stream(key.globalSecondaryIndexNames()))
                .filter(Objects::nonNull)
                .filter(str -> !str.isEmpty())
                .collect(toSet());
    }

    private Set<DynamoIndex> toIndexes(Set<DynamoAttribute> hashKeys, Set<DynamoAttribute> rangeKeys, Set<DynamoAttribute> attributes) {
        Set<DynamoIndex> indexes = new HashSet<>();
        for (DynamoAttribute hashKey : hashKeys) {
            for (String indexName : getHashKeyIndexNames(hashKey)) {
                Optional<DynamoAttribute> rangeKey = getRangeKeyForIndex(indexName, rangeKeys);
                indexes.add(DynamoIndex.builder()
                        .indexType(IndexType.GLOBAL_SECONDARY_INDEX)
                        .name(indexName)
                        .hashKeyAttribute(hashKey)
                        .rangeKeyAttribute(rangeKey)
                        .projectedAttributes(attributes)
                        .build());
            }
        }
        return indexes;
    }

    private Optional<DynamoAttribute> getRangeKeyForIndex(String indexName, Set<DynamoAttribute> rangeKeys) {
        for (DynamoAttribute rangeKey : rangeKeys) {
            boolean sameIndex = getRangeKeyIndexNames(rangeKey).contains(indexName);
            if (sameIndex) {
                return Optional.of(rangeKey);
            }
        }
        return Optional.empty();
    }

}
