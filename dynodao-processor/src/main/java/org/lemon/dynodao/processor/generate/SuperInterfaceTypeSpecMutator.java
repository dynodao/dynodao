package org.lemon.dynodao.processor.generate;

import javax.inject.Inject;

import org.lemon.dynodao.processor.model.PojoClassBuilder;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

/**
 * Adds the appropriate implementing interfaces to the type. These include {@link org.lemon.dynodao.DocumentLoad}
 * and {@link org.lemon.dynodao.DocumentQuery}.
 */
class SuperInterfaceTypeSpecMutator implements TypeSpecMutator {

    @Inject SuperInterfaceTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, PojoClassBuilder pojo) {
        pojo.getInterfaceType().getInterfaceClass().ifPresent(interfaceClass -> {
            TypeName sup = ParameterizedTypeName.get(ClassName.get(interfaceClass), TypeName.get(pojo.getDocument().asType()));
            typeSpec.addSuperinterface(sup);
        });
    }

}
