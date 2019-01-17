package org.lemon.dynodao.processor.generate;

import static java.util.stream.Collectors.joining;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.context.ProcessorContext;
import org.lemon.dynodao.processor.model.PojoClassBuilder;

import javax.inject.Inject;
import javax.lang.model.element.Modifier;
import java.util.Objects;

/**
 * Adds a decent implementation of {@link Object#equals(Object)} to the type, delegating
 * to {@link Objects#equals(Object, Object)}, field by field.
 */
class EqualsTypeSpecMutator implements TypeSpecMutator {

    @Inject ProcessorContext processorContext;

    private MethodSpec equalsWithNoBody;
    private ParameterSpec objectParam;

    @Inject EqualsTypeSpecMutator() { }

    @Inject void init() {
        objectParam = ParameterSpec.builder(Object.class, "obj").build();
        equalsWithNoBody = MethodSpec.methodBuilder("equals")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(boolean.class)
                .addParameter(objectParam)
                .build();
    }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, PojoClassBuilder pojo) {
        // build the incomplete class in order to get the name, the builder provides no access
        String className = typeSpec.build().name;
        MethodSpec equals = buildEquals(className, pojo);
        typeSpec.addMethod(equals);
    }

    private MethodSpec buildEquals(String className, PojoClassBuilder pojo) {
        if (pojo.getAttributes().isEmpty()) {
            return buildNoFieldsEquals(className);
        } else {
            return buildPojoEquals(className, pojo);
        }
    }

    private MethodSpec buildNoFieldsEquals(String className) {
        return equalsWithNoBody.toBuilder()
                .addStatement("return this == $N || $N instanceof $L", objectParam, objectParam, className)
                .build();
    }

    private MethodSpec buildPojoEquals(String className, PojoClassBuilder pojo) {
        MethodSpec.Builder equals = equalsWithNoBody.toBuilder()
                .beginControlFlow("if (this == $N)", objectParam)
                .addStatement("return true")
                .nextControlFlow("else if ($N instanceof $L)", objectParam, className)
                .addStatement("$L rhs = ($L) $N", className, className, objectParam);

        String equal = pojo.getAttributesAsFields().stream()
                .map(field -> String.format("$T.equals(this.%s, rhs.%s)", field.name, field.name))
                .collect(joining(" && "));
        Object[] objects = pojo.getAttributes().stream().map(f -> Objects.class).toArray();

        return equals
                .addStatement("return " + equal, objects)
                .nextControlFlow("else")
                .addStatement("return false")
                .endControlFlow()
                .build();
    }

}
