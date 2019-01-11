package org.lemon.dynodao.processor.generate.method;

import javax.lang.model.element.Modifier;

import org.lemon.dynodao.processor.context.ProcessorContext;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

class EqualsAndHashCodeTypeGenerator implements TypeGenerator {

    @Override
    public void build(TypeSpec.Builder pojo, ProcessorContext processorContext) {
    }

    private MethodSpec equals() {
        ParameterSpec obj = ParameterSpec.builder(Object.class, "obj").build();

        MethodSpec.Builder equals =  MethodSpec.methodBuilder("equals")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(boolean.class)
                .addParameter(obj);

        return equals.build();
    }

}
