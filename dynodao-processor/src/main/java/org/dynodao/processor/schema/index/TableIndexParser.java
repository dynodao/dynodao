package org.dynodao.processor.schema.index;

import org.dynodao.annotation.DynoDaoHashKey;
import org.dynodao.annotation.DynoDaoRangeKey;
import org.dynodao.annotation.DynoDaoSchema;
import org.dynodao.processor.context.ProcessorMessager;
import org.dynodao.processor.schema.attribute.DocumentDynamoAttribute;
import org.dynodao.processor.schema.attribute.DynamoAttribute;

import javax.inject.Inject;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Collections.singleton;

/**
 * Extracts the overall table "index" from the schema document.
 */
class TableIndexParser implements DynamoIndexParser {

    private final ProcessorMessager processorMessager;

    @Inject TableIndexParser(ProcessorMessager processorMessager) {
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
            if (attribute.getElement().getAnnotation(DynoDaoRangeKey.class) != null) {
                rangeKeys.add(attribute);
            }
            attributes.add(attribute);
        }

        validate(document, hashKeys, rangeKeys);

        return singleton(toIndex(hashKeys, rangeKeys, attributes));
    }

    /**
     * TODO validate scalar hash and range keys
     */
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
        if (rangeKeys.size() > 1) {
            processorMessager.submitError("@%s must exist on at most one attribute, but found %s", DynoDaoRangeKey.class.getSimpleName(), rangeKeys);
        }
    }

    private DynamoIndex toIndex(Set<DynamoAttribute> hashKeys, Set<DynamoAttribute> rangeKeys, Set<DynamoAttribute> attributes) {
        return DynamoIndex.builder()
                .indexType(IndexType.TABLE)
                .name("<table>")
                .hashKey(hashKeys.iterator().next())
                .rangeKey(rangeKeys.stream().findAny())
                .projectedAttributes(attributes)
                .build();
    }

}
