package org.lemon.dynodao.processor.index;

import static java.util.stream.Collectors.toSet;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.VariableElement;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.google.common.base.Strings;

import lombok.Data;

/**
 * Builder for parsing a DynamoDB annotated class.
 */
@Data
class DynamoIndexAnnotatedFields {

    private VariableElement hashKey;
    private Optional<VariableElement> rangeKey = Optional.empty();
    private Set<VariableElement> indexHashKeys = new HashSet<>();
    private Set<VariableElement> indexRangeKeys = new HashSet<>();

    /**
     * Adds the element to this field set, if it is applicable.
     * @param element the element to add
     */
    void append(Element element) {
        if (isField(element)) {
            VariableElement field = (VariableElement) element;
            if (isHashKey(field)) {
                setHashKey(field);
            }
            if (isRangeKey(field)) {
                setRangeKey(Optional.of(field));
            }
            if (isIndexHashKey(field)) {
                indexHashKeys.add(field);
            }
            if (isIndexRangeKey(field)) {
                indexRangeKeys.add(field);
            }
        }
    }

    private boolean isField(Element element) {
        return element.getKind().equals( ElementKind.FIELD);
    }

    private boolean isHashKey(VariableElement field) {
        return field.getAnnotation(DynamoDBHashKey.class) != null;
    }

    private boolean isRangeKey(VariableElement field) {
        return field.getAnnotation(DynamoDBRangeKey.class) != null;
    }

    private boolean isIndexHashKey(VariableElement field) {
        return field.getAnnotation(DynamoDBIndexHashKey.class) != null;
    }

    private boolean isIndexRangeKey(VariableElement field) {
        return field.getAnnotation(DynamoDBIndexRangeKey.class) != null;
    }

    /**
     * @return the core dynamo table index
     */
    DynamoIndex getTable() {
        return DynamoIndex.builder()
                .indexType(IndexType.TABLE)
                .name("<TABLE>")
                .hashKey(getHashKey())
                .rangeKey(getRangeKey())
                .build();
    }

    /**
     * @return all of the local secondary indexes on the table
     */
    Set<DynamoIndex> getLocalSecondaryIndexes() {
        Set<DynamoIndex> indexes = new HashSet<>();
        for (VariableElement indexRangeKey : getIndexRangeKeys()) {
            DynamoDBIndexRangeKey annotation = indexRangeKey.getAnnotation(DynamoDBIndexRangeKey.class);
            for (String name : indexNames(annotation.localSecondaryIndexName(), annotation.localSecondaryIndexNames())) {
                indexes.add(DynamoIndex.builder()
                        .indexType(IndexType.LOCAL_SECONDARY_INDEX)
                        .name(name)
                        .hashKey(getHashKey())
                        .rangeKey(Optional.of(indexRangeKey))
                        .build());
            }
        }
        return indexes;
    }

    private Set<String> indexNames(String name, String[] names) {
        return Stream.concat(Stream.of(name), Arrays.stream(names))
                .filter(str -> !Strings.isNullOrEmpty(str))
                .collect(toSet());
    }

    /**
     * @return all of the global secondary indexes on the table
     */
    Set<DynamoIndex> getGlobalSecondaryIndexes() {
        Set<DynamoIndex> indexes = new HashSet<>();
        for (VariableElement indexHashKey : getIndexHashKeys()) {
            DynamoDBIndexHashKey hashAnnotation = indexHashKey.getAnnotation(DynamoDBIndexHashKey.class);
            for (String indexName : indexNames(hashAnnotation.globalSecondaryIndexName(), hashAnnotation.globalSecondaryIndexNames())) {
                Optional<VariableElement> indexRangeKey = getGsiRangeKey(indexName);
                indexes.add(DynamoIndex.builder()
                        .indexType(IndexType.GLOBAL_SECONDARY_INDEX)
                        .name(indexName)
                        .hashKey(indexHashKey)
                        .rangeKey(indexRangeKey)
                        .build());
            }
        }
        return indexes;
    }

    private Optional<VariableElement> getGsiRangeKey(String indexName) {
        for (VariableElement indexRangeKey : getIndexRangeKeys()) {
            DynamoDBIndexRangeKey rangeAnnotation = indexRangeKey.getAnnotation(DynamoDBIndexRangeKey.class);
            boolean sameIndex = indexNames(rangeAnnotation.globalSecondaryIndexName(), rangeAnnotation.globalSecondaryIndexNames()).contains(indexName);
            if (sameIndex) {
                return Optional.of(indexRangeKey);
            }
        }
        return Optional.empty();
    }

}
