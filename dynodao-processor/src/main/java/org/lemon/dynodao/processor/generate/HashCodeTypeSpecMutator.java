package org.lemon.dynodao.processor.generate;

import static java.util.stream.Collectors.joining;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.context.ProcessorContext;
import org.lemon.dynodao.processor.model.PojoClassBuilder;

import javax.inject.Inject;
import javax.lang.model.element.Modifier;
import java.util.Objects;
import java.util.stream.Stream;

class HashCodeTypeSpecMutator implements TypeSpecMutator {

    @Inject ProcessorContext processorContext;

    private MethodSpec hashCodeWithNoBody;

    @Inject HashCodeTypeSpecMutator() { }

    @Inject void init() {
        hashCodeWithNoBody = MethodSpec.methodBuilder("hashCode")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(int.class)
                .build();
    }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, PojoClassBuilder pojo) {
        MethodSpec hashCode = buildHashCode(pojo);
        typeSpec.addMethod(hashCode);
    }

    private MethodSpec buildHashCode(PojoClassBuilder pojo) {
        MethodSpec.Builder hashCode = hashCodeWithNoBody.toBuilder();
        String hashCodeParams = pojo.getFields().stream()
                .map(field -> "$N")
                .collect(joining(", "));
        Object[] args = Stream.concat(Stream.of(Objects.class), pojo.getFields().stream()).toArray();
        return hashCode
                .addStatement("return $T.hash(" + hashCodeParams + ")", args)
                .build();
    }

}
