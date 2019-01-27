package org.lemon.dynodao.processor.dynamo;

import org.lemon.dynodao.annotation.DynoDaoIgnore;

import javax.inject.Inject;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.Set;

import static org.lemon.dynodao.processor.util.StreamUtil.toLinkedHashSet;

/**
 * Returns the fields from the document class. This ignores fields that otherwise would not apply.
 */
class SchemaFieldsProvider {

    @Inject SchemaFieldsProvider() { }

    /**
     * @param document the schema document to parse
     * @return the relevant dynamo db schema attributes from the document
     */
    Set<DynamoAttribute> getDynamoAttributes(TypeElement document) {
        return document.getEnclosedElements().stream()
                .filter(element -> element.getKind().equals(ElementKind.FIELD))
                .filter(element -> element.getAnnotation(DynoDaoIgnore.class) == null)
                .map(element -> (VariableElement) element)
                .map(DynamoAttribute::of)
                .collect(toLinkedHashSet());
    }

}
