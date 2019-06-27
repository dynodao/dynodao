package com.github.dynodao.processor.stage.generate;

import com.github.dynodao.processor.stage.Stage;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import javax.inject.Inject;
import javax.lang.model.element.Modifier;
import java.util.Objects;

import static java.util.stream.Collectors.joining;

/**
 * Adds a decent implementation of {@link Object#equals(Object)} to the type, delegating
 * to {@link Objects#equals(Object, Object)}, field by field.
 */
class EqualsStageTypeSpecMutator implements StageTypeSpecMutator {

    private static final ParameterSpec OBJECT_PARAM = ParameterSpec.builder(Object.class, "obj").build();

    private static final MethodSpec EQUALS_WITH_NO_BODY = MethodSpec.methodBuilder("equals")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(boolean.class)
            .addParameter(OBJECT_PARAM)
            .build();

    @Inject EqualsStageTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, Stage stage) {
        // build the incomplete class in order to get the name, the builder provides no access
        String className = typeSpec.build().name;
        MethodSpec equals = buildEquals(className, stage);
        typeSpec.addMethod(equals);
    }

    private MethodSpec buildEquals(String className, Stage stage) {
        if (stage.getAttributes().isEmpty()) {
            return buildNoFieldsEquals(className);
        } else {
            return buildPojoEquals(className, stage);
        }
    }

    private MethodSpec buildNoFieldsEquals(String className) {
        return EQUALS_WITH_NO_BODY.toBuilder()
                .addStatement("return this == $N || $N instanceof $L", OBJECT_PARAM, OBJECT_PARAM, className)
                .build();
    }

    private MethodSpec buildPojoEquals(String className, Stage stage) {
        MethodSpec.Builder equals = EQUALS_WITH_NO_BODY.toBuilder()
                .beginControlFlow("if (this == $N)", OBJECT_PARAM)
                .addStatement("return true")
                .nextControlFlow("else if ($N instanceof $L)", OBJECT_PARAM, className)
                .addStatement("$L rhs = ($L) $N", className, className, OBJECT_PARAM);

        String equal = stage.getAttributesAsFields().stream()
                .map(field -> String.format("$T.equals(this.%s, rhs.%s)", field.name, field.name))
                .collect(joining(" && "));
        Object[] objects = stage.getAttributes().stream().map(f -> Objects.class).toArray();

        return equals
                .addStatement("return " + equal, objects)
                .nextControlFlow("else")
                .addStatement("return false")
                .endControlFlow()
                .build();
    }

}
