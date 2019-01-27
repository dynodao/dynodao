package org.lemon.dynodao.processor.dynamo;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

/**
 * Represents a single attribute in a structured dynamo db schema.
 */
@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DynamoAttribute {

    private final String name;
    private final VariableElement field;

    /**
     * Returns a new {@link DynamoAttribute} of the field. The attribute name is pulled of existing annotations
     * on the field, or of the field name if none apply.
     * @param field the field in the schema class
     * @return DynamoAttribute for the field
     */
    static DynamoAttribute of(VariableElement field) {
        String name = field.getAnnotationMirrors().stream()
                .filter(mirror -> mirror.getAnnotationType().asElement().getSimpleName().toString().startsWith("DynamoDB"))
                .flatMap(mirror -> mirror.getElementValues().entrySet().stream())
                .filter(entry -> entry.getKey().getSimpleName().contentEquals("attributeName"))
                .map(entry -> entry.getValue().getValue().toString())
                .findAny().orElse(field.getSimpleName().toString());
        return new DynamoAttribute(name, field);
    }

    /**
     * @return this attribute as a field to be placed into a pojo class
     */
    public FieldSpec asFieldSpec() {
        return FieldSpec.builder(TypeName.get(field.asType()), field.getSimpleName().toString())
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();
    }

    /**
     * @return this attribute as a parameter to a method
     */
    public ParameterSpec asParameterSpec() {
        return ParameterSpec.builder(TypeName.get(field.asType()), field.getSimpleName().toString()).build();
    }

}
