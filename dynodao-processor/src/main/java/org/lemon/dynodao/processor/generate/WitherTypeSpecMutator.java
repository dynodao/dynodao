package org.lemon.dynodao.processor.generate;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.lemon.dynodao.processor.util.StreamUtil.concat;
import static org.lemon.dynodao.processor.util.StringUtil.capitalize;
import static org.lemon.dynodao.processor.util.StringUtil.repeat;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.dynamo.DynamoAttribute;
import org.lemon.dynodao.processor.model.PojoClassBuilder;
import org.lemon.dynodao.processor.model.PojoTypeSpec;

import javax.inject.Inject;
import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Adds a wither to type being built. The wither is a factory which forwards the fields in this type
 * plus wither method arguments to the construct of a new type, which is returned.
 */
class WitherTypeSpecMutator implements TypeSpecMutator {

    @Inject WitherTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, PojoClassBuilder pojo) {
        for (PojoTypeSpec targetWither : pojo.getTargetWithers()) {
            MethodSpec wither = buildWither(pojo, targetWither);
            typeSpec.addMethod(wither);
        }
    }

    private MethodSpec buildWither(PojoClassBuilder pojo, PojoTypeSpec targetWither) {
        List<ParameterSpec> params = getRequiredParameters(pojo, targetWither);
        ClassName type = ClassName.bestGuess(targetWither.getTypeSpec().name);

        String argsFormat = repeat(pojo.getAttributes().size() + params.size(), "$N", ", ");
        Object[] args = concat(type, pojo.getAttributesAsFields(), params).toArray();

        return MethodSpec.methodBuilder(getMethodName(params))
                .addModifiers(Modifier.PUBLIC)
                .returns(type)
                .addParameters(params)
                .addStatement("return new $T(" + argsFormat + ")", args)
                .build();
    }

    private List<ParameterSpec> getRequiredParameters(PojoClassBuilder pojo, PojoTypeSpec targetWither) {
        List<DynamoAttribute> attributes = new ArrayList<>(targetWither.getPojo().getAttributes());
        attributes.removeAll(pojo.getAttributes());
        return attributes.stream()
                .map(DynamoAttribute::asParameterSpec)
                .collect(toList());
    }

    private String getMethodName(List<ParameterSpec> params) {
        return "with" + params.stream().map(param -> capitalize(param.name)).collect(joining());
    }

}
