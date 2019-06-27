package com.github.dynodao.processor.stage.generate;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.github.dynodao.processor.stage.Stage;

import javax.inject.Inject;
import javax.lang.model.element.Modifier;
import java.util.List;
import java.util.Objects;

import static com.github.dynodao.processor.util.StreamUtil.concat;
import static com.github.dynodao.processor.util.StringUtil.repeat;

/**
 * Adds a decent implementation of {@link Object#hashCode()}} to the type, delegating to {@link Objects#hash(Object...)}
 * passing in all fields.
 */
class HashCodeStageTypeSpecMutator implements StageTypeSpecMutator {

    private static final MethodSpec HASH_CODE_WITH_NO_BODY = MethodSpec.methodBuilder("hashCode")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(int.class)
            .build();

    @Inject HashCodeStageTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, Stage stage) {
        MethodSpec hashCode = buildHashCode(stage);
        typeSpec.addMethod(hashCode);
    }

    private MethodSpec buildHashCode(Stage stage) {
        List<FieldSpec> fields = stage.getAttributesAsFields();
        String hashCodeParams = repeat(fields.size(), "$N", ", ");
        Object[] args = concat(Objects.class, fields).toArray();
        return HASH_CODE_WITH_NO_BODY.toBuilder()
                .addStatement("return $T.hash(" + hashCodeParams + ")", args)
                .build();
    }

}
