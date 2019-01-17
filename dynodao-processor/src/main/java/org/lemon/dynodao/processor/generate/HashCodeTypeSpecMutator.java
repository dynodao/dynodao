package org.lemon.dynodao.processor.generate;

import static org.lemon.dynodao.processor.util.StreamUtil.concat;
import static org.lemon.dynodao.processor.util.StringUtil.repeat;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.model.PojoClassBuilder;

import javax.inject.Inject;
import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.Objects;

/**
 * Adds a decent implementation of {@link Object#hashCode()}} to the type, delegating to {@link Objects#hash(Object...)}.
 */
class HashCodeTypeSpecMutator implements TypeSpecMutator {

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
        List<FieldSpec> fields = pojo.getAttributesAsFields();
        String hashCodeParams = repeat(fields.size(), "$N", ", ");
        Object[] args = concat(Objects.class, fields).toArray();
        return hashCodeWithNoBody.toBuilder()
                .addStatement("return $T.hash(" + hashCodeParams + ")", args)
                .build();
    }

}
