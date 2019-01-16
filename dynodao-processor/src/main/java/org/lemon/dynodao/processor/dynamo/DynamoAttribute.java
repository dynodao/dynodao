package org.lemon.dynodao.processor.dynamo;

import static org.lemon.dynodao.processor.util.DynamoDbUtil.attributeValue;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;

import lombok.Data;

@Data
public class DynamoAttribute {

    private final String name;
    private final VariableElement field;

    public FieldSpec toFieldSpec() {
        return FieldSpec.builder(TypeName.get(field.asType()), field.getSimpleName().toString())
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();
    }

    public CodeBlock newAttributeValue() {
        return CodeBlock.builder()
                .addStatement("return new $T.$L($N)", attributeValue(), "withS", toFieldSpec())
                .build();
    }

}
