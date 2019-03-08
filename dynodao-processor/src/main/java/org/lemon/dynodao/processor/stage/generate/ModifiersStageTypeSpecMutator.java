package org.lemon.dynodao.processor.stage.generate;

import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.stage.Stage;

import javax.inject.Inject;
import javax.lang.model.element.Modifier;

/**
 * Adds the appropriate modifiers to the stage type. Stage types are always final, and public
 * if the document type is also public.
 */
class ModifiersStageTypeSpecMutator implements StageTypeSpecMutator {

    @Inject ModifiersStageTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, Stage stage) {
        if (stage.getDocumentElement().getModifiers().contains(Modifier.PUBLIC)) {
            typeSpec.addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        } else {
            typeSpec.addModifiers(Modifier.FINAL);
        }
    }

}
