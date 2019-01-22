package org.lemon.dynodao.processor.generate;

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

import static java.util.stream.Collectors.toList;
import static org.lemon.dynodao.processor.util.StreamUtil.concat;
import static org.lemon.dynodao.processor.util.StringUtil.repeat;
import static org.lemon.dynodao.processor.util.StringUtil.toClassCase;

/**
 * Adds an user to type being built. The user (<tt>using*</tt>) is a factory which forwards the parameters to the
 * construct of a new type, which is returned.
 */
class UserTypeSpecMutator implements TypeSpecMutator {

    @Inject UserTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, PojoClassBuilder pojo) {
        for (PojoTypeSpec targetUsers : pojo.getTargetUsingIndexes()) {
            MethodSpec user = buildUser(pojo, targetUsers);
            typeSpec.addMethod(user);
        }
    }

    private MethodSpec buildUser(PojoClassBuilder pojo, PojoTypeSpec targetAgainster) {
        List<ParameterSpec> params = getRequiredParameters(pojo, targetAgainster);
        ClassName type = ClassName.bestGuess(targetAgainster.getTypeSpec().name);

        String argsFormat = repeat(pojo.getAttributes().size() + params.size(), "$N", ", ");
        Object[] args = concat(type, pojo.getAttributes(), params).toArray();

        return MethodSpec.methodBuilder(getMethodName(targetAgainster))
                .addModifiers(Modifier.PUBLIC)
                .returns(type)
                .addParameters(params)
                .addStatement("return new $T(" + argsFormat + ")", args)
                .build();
    }

    private List<ParameterSpec> getRequiredParameters(PojoClassBuilder pojo, PojoTypeSpec targetUser) {
        List<DynamoAttribute> attributes = new ArrayList<>(targetUser.getPojo().getAttributes());
        attributes.removeAll(pojo.getAttributes());
        return attributes.stream()
                .map(DynamoAttribute::asParameterSpec)
                .collect(toList());
    }

    private String getMethodName(PojoTypeSpec targetAgainster) {
        return "using" + toClassCase(targetAgainster.getPojo().getDynamoIndex().getName());
    }

}
