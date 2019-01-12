package org.lemon.dynodao.processor.generate.type;

import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.model.PojoClassBuilder;

import javax.inject.Inject;

class FieldTypeSpecMutator implements TypeSpecMutator {

    @Inject FieldTypeSpecMutator() { }

    @Override
    public void build(TypeSpec.Builder typeSpec, PojoClassBuilder pojo) {
        typeSpec.addFields(pojo.getFields());
    }

}
