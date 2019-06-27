package com.github.dynodao.processor.stage.generate;

import com.github.dynodao.processor.schema.attribute.DynamoAttribute;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.github.dynodao.processor.stage.Stage;

import javax.inject.Inject;
import javax.lang.model.element.Modifier;
import java.util.Iterator;

/**
 * Adds a decent {@link Object#toString()} to the type. Creates an output similar to
 * <code>"Stage(field1=field1sToString, field2=field2Bro)"</code>.
 */
class ToStringStageTypeSpecMutator implements StageTypeSpecMutator {

    private static final MethodSpec TO_STRING_WITH_NO_BODY = MethodSpec.methodBuilder("toString")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(String.class)
            .build();

    @Inject ToStringStageTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, Stage stage) {
        // build the incomplete class in order to get the name, the builder provides no access
        String className = typeSpec.build().name;
        MethodSpec toString = buildToString(className, stage);
        typeSpec.addMethod(toString);
    }

    private MethodSpec buildToString(String className, Stage stage) {
        if (stage.getAttributes().isEmpty()) {
            return buildNoFieldsToString(className);
        } else {
            return buildPojoToString(className, stage);
        }
    }

    private MethodSpec buildNoFieldsToString(String className) {
        return TO_STRING_WITH_NO_BODY.toBuilder()
                .addStatement("return $S", className + "()")
                .build();
    }

    private MethodSpec buildPojoToString(String className, Stage stage) {
        MethodSpec.Builder toString = TO_STRING_WITH_NO_BODY.toBuilder()
                .addStatement("$T sb = new $T()", StringBuilder.class, StringBuilder.class)
                .addStatement("sb.append($S)", className + "(");

        Iterator<DynamoAttribute> attributes = stage.getAttributes().iterator();
        while (attributes.hasNext()) {
            FieldSpec field = attributes.next().asFieldSpec();
            if (attributes.hasNext()) {
                toString.addStatement("sb.append($S).append($N).append($S)", field.name + "=", field, ", ");
            } else {
                toString.addStatement("sb.append($S).append($N)", field.name + "=", field);
            }
        }

        return toString
                .addStatement("sb.append($S)", ")")
                .addStatement("return sb.toString()")
                .build();
    }

}
