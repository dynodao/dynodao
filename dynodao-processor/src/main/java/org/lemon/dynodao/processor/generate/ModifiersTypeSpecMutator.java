package org.lemon.dynodao.processor.generate;

import javax.inject.Inject;
import javax.lang.model.element.Modifier;

import org.lemon.dynodao.processor.model.PojoClassBuilder;

import com.squareup.javapoet.TypeSpec;

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
