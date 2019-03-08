package org.lemon.dynodao.processor.stage.generate;

import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.stage.Stage;

import javax.inject.Inject;

import static org.lemon.dynodao.processor.util.DynoDaoUtil.generatedAnnotation;

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
