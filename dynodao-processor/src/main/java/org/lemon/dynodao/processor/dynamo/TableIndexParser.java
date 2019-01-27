package org.lemon.dynodao.processor.dynamo;

import org.lemon.dynodao.annotation.DynoDaoHashKey;
import org.lemon.dynodao.annotation.DynoDaoRangeKey;
import org.lemon.dynodao.annotation.DynoDaoSchema;
import org.lemon.dynodao.processor.context.ProcessorMessager;

import javax.inject.Inject;
import javax.lang.model.element.TypeElement;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Collections.singleton;

/**
 * Extracts the overall table "index" from the schema document.
 */
class TableIndexParser implements DynamoIndexParser {

    private final ProcessorMessager processorMessager;
    private final SchemaFieldsProvider schemaFieldsProvider;

    @Inject TableIndexParser(ProcessorMessager processorMessager, SchemaFieldsProvider schemaFieldsProvider) {
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
            if (attribute.getField().getAnnotation(DynoDaoRangeKey.class) != null) {
                rangeKeys.add(attribute);
            }
            attributes.add(attribute);
        }

        validate(document, hashKeys, rangeKeys);

        return singleton(toIndex(hashKeys, rangeKeys, attributes));
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
        if (rangeKeys.size() > 1) {
            processorMessager.submitError("@%s must exist on at most one attribute, but found %s", DynoDaoRangeKey.class.getSimpleName(), rangeKeys);
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
