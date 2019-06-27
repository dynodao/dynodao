package com.github.dynodao.processor.stage.generate;

import com.github.dynodao.processor.schema.attribute.DynamoAttribute;
import com.github.dynodao.processor.stage.Stage;
import com.github.dynodao.processor.stage.StageTypeSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import javax.inject.Inject;
import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;

import static com.github.dynodao.processor.util.StreamUtil.concat;
import static com.github.dynodao.processor.util.StringUtil.repeat;
import static com.github.dynodao.processor.util.StringUtil.toClassCase;
import static java.util.stream.Collectors.toList;

/**
 * Adds an user to type being built. The user (<tt>using*</tt>) is a factory which forwards the parameters to the
 * construct of a new type, which is returned.
 */
class UserStageTypeSpecMutator implements StageTypeSpecMutator {

    @Inject UserStageTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, Stage stage) {
        for (StageTypeSpec usingTarget : stage.getTargetUsingIndexes()) {
            MethodSpec user = buildUser(stage, usingTarget);
            typeSpec.addMethod(user);
        }
    }

    private MethodSpec buildUser(Stage stage, StageTypeSpec usingTarget) {
        List<ParameterSpec> params = getRequiredParameters(stage, usingTarget);
        ClassName type = ClassName.bestGuess(usingTarget.getTypeSpec().name);

        String argsFormat = repeat(stage.getAttributes().size() + params.size(), "$N", ", ");
        Object[] args = concat(type, stage.getAttributes(), params).toArray();

        return MethodSpec.methodBuilder(getMethodName(usingTarget))
                .addModifiers(Modifier.PUBLIC)
                .returns(type)
                .addParameters(params)
                .addStatement("return new $T(" + argsFormat + ")", args)
                .build();
    }

    private List<ParameterSpec> getRequiredParameters(Stage stage, StageTypeSpec usingTarget) {
        List<DynamoAttribute> attributes = new ArrayList<>(usingTarget.getStage().getAttributes());
        attributes.removeAll(stage.getAttributes());
        return attributes.stream()
                .map(DynamoAttribute::asParameterSpec)
                .collect(toList());
    }

    private String getMethodName(StageTypeSpec usingTarget) {
        return "using" + toClassCase(usingTarget.getStage().getDynamoIndex().getName());
    }

}
