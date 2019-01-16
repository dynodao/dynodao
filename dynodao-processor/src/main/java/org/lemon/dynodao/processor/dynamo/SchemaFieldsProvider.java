package org.lemon.dynodao.processor.dynamo;

import static java.util.stream.Collectors.toSet;

import java.util.Set;

import javax.inject.Inject;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;

/**
 * Returns the fields from the document class.
 */
class SchemaFieldsProvider {

    @Inject SchemaFieldsProvider() { }

    /**
     * @param document the schema document to parse
     * @return the relevant dynamo db schema fields from the document
     */
    Set<VariableElement> getDynamoDbFields(TypeElement document) {
        return document.getEnclosedElements().stream()
                .filter(element -> element.getKind().equals(ElementKind.FIELD))
                .filter(element -> element.getAnnotation(DynamoDBIgnore.class) == null)
                .map(element -> (VariableElement) element)
                .collect(toSet());
    }

}
