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

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.lemon.dynodao.processor.util.StreamUtil.concat;
import static org.lemon.dynodao.processor.util.StringUtil.capitalize;
import static org.lemon.dynodao.processor.util.StringUtil.repeat;

/**
 * Adds a wither to type being built. The wither is a factory which forwards the fields in this type
 * plus wither method arguments to the construct of a new type, which is returned.
 */
class WitherNodeTypeSpecMutator implements NodeTypeSpecMutator {

    @Inject WitherNodeTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, NodeClassData node) {
        for (NodeTypeSpec witherTarget : node.getTargetWithers()) {
            MethodSpec wither = buildWither(node, witherTarget);
            typeSpec.addMethod(wither);
        }
    }

    private MethodSpec buildWither(NodeClassData node, NodeTypeSpec witherTarget) {
        List<ParameterSpec> params = getRequiredParameters(node, witherTarget);
        ClassName type = ClassName.bestGuess(witherTarget.getTypeSpec().name);

        String argsFormat = repeat(node.getAttributes().size() + params.size(), "$N", ", ");
        Object[] args = concat(type, node.getAttributesAsFields(), params).toArray();

        return MethodSpec.methodBuilder(getMethodName(params))
                .addModifiers(Modifier.PUBLIC)
                .returns(type)
                .addParameters(params)
                .addStatement("return new $T(" + argsFormat + ")", args)
                .build();
    }

    private List<ParameterSpec> getRequiredParameters(NodeClassData node, NodeTypeSpec witherTarget) {
        List<DynamoAttribute> attributes = new ArrayList<>(witherTarget.getNode().getAttributes());
        attributes.removeAll(node.getAttributes());
        return attributes.stream()
                .map(DynamoAttribute::asParameterSpec)
                .collect(toList());
    }

    private String getMethodName(List<ParameterSpec> params) {
        return "with" + params.stream().map(param -> capitalize(param.name)).collect(joining());
    }

}
