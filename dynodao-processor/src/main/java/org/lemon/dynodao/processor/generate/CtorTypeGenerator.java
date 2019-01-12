package org.lemon.dynodao.processor.generate;

import java.util.List;

import javax.inject.Inject;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;

public class CtorTypeGenerator {

    @Inject CtorTypeGenerator() { }

    /**
     * Creates a package private all args ctor.
     * @param fields the fields that should be set in the ctor
     * @return the ctor method
     */
    public MethodSpec buildAllArgsCtor(List<FieldSpec> fields) {
        MethodSpec.Builder ctor = MethodSpec.constructorBuilder();
        for (FieldSpec field : fields) {
            ParameterSpec param = ParameterSpec.builder(field.type, field.name).build();
            ctor
                    .addParameter(param)
                    .addStatement("this.$N = $N", field, param);
        }
        return ctor.build();
    }

}
