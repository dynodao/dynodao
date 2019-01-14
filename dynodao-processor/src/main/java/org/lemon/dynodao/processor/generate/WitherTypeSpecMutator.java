package org.lemon.dynodao.processor.generate;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.lemon.dynodao.processor.util.StringUtil.capitalize;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.lang.model.element.Modifier;

import org.lemon.dynodao.processor.model.PojoClassBuilder;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

/**
 * Adds a wither to type being built. The wither is a factory which forwards the fields in this type
 * plus wither method arguments to the construct of a new type, which is returned.
 */
class WitherTypeSpecMutator implements TypeSpecMutator {

    @Inject WitherTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, PojoClassBuilder pojo) {
        for (TypeSpec targetWither : pojo.getTargetWithers()) {
            MethodSpec wither = buildWither(pojo, targetWither);
            typeSpec.addMethod(wither);
        }
    }

    private MethodSpec buildWither(PojoClassBuilder pojo, TypeSpec targetWither) {
        List<ParameterSpec> params = getRequiredParameters(pojo, targetWither);
        ClassName type = ClassName.bestGuess(targetWither.name);

        String argsFormat = Stream.concat(pojo.getFields().stream(), params.stream())
                .map(obj -> "$N")
                .collect(joining(", "));
        Object[] args = Stream.concat(Stream.of(type), Stream.concat(pojo.getFields().stream(), params.stream())).toArray();

        return MethodSpec.methodBuilder(getMethodName(params))
                .addModifiers(Modifier.PUBLIC)
                .returns(type)
                .addParameters(params)
                .addStatement("return new $T(" + argsFormat + ")", args)
                .build();
    }

    private List<ParameterSpec> getRequiredParameters(PojoClassBuilder pojo, TypeSpec targetWither) {
        List<FieldSpec> fields = new ArrayList<>(targetWither.fieldSpecs);
        fields.removeAll(pojo.getFields());
        return fields.stream()
                .map(field -> ParameterSpec.builder(field.type, field.name).build())
                .collect(toList());
    }

    private String getMethodName(List<ParameterSpec> params) {
        return "with" + params.stream().map(param -> capitalize(param.name)).collect(joining());
    }

}
