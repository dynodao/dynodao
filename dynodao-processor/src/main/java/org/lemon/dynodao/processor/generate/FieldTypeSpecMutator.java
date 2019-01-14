package org.lemon.dynodao.processor.generate;

import javax.inject.Inject;

import org.lemon.dynodao.processor.model.PojoClassBuilder;

import com.squareup.javapoet.TypeSpec;

/**
 * Adds the fields from the pojo model to the type. They are added verbatim.
 */
class FieldTypeSpecMutator implements TypeSpecMutator {

    @Inject FieldTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, PojoClassBuilder pojo) {
        typeSpec.addFields(pojo.getFields());
    }

}
