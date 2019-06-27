package com.github.dynodao.processor.stage.generate;

import com.github.dynodao.processor.stage.Stage;
import com.squareup.javapoet.TypeSpec;

import javax.inject.Inject;

import static com.github.dynodao.processor.util.DynoDaoUtil.generatedAnnotation;

/**
 * Adds the {@link javax.annotation.Generated} annotation to the type.
 */
class GeneratedAnnotationStageTypeSpecMutator implements StageTypeSpecMutator {

    @Inject GeneratedAnnotationStageTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, Stage stage) {
        typeSpec.addAnnotation(generatedAnnotation());
    }

}
