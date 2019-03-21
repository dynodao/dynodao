package org.dynodao.processor.schema.index;

import org.dynodao.annotation.DynoDaoIndexHashKey;
import org.dynodao.annotation.DynoDaoIndexRangeKey;
import org.dynodao.processor.context.ProcessorMessager;
import org.dynodao.processor.schema.attribute.DocumentDynamoAttribute;
import org.dynodao.processor.schema.attribute.DynamoAttribute;

import javax.inject.Inject;
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

    @Inject GlobalSecondaryIndexParser(ProcessorMessager processorMessager) {
        this.processorMessager = processorMessager;
    }

    @Override
    public Set<DynamoIndex> getIndexesFrom(DocumentDynamoAttribute document) {
        Set<DynamoAttribute> hashKeys = new LinkedHashSet<>();
        Set<DynamoAttribute> rangeKeys = new LinkedHashSet<>();
        Set<DynamoAttribute> attributes = new LinkedHashSet<>();

        for (DynamoAttribute attribute : document.getAttributes()) {
            if (attribute.getElement().getAnnotation(DynoDaoIndexHashKey.class) != null) {
                hashKeys.add(attribute);
            }
            if (attribute.getElement().getAnnotation(DynoDaoIndexRangeKey.class) != null) {
                rangeKeys.add(attribute);
            }
            attributes.add(attribute);
        }

        validate(hashKeys, rangeKeys);

        return toIndexes(hashKeys, rangeKeys, attributes);
    }

    private void validate(Set<DynamoAttribute> hashKeys, Set<DynamoAttribute> rangeKeys) {
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
        DynoDaoIndexHashKey key = hashKey.getElement().getAnnotation(DynoDaoIndexHashKey.class);
        return new HashSet<>(Arrays.asList(key.gsiNames()));
    }

    private Set<String> getRangeKeyIndexNames(DynamoAttribute rangeKey) {
        DynoDaoIndexRangeKey key = rangeKey.getElement().getAnnotation(DynoDaoIndexRangeKey.class);
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
                        .hashKey(hashKey)
                        .rangeKey(rangeKey)
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
