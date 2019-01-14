package org.lemon.dynodao.processor.generate;

import static java.util.stream.Collectors.toList;
import static org.lemon.dynodao.processor.util.StreamUtil.concat;
import static org.lemon.dynodao.processor.util.StringUtil.repeat;
import static org.lemon.dynodao.processor.util.StringUtil.toClassCase;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.lang.model.element.Modifier;

import org.lemon.dynodao.processor.model.PojoClassBuilder;
import org.lemon.dynodao.processor.model.PojoTypeSpec;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

/**
 * Adds an againster to type being built. The againster is a factory which forwards the parameters to the construct
 * of a new type, which is returned.
 */
class AgainsterTypeSpecMutator implements TypeSpecMutator {

    @Inject AgainsterTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, PojoClassBuilder pojo) {
        for (PojoTypeSpec targetAgainster : pojo.getTargetAgainstIndexes()) {
            MethodSpec wither = buildAgainster(pojo, targetAgainster);
            typeSpec.addMethod(wither);
        }
    }

    private MethodSpec buildAgainster(PojoClassBuilder pojo, PojoTypeSpec targetAgainster) {
        List<ParameterSpec> params = getRequiredParameters(pojo, targetAgainster);
        ClassName type = ClassName.bestGuess(targetAgainster.getTypeSpec().name);

        String argsFormat = repeat(pojo.getFields().size() + params.size(), "$N", ", ");
        Object[] args = concat(type, pojo.getFields(), params).toArray();

        return MethodSpec.methodBuilder(getMethodName(targetAgainster))
                .addModifiers(Modifier.PUBLIC)
                .returns(type)
                .addParameters(params)
                .addStatement("return new $T(" + argsFormat + ")", args)
                .build();
    }

    private List<ParameterSpec> getRequiredParameters(PojoClassBuilder pojo, PojoTypeSpec targetAgainster) {
        List<FieldSpec> fields = new ArrayList<>(targetAgainster.getTypeSpec().fieldSpecs);
        fields.removeAll(pojo.getFields());
        return fields.stream()
                .map(field -> ParameterSpec.builder(field.type, field.name).build())
                .collect(toList());
    }

    private String getMethodName(PojoTypeSpec targetAgainster) {
        return "against" + toClassCase(targetAgainster.getPojo().getDynamoIndex().getName());
    }

}
