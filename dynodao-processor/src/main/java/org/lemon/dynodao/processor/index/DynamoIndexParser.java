package org.lemon.dynodao.processor.index;

import java.lang.annotation.Annotation;
import java.util.Set;
import javax.inject.Inject;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import com.google.common.collect.Multimap;
import org.lemon.dynodao.processor.context.ProcessorContext;
import org.omg.DynamicAny.DynAny;

/**
 * Parses a dynamo document class and extracts all of the indexes that the table has.
 */
public class DynamoIndexParser {

    @Inject DynamoIndexParser() { }

    @Inject ProcessorContext processorContext;

    /**
     *
     * @param document
     */
    public void getIndexes(TypeElement document) {
        DynamoIndexAnnotatedFields fields = getKeyFields(document);

        DynamoIndex table = fields.getTable();
        Set<DynamoIndex> lsi = fields.getLocalSecondaryIndexes();

        lsi.add(table);

        lsi.forEach(i -> processorContext.submitErrorMessage("%s", i));
    }

    private DynamoIndexAnnotatedFields getKeyFields(TypeElement document) {
        DynamoIndexAnnotatedFields fields = new DynamoIndexAnnotatedFields();
        document.getEnclosedElements().forEach(fields::append);
        return fields;
    }

}
