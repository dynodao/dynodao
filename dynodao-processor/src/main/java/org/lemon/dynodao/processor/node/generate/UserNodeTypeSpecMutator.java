package org.lemon.dynodao.processor.node.generate;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.dynamo.DynamoAttribute;
import org.lemon.dynodao.processor.node.NodeClassData;
import org.lemon.dynodao.processor.node.NodeTypeSpec;

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
class UserNodeTypeSpecMutator implements NodeTypeSpecMutator {

    @Inject UserNodeTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, NodeClassData node) {
        for (NodeTypeSpec usingTarget : node.getTargetUsingIndexes()) {
            MethodSpec user = buildUser(node, usingTarget);
            typeSpec.addMethod(user);
        }
    }

    private MethodSpec buildUser(NodeClassData node, NodeTypeSpec usingTarget) {
        List<ParameterSpec> params = getRequiredParameters(node, usingTarget);
        ClassName type = ClassName.bestGuess(usingTarget.getTypeSpec().name);

        String argsFormat = repeat(node.getAttributes().size() + params.size(), "$N", ", ");
        Object[] args = concat(type, node.getAttributes(), params).toArray();

        return MethodSpec.methodBuilder(getMethodName(usingTarget))
                .addModifiers(Modifier.PUBLIC)
                .returns(type)
                .addParameters(params)
                .addStatement("return new $T(" + argsFormat + ")", args)
                .build();
    }

    private List<ParameterSpec> getRequiredParameters(NodeClassData node, NodeTypeSpec usingTarget) {
        List<DynamoAttribute> attributes = new ArrayList<>(usingTarget.getNode().getAttributes());
        attributes.removeAll(node.getAttributes());
        return attributes.stream()
                .map(DynamoAttribute::asParameterSpec)
                .collect(toList());
    }

    private String getMethodName(NodeTypeSpec usingTarget) {
        return "using" + toClassCase(usingTarget.getNode().getDynamoIndex().getName());
    }

}
