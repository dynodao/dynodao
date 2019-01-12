package org.lemon.dynodao.processor.generate.type;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.model.PojoClassBuilder;

import javax.inject.Inject;

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
