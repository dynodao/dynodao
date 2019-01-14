package org.lemon.dynodao.processor.generate;

import java.util.Iterator;

import javax.inject.Inject;
import javax.lang.model.element.Modifier;

import org.lemon.dynodao.processor.model.PojoClassBuilder;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

/**
 * Adds a decent {@link Object#toString()} to the type. Creates an output similar to
 * <code>"Pojo(field1=field1sToString, field2=field2Bro)"</code>.
 */
class ToStringTypeSpecMutator implements TypeSpecMutator {

    private MethodSpec toStringWithNoBody;

    @Inject ToStringTypeSpecMutator() { }

    @Inject void init() {
        toStringWithNoBody = MethodSpec.methodBuilder("toString")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(String.class)
                .build();
    }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, PojoClassBuilder pojo) {
        // build the incomplete class in order to get the name, the builder provides no access
        String className = typeSpec.build().name;
        MethodSpec toString = buildToString(className, pojo);
        typeSpec.addMethod(toString);
    }

    private MethodSpec buildToString(String className, PojoClassBuilder pojo) {
        if (pojo.getFields().isEmpty()) {
            return buildNoFieldsToString(className);
        } else {
            return buildPojoToString(className, pojo);
        }
    }

    private MethodSpec buildNoFieldsToString(String className) {
        return toStringWithNoBody.toBuilder()
                .addStatement("return $S", className + "()")
                .build();
    }

    private MethodSpec buildPojoToString(String className, PojoClassBuilder pojo) {
        MethodSpec.Builder toString = toStringWithNoBody.toBuilder()
                .addStatement("$T sb = new $T()", StringBuilder.class, StringBuilder.class)
                .addStatement("sb.append($S)", className + "(");

        Iterator<FieldSpec> fields = pojo.getFields().iterator();
        while (fields.hasNext()) {
            FieldSpec field = fields.next();
            if (fields.hasNext()) {
                toString.addStatement("sb.append($N).append($S)", field, ", ");
            } else {
                toString.addStatement("sb.append($N)", field);
            }
        }

        return toString
                .addStatement("sb.append($S)", ")")
                .addStatement("return sb.toString()")
                .build();
    }

}
