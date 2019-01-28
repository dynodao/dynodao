package org.lemon.dynodao.processor.serialize.generate;

import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.serialize.SerializerClassData;

import javax.inject.Inject;
import javax.lang.model.element.Modifier;

/**
 * Adds the appropriate modifiers to the serializer type. Serializers are always final,
 * and public if the document type is public.
 */
class ModifiersSerializerTypeSpecMutator implements SerializerTypeSpecMutator {

    @Inject ModifiersSerializerTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, SerializerClassData serializerClassData) {
        if (serializerClassData.getDocument().getModifiers().contains(Modifier.PUBLIC)) {
            typeSpec.addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        } else {
            typeSpec.addModifiers(Modifier.FINAL);
        }
    }

}