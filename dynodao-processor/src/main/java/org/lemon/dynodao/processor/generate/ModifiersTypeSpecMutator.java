package org.lemon.dynodao.processor.generate;

import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.model.PojoClassBuilder;

import javax.inject.Inject;
import javax.lang.model.element.Modifier;

/**
 * Adds the appropriate modifiers to the type.
 */
class ModifiersTypeSpecMutator implements TypeSpecMutator {

    @Inject ModifiersTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, PojoClassBuilder pojo) {
        typeSpec.addModifiers(Modifier.PUBLIC);
    }

}
