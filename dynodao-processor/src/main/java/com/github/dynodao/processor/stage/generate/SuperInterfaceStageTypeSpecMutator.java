package com.github.dynodao.processor.stage.generate;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.github.dynodao.DynoDaoCreateTable;
import com.github.dynodao.DynoDaoLoad;
import com.github.dynodao.DynoDaoQuery;
import com.github.dynodao.processor.stage.InterfaceType;
import com.github.dynodao.processor.stage.Stage;

import javax.inject.Inject;

/**
 * Adds the appropriate implementing interfaces to the type. These include, but are not limited to
 * {@link DynoDaoLoad}, {@link DynoDaoQuery} or {@link DynoDaoCreateTable}.
 */
class SuperInterfaceStageTypeSpecMutator implements StageTypeSpecMutator {

    @Inject SuperInterfaceStageTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, Stage stage) {
        stage.getInterfaceTypes().stream()
                .map(InterfaceType::getInterfaceClass)
                .forEach(interfaceClass -> {
                    TypeName superType = ParameterizedTypeName.get(ClassName.get(interfaceClass), TypeName.get(stage.getSchema().getDocument().getTypeMirror()));
                    typeSpec.addSuperinterface(superType);
                });
    }

}
