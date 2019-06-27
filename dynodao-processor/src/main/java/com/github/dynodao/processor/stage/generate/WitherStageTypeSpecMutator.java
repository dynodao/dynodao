package com.github.dynodao.processor.stage.generate;

import com.github.dynodao.processor.schema.attribute.DynamoAttribute;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import com.github.dynodao.processor.stage.Stage;
import com.github.dynodao.processor.stage.StageTypeSpec;

import javax.inject.Inject;
import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static com.github.dynodao.processor.util.StreamUtil.concat;
import static com.github.dynodao.processor.util.StringUtil.capitalize;
import static com.github.dynodao.processor.util.StringUtil.repeat;

/**
 * Adds a wither to type being built. The wither is a factory which forwards the fields in this type
 * plus wither method arguments to the construct of a new type, which is returned.
 */
class WitherStageTypeSpecMutator implements StageTypeSpecMutator {

    @Inject WitherStageTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, Stage stage) {
        for (StageTypeSpec witherTarget : stage.getTargetWithers()) {
            MethodSpec wither = buildWither(stage, witherTarget);
            typeSpec.addMethod(wither);
        }
    }

    private MethodSpec buildWither(Stage stage, StageTypeSpec witherTarget) {
        List<ParameterSpec> params = getRequiredParameters(stage, witherTarget);
        ClassName type = ClassName.bestGuess(witherTarget.getTypeSpec().name);

        String argsFormat = repeat(stage.getAttributes().size() + params.size(), "$N", ", ");
        Object[] args = concat(type, stage.getAttributesAsFields(), params).toArray();

        return MethodSpec.methodBuilder(getMethodName(params))
                .addModifiers(Modifier.PUBLIC)
                .returns(type)
                .addParameters(params)
                .addStatement("return new $T(" + argsFormat + ")", args)
                .build();
    }

    private List<ParameterSpec> getRequiredParameters(Stage stage, StageTypeSpec witherTarget) {
        List<DynamoAttribute> attributes = new ArrayList<>(witherTarget.getStage().getAttributes());
        attributes.removeAll(stage.getAttributes());
        return attributes.stream()
                .map(DynamoAttribute::asParameterSpec)
                .collect(toList());
    }

    private String getMethodName(List<ParameterSpec> params) {
        return "with" + params.stream().map(param -> capitalize(param.name)).collect(joining());
    }

}
