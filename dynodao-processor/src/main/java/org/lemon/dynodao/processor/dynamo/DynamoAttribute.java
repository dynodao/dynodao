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

    /**
     * @return this attribute as a field to be placed into a pojo class
     */
    public FieldSpec toFieldSpec() {
        return FieldSpec.builder(TypeName.get(field.asType()), field.getSimpleName().toString())
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();
    }

    /**
     * Returns this attribute as a new {@link com.amazonaws.services.dynamodbv2.model.AttributeValue} block
     * of code. The field name is assumed to be the same field name as the field returned by {@link #toFieldSpec()}.
     * The block is standalone, no semicolon or statement is included, it is similar to
     * <tt>new AttributeValue().withS(hashKey)</tt>.
     * @return the block of code that initializes this as an {@link com.amazonaws.services.dynamodbv2.model.AttributeValue}
     */
    public CodeBlock toNewAttributeValue() {
        return CodeBlock.of("new $T.$L($N)", attributeValue(), "withS", toFieldSpec());
    }

}
