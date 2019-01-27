package org.lemon.dynodao.processor.node.generate;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.node.NodeClassData;

import javax.inject.Inject;
import javax.lang.model.element.Modifier;
import java.util.Objects;

import static java.util.stream.Collectors.joining;

/**
 * Adds a decent implementation of {@link Object#equals(Object)} to the type, delegating
 * to {@link Objects#equals(Object, Object)}, field by field.
 */
class EqualsNodeTypeSpecMutator implements NodeTypeSpecMutator {

    private static final ParameterSpec OBJECT_PARAM = ParameterSpec.builder(Object.class, "obj").build();

    private static final MethodSpec EQUALS_WITH_NO_BODY = MethodSpec.methodBuilder("equals")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(boolean.class)
            .addParameter(OBJECT_PARAM)
            .build();

    @Inject EqualsNodeTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, NodeClassData node) {
        // build the incomplete class in order to get the name, the builder provides no access
        String className = typeSpec.build().name;
        MethodSpec equals = buildEquals(className, node);
        typeSpec.addMethod(equals);
    }

    private MethodSpec buildEquals(String className, NodeClassData node) {
        if (node.getAttributes().isEmpty()) {
            return buildNoFieldsEquals(className);
        } else {
            return buildPojoEquals(className, node);
        }
    }

    private MethodSpec buildNoFieldsEquals(String className) {
        return EQUALS_WITH_NO_BODY.toBuilder()
                .addStatement("return this == $N || $N instanceof $L", OBJECT_PARAM, OBJECT_PARAM, className)
                .build();
    }

    private MethodSpec buildPojoEquals(String className, NodeClassData node) {
        MethodSpec.Builder equals = EQUALS_WITH_NO_BODY.toBuilder()
                .beginControlFlow("if (this == $N)", OBJECT_PARAM)
                .addStatement("return true")
                .nextControlFlow("else if ($N instanceof $L)", OBJECT_PARAM, className)
                .addStatement("$L rhs = ($L) $N", className, className, OBJECT_PARAM);

        String equal = node.getAttributesAsFields().stream()
                .map(field -> String.format("$T.equals(this.%s, rhs.%s)", field.name, field.name))
                .collect(joining(" && "));
        Object[] objects = node.getAttributes().stream().map(f -> Objects.class).toArray();

        return equals
                .addStatement("return " + equal, objects)
                .nextControlFlow("else")
                .addStatement("return false")
                .endControlFlow()
                .build();
    }

}
