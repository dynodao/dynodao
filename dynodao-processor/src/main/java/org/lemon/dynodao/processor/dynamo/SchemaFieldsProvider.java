package org.lemon.dynodao.processor.dynamo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;

import javax.inject.Inject;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

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
                .filter(element -> element.getAnnotation(DynamoDBIgnore.class) == null)
                .map(element -> (VariableElement) element)
                .map(DynamoAttribute::of)
                .collect(toSet());
    }

}
