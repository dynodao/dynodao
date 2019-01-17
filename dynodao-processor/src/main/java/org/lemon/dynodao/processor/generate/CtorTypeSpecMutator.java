package org.lemon.dynodao.processor.generate;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.dynamo.DynamoAttribute;
import org.lemon.dynodao.processor.model.PojoClassBuilder;

import javax.inject.Inject;

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
        return !pojo.getAttributes().isEmpty();
    }

    private MethodSpec buildCtor(PojoClassBuilder pojo) {
        MethodSpec.Builder ctor = MethodSpec.constructorBuilder();
        for (DynamoAttribute attribute : pojo.getAttributes()) {
            FieldSpec field = attribute.asFieldSpec();
            ParameterSpec param = attribute.asParameterSpec();
            ctor
                    .addParameter(param)
                    .addStatement("this.$N = $N", field, param);
        }
        return ctor.build();
    }

}
