package org.lemon.dynodao.processor.stage.generate;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.stage.Stage;
import org.lemon.dynodao.processor.schema.attribute.DynamoAttribute;

import javax.inject.Inject;

/**
 * Adds an all args constructor the type being built. If the type has no fields, nothing is added.
 */
class CtorStageTypeSpecMutator implements StageTypeSpecMutator {

    @Inject CtorStageTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, Stage stage) {
        if (needsCtor(stage)) {
            MethodSpec ctor = buildCtor(stage);
            typeSpec.addMethod(ctor);
        }
    }

    private boolean needsCtor(Stage stage) {
        return !stage.getAttributes().isEmpty();
    }

    private MethodSpec buildCtor(Stage stage) {
        MethodSpec.Builder ctor = MethodSpec.constructorBuilder();
        for (DynamoAttribute attribute : stage.getAttributes()) {
            FieldSpec field = attribute.asFieldSpec();
            ParameterSpec param = attribute.asParameterSpec();
            ctor
                    .addParameter(param)
                    .addStatement("this.$N = $N", field, param);
        }
        return ctor.build();
    }

}
