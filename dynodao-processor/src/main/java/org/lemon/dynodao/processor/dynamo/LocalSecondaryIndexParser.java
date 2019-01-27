package org.lemon.dynodao.processor.dynamo;

import org.lemon.dynodao.annotation.DynoDaoHashKey;
import org.lemon.dynodao.annotation.DynoDaoIndexRangeKey;
import org.lemon.dynodao.annotation.DynoDaoSchema;
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

/**
 * Extracts local secondary indexes from the schema document.
 */
class LocalSecondaryIndexParser implements DynamoIndexParser {

    private final ProcessorMessager processorMessager;
    private final SchemaFieldsProvider schemaFieldsProvider;

    @Inject LocalSecondaryIndexParser(ProcessorMessager processorMessager, SchemaFieldsProvider schemaFieldsProvider) {
        this.processorMessager = processorMessager;
        this.schemaFieldsProvider = schemaFieldsProvider;
    }

    @Override
    public Set<DynamoIndex> getIndexesFrom(TypeElement document) {
        Set<DynamoAttribute> hashKeys = new LinkedHashSet<>();
        Set<DynamoAttribute> rangeKeys = new LinkedHashSet<>();
        Set<DynamoAttribute> attributes = new LinkedHashSet<>();
        for (DynamoAttribute attribute : schemaFieldsProvider.getDynamoAttributes(document)) {
            if (attribute.getField().getAnnotation(DynoDaoHashKey.class) != null) {
                hashKeys.add(attribute);
            }
            if (attribute.getField().getAnnotation(DynoDaoIndexRangeKey.class) != null) {
                rangeKeys.add(attribute);
            }
            attributes.add(attribute);
        }

        validate(document, hashKeys, rangeKeys);

        return toIndexes(hashKeys, rangeKeys, attributes);
    }

    private void validate(TypeElement document, Set<DynamoAttribute> hashKeys, Set<DynamoAttribute> rangeKeys) {
        if (hashKeys.size() != 1) {
            if (hashKeys.isEmpty()) {
                processorMessager.submitError("@%s must exist on exactly one scalar attribute, but none found.", DynoDaoHashKey.class.getSimpleName())
                        .atElement(document)
                        .atAnnotation(DynoDaoSchema.class);
            }
            hashKeys.forEach(hashKey -> processorMessager.submitError("@%s must exist on exactly one attribute.", DynoDaoHashKey.class.getSimpleName())
                    .atElement(hashKey.getField())
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
        DynoDaoIndexRangeKey key = rangeKey.getField().getAnnotation(DynoDaoIndexRangeKey.class);
        return new HashSet<>(Arrays.asList(key.lsiNames()));
    }

    private Set<DynamoIndex> toIndexes(Set<DynamoAttribute> hashKeys, Set<DynamoAttribute> rangeKeys, Set<DynamoAttribute> attributes) {
        Set<DynamoIndex> indexes = new LinkedHashSet<>();
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
