package org.dynodao.processor.schema.index;

import org.dynodao.annotation.DynoDaoHashKey;
import org.dynodao.annotation.DynoDaoIndexRangeKey;
import org.dynodao.annotation.DynoDaoSchema;
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

/**
 * Extracts local secondary indexes from the schema document.
 */
class LocalSecondaryIndexParser implements DynamoIndexParser {

    private final ProcessorMessager processorMessager;

    @Inject LocalSecondaryIndexParser(ProcessorMessager processorMessager) {
        this.processorMessager = processorMessager;
    }

    @Override
    public Set<DynamoIndex> getIndexesFrom(DocumentDynamoAttribute document) {
        Set<DynamoAttribute> hashKeys = new LinkedHashSet<>();
        Set<DynamoAttribute> rangeKeys = new LinkedHashSet<>();
        Set<DynamoAttribute> attributes = new LinkedHashSet<>();

        for (DynamoAttribute attribute : document.getAttributes()) {
            if (attribute.getElement().getAnnotation(DynoDaoHashKey.class) != null) {
                hashKeys.add(attribute);
            }
            if (attribute.getElement().getAnnotation(DynoDaoIndexRangeKey.class) != null) {
                rangeKeys.add(attribute);
            }
            attributes.add(attribute);
        }

        validate(document, hashKeys, rangeKeys);

        return toIndexes(hashKeys, rangeKeys, attributes);
    }

    private void validate(DocumentDynamoAttribute document, Set<DynamoAttribute> hashKeys, Set<DynamoAttribute> rangeKeys) {
        if (hashKeys.size() != 1) {
            if (hashKeys.isEmpty()) {
                processorMessager.submitError("@%s must exist on exactly one scalar attribute, but none found.", DynoDaoHashKey.class.getSimpleName())
                        .atElement(document.getElement())
                        .atAnnotation(DynoDaoSchema.class);
            }
            hashKeys.forEach(hashKey -> processorMessager.submitError("@%s must exist on exactly one attribute.", DynoDaoHashKey.class.getSimpleName())
                    .atElement(hashKey.getElement())
                    .atAnnotation(DynoDaoHashKey.class));
        }

        Map<String, Set<DynamoAttribute>> rangeKeyByIndex = new HashMap<>();
        for (DynamoAttribute rangeKey : rangeKeys) {
            getIndexNames(rangeKey).forEach(index -> rangeKeyByIndex.computeIfAbsent(index, k -> new HashSet<>()).add(rangeKey));
        }
        rangeKeyByIndex.forEach((index, keys) -> {
            if (keys.size() != 1) {
                processorMessager.submitError("@%s must exist on exactly one field for LSI[%s], but found %s", DynoDaoIndexRangeKey.class.getSimpleName(), index, keys);
            }
        });
    }

    private Set<String> getIndexNames(DynamoAttribute rangeKey) {
        DynoDaoIndexRangeKey key = rangeKey.getElement().getAnnotation(DynoDaoIndexRangeKey.class);
        return new HashSet<>(Arrays.asList(key.lsiNames()));
    }

    private Set<DynamoIndex> toIndexes(Set<DynamoAttribute> hashKeys, Set<DynamoAttribute> rangeKeys, Set<DynamoAttribute> attributes) {
        Set<DynamoIndex> indexes = new LinkedHashSet<>();
        for (DynamoAttribute rangeKey : rangeKeys) {
            for (String indexName : getIndexNames(rangeKey)) {
                indexes.add(DynamoIndex.builder()
                        .indexType(IndexType.LOCAL_SECONDARY_INDEX)
                        .name(indexName)
                        .hashKey(hashKeys.iterator().next())
                        .rangeKey(Optional.of(rangeKey))
                        .projectedAttributes(attributes)
                        .build());
            }
        }
        return indexes;
    }

}
