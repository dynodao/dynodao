package org.lemon.dynodao.processor.node.generate;

import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.node.NodeClassData;

import javax.inject.Inject;

/**
 * Adds the fields from the model model to the type. They are added verbatim.
 */
class FieldsNodeTypeSpecMutator implements NodeTypeSpecMutator {

    @Inject FieldsNodeTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, NodeClassData node) {
        typeSpec.addFields(node.getAttributesAsFields());
    }

}
