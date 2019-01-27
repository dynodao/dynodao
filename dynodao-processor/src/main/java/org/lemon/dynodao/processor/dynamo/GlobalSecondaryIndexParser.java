package org.lemon.dynodao.processor.dynamo;

import org.lemon.dynodao.annotation.DynoDaoIndexHashKey;
import org.lemon.dynodao.annotation.DynoDaoIndexRangeKey;
import org.lemon.dynodao.processor.context.ProcessorMessager;

import javax.inject.Inject;
import javax.lang.model.element.TypeElement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Extracts global secondary indexes from the schema document.
 */
class GlobalSecondaryIndexParser implements DynamoIndexParser {

    private final ProcessorMessager processorMessager;
    private final SchemaFieldsProvider schemaFieldsProvider;

    @Inject GlobalSecondaryIndexParser(ProcessorMessager processorMessager, SchemaFieldsProvider schemaFieldsProvider) {
        this.processorMessager = processorMessager;
        this.schemaFieldsProvider = schemaFieldsProvider;
    }

    @Override
    public Set<DynamoIndex> getIndexesFrom(TypeElement document) {
        Set<DynamoAttribute> hashKeys = new LinkedHashSet<>();
        Set<DynamoAttribute> rangeKeys = new LinkedHashSet<>();
        Set<DynamoAttribute> attributes = new LinkedHashSet<>();
        for (DynamoAttribute attribute : schemaFieldsProvider.getDynamoAttributes(document)) {
            if (attribute.getField().getAnnotation(DynoDaoIndexHashKey.class) != null) {
                hashKeys.add(attribute);
            }
            if (attribute.getField().getAnnotation(DynoDaoIndexRangeKey.class) != null) {
                rangeKeys.add(attribute);
            }
            attributes.add(attribute);
        }

        validate(hashKeys, rangeKeys, attributes);

        return toIndexes(hashKeys, rangeKeys, attributes);
    }

    private void validate(Set<DynamoAttribute> hashKeys, Set<DynamoAttribute> rangeKeys, Set<DynamoAttribute> attributes) {
        validateIndex(hashKeys, DynoDaoIndexHashKey.class, this::getHashKeyIndexNames);
        validateIndex(rangeKeys, DynoDaoIndexRangeKey.class, this::getRangeKeyIndexNames);
    }

    private void validateIndex(Set<DynamoAttribute> keys, Class<?> annotation, Function<DynamoAttribute, Set<String>> getIndexNames) {
        Map<String, Set<DynamoAttribute>> keysByIndex = new HashMap<>();
        for (DynamoAttribute key : keys) {
            getIndexNames.apply(key).forEach(index -> keysByIndex.computeIfAbsent(index, k -> new HashSet<>()).add(key));
        }
        keysByIndex.forEach((index, indexKeys) -> {
            if (indexKeys.size() != 1) { // for range key, no entry would be created if it was absent, it is sufficient to check size=1
                processorMessager.submitError("@%s must exist on exactly one field for GSI[%s], but found %s", annotation.getSimpleName(), index, indexKeys);
            }
        });
    }

    private Set<String> getHashKeyIndexNames(DynamoAttribute hashKey) {
        DynoDaoIndexHashKey key = hashKey.getField().getAnnotation(DynoDaoIndexHashKey.class);
        return new HashSet<>(Arrays.asList(key.gsiNames()));
    }

    private Set<String> getRangeKeyIndexNames(DynamoAttribute rangeKey) {
        DynoDaoIndexRangeKey key = rangeKey.getField().getAnnotation(DynoDaoIndexRangeKey.class);
        return new HashSet<>(Arrays.asList(key.gsiNames()));
    }

    private Set<DynamoIndex> toIndexes(Set<DynamoAttribute> hashKeys, Set<DynamoAttribute> rangeKeys, Set<DynamoAttribute> attributes) {
        Set<DynamoIndex> indexes = new LinkedHashSet<>();
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
