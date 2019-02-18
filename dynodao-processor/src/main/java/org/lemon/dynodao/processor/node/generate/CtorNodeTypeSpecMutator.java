package org.lemon.dynodao.processor.node.generate;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.node.NodeClassData;
import org.lemon.dynodao.processor.schema.attribute.DynamoAttribute;

import javax.inject.Inject;

/**
 * Adds an all args constructor the type being built. If the type has no fields, nothing is added.
 */
class CtorNodeTypeSpecMutator implements NodeTypeSpecMutator {

    @Inject CtorNodeTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, NodeClassData node) {
        if (needsCtor(node)) {
            MethodSpec ctor = buildCtor(node);
            typeSpec.addMethod(ctor);
        }
    }

    private boolean needsCtor(NodeClassData node) {
        return !node.getAttributes().isEmpty();
    }

    private MethodSpec buildCtor(NodeClassData node) {
        MethodSpec.Builder ctor = MethodSpec.constructorBuilder();
        for (DynamoAttribute attribute : node.getAttributes()) {
            FieldSpec field = attribute.asFieldSpec();
            ParameterSpec param = attribute.asParameterSpec();
            ctor
                    .addParameter(param)
                    .addStatement("this.$N = $N", field, param);
        }
        return ctor.build();
    }

}
