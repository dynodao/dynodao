package org.lemon.dynodao.processor.node.generate;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.node.NodeClassData;

import javax.inject.Inject;

/**
 * Adds the appropriate implementing interfaces to the type. These include {@link org.lemon.dynodao.DynoDaoLoad}
 * and {@link org.lemon.dynodao.DynoDaoQuery}.
 */
class SuperInterfaceNodeTypeSpecMutator implements NodeTypeSpecMutator {

    @Inject SuperInterfaceNodeTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, NodeClassData node) {
        node.getInterfaceType().getInterfaceClass().ifPresent(interfaceClass -> {
            TypeName superType = ParameterizedTypeName.get(ClassName.get(interfaceClass), TypeName.get(node.getSchema().getDocument().getTypeMirror()));
            typeSpec.addSuperinterface(superType);
        });
    }

}
