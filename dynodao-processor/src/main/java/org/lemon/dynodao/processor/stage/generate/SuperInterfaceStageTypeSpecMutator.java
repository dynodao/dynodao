package org.lemon.dynodao.processor.stage.generate;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.stage.Stage;

import javax.inject.Inject;

/**
 * Adds the appropriate implementing interfaces to the type. These include, but are not limited to
 * {@link org.lemon.dynodao.DynoDaoLoad}, {@link org.lemon.dynodao.DynoDaoQuery} or {@link org.lemon.dynodao.DynoDaoCreateTable}.
 */
class SuperInterfaceStageTypeSpecMutator implements StageTypeSpecMutator {

    @Inject SuperInterfaceStageTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, Stage stage) {
        stage.getInterfaceType().getInterfaceClass().ifPresent(interfaceClass -> {
            TypeName superType = ParameterizedTypeName.get(ClassName.get(interfaceClass), TypeName.get(stage.getSchema().getDocument().getTypeMirror()));
            typeSpec.addSuperinterface(superType);
        });
    }

}
