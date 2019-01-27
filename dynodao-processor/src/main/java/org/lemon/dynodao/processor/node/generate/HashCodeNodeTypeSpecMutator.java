package org.lemon.dynodao.processor.node.generate;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.node.NodeClassData;

import javax.inject.Inject;
import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.Objects;

import static org.lemon.dynodao.processor.util.StreamUtil.concat;
import static org.lemon.dynodao.processor.util.StringUtil.repeat;

/**
 * Adds a decent implementation of {@link Object#hashCode()}} to the type, delegating to {@link Objects#hash(Object...)}.
 */
class HashCodeNodeTypeSpecMutator implements NodeTypeSpecMutator {

    private static final MethodSpec HASH_CODE_WITH_NO_BODY = MethodSpec.methodBuilder("hashCode")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(int.class)
            .build();

    @Inject HashCodeNodeTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, NodeClassData node) {
        MethodSpec hashCode = buildHashCode(node);
        typeSpec.addMethod(hashCode);
    }

    private MethodSpec buildHashCode(NodeClassData node) {
        List<FieldSpec> fields = node.getAttributesAsFields();
        String hashCodeParams = repeat(fields.size(), "$N", ", ");
        Object[] args = concat(Objects.class, fields).toArray();
        return HASH_CODE_WITH_NO_BODY.toBuilder()
                .addStatement("return $T.hash(" + hashCodeParams + ")", args)
                .build();
    }

}
