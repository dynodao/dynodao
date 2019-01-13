package org.lemon.dynodao.processor.generate;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.model.PojoClassBuilder;

import javax.inject.Inject;

/**
 * Adds the appropriate implementing interfaces to the type. These include {@link org.lemon.dynodao.DocumentLoad}
 * and {@link org.lemon.dynodao.DocumentQuery}.
 */
class SuperInterfaceTypeSpecMutator implements TypeSpecMutator {

    @Inject SuperInterfaceTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, PojoClassBuilder pojo) {
        pojo.getInterfaceType().getInterfaceClass().ifPresent(clazz -> {
            TypeName sup = ParameterizedTypeName.get(ClassName.get(clazz), TypeName.get(pojo.getDocument().asType()));
            typeSpec.addSuperinterface(sup);
        });
    }

}
