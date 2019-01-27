package org.lemon.dynodao.processor.node.generate;

import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.node.NodeClassData;

import javax.inject.Inject;
import javax.lang.model.element.Modifier;

/**
 * Adds the appropriate modifiers to the node type. Node types are always final, and public
 * if the document type is also public.
 */
class ModifiersNodeTypeSpecMutator implements NodeTypeSpecMutator {

    @Inject ModifiersNodeTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, NodeClassData node) {
        if (node.getDocument().getModifiers().contains(Modifier.PUBLIC)) {
            typeSpec.addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        } else {
            typeSpec.addModifiers(Modifier.FINAL);
        }
    }

}
