package org.lemon.dynodao.processor.index;

import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import javax.lang.model.element.TypeElement;

import org.lemon.dynodao.processor.context.ProcessorContext;

/**
 * Parses a dynamo document class and extracts all of the indexes that the table has.
 */
public class DynamoIndexParser {

    @Inject DynamoIndexParser() { }

    @Inject ProcessorContext processorContext;

    /**
     * Returns all of the dynamo indexes in the document object.
     * @param document the document to get indexes from
     */
    public Set<DynamoIndex> getIndexes(TypeElement document) {
        DynamoIndexAnnotatedFields fields = getKeyFields(document);

        Set<DynamoIndex> indexes = new HashSet<>();
        indexes.add(fields.getTable());
        indexes.addAll(fields.getLocalSecondaryIndexes());
        indexes.addAll(fields.getGlobalSecondaryIndexes());

        return indexes;
    }

    private DynamoIndexAnnotatedFields getKeyFields(TypeElement document) {
        DynamoIndexAnnotatedFields fields = new DynamoIndexAnnotatedFields();
        document.getEnclosedElements().forEach(fields::append);
        return fields;
    }

}
