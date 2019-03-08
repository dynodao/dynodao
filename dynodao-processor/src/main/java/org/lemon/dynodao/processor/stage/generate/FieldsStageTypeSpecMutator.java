package org.lemon.dynodao.processor.stage.generate;

import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.stage.Stage;

import javax.inject.Inject;

/**
 * Adds the fields from the model model to the type. They are added verbatim.
 */
class FieldsStageTypeSpecMutator implements StageTypeSpecMutator {

    @Inject FieldsStageTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, Stage stage) {
        typeSpec.addFields(stage.getAttributesAsFields());
    }

}
