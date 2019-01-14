package org.lemon.dynodao.processor.generate;

import javax.inject.Inject;

import org.lemon.dynodao.processor.model.PojoClassBuilder;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

/**
 * Adds an all args constructor the type being built. If the type has no fields, nothing is added.
 */
class CtorTypeSpecMutator implements TypeSpecMutator {

    @Inject CtorTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, PojoClassBuilder pojo) {
        if (needsCtor(pojo)) {
            MethodSpec ctor = buildCtor(pojo);
            typeSpec.addMethod(ctor);
        }
    }

    private boolean needsCtor(PojoClassBuilder pojo) {
        return !pojo.getFields().isEmpty();
    }

    private MethodSpec buildCtor(PojoClassBuilder pojo) {
        MethodSpec.Builder ctor = MethodSpec.constructorBuilder();
        for (FieldSpec field : pojo.getFields()) {
            ParameterSpec param = ParameterSpec.builder(field.type, field.name).build();
            ctor
                    .addParameter(param)
                    .addStatement("this.$N = $N", field, param);
        }
        return ctor.build();
    }

}
