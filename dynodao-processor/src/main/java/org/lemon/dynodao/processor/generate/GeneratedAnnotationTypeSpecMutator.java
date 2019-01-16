package org.lemon.dynodao.processor.generate;

import java.time.ZonedDateTime;

import javax.annotation.Generated;
import javax.inject.Inject;

import org.lemon.dynodao.processor.model.PojoClassBuilder;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.TypeSpec;

/**
 * Adds the {@link Generated} annotation to the type.
 */
class GeneratedAnnotationTypeSpecMutator implements TypeSpecMutator {

    private static final AnnotationSpec GENERATED = AnnotationSpec.builder(Generated.class)
            .addMember("value", "$S", "org.lemon.dynodao.processor")
            .addMember("date", "$S", ZonedDateTime.now())
            .addMember("comments", "$S", "https://github.com/twentylemon/dynodao")
            .build();

    @Inject GeneratedAnnotationTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, PojoClassBuilder pojo) {
        typeSpec.addAnnotation(GENERATED);
    }
}
