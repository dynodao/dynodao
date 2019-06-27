package com.github.dynodao.processor.serialize.generate;

import com.github.dynodao.processor.schema.DynamoSchema;
import com.github.dynodao.processor.schema.attribute.DocumentDynamoAttribute;
import com.github.dynodao.processor.schema.attribute.DynamoAttribute;
import com.github.dynodao.processor.schema.serialize.MappingMethod;
import com.github.dynodao.processor.util.SimpleDynamoAttributeVisitor;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import javax.inject.Inject;
import javax.lang.model.element.Modifier;
import java.util.Arrays;

import static java.util.Collections.singletonList;

/**
 * Adds the item deserialization method for each {@link DocumentDynamoAttribute} within the schema.
 */
class ItemDeserializationMethodSerializerTypeSpecMutator implements SerializerTypeSpecMutator {

    @Inject ItemDeserializationMethodSerializerTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, DynamoSchema schema) {
        Iterable<Modifier> modifiers = getModifiers(schema.getDocument());

        for (DynamoAttribute attribute : schema.getDocument().getNestedAttributesRecursively()) {
            attribute.accept(new SimpleDynamoAttributeVisitor<Void, Void>() {
                @Override
                public Void visitDocument(DocumentDynamoAttribute document, Void aVoid) {
                    typeSpec.addMethod(toMethodSpec(document.getItemDeserializationMethod(), modifiers));
                    return super.visitDocument(document, aVoid);
                }
            });
        }
    }

    private Iterable<Modifier> getModifiers(DocumentDynamoAttribute document) {
        if (document.getElement().getModifiers().contains(Modifier.PUBLIC)) {
            return Arrays.asList(Modifier.PUBLIC, Modifier.STATIC);
        } else {
            return singletonList(Modifier.STATIC);
        }
    }

    private MethodSpec toMethodSpec(MappingMethod method, Iterable<Modifier> modifiers) {
        ParameterSpec parameter = method.getParameter();
        return MethodSpec.methodBuilder(method.getMethodName())
                .addJavadoc("Deserializes <tt>$N</tt> into {@link $T}.\n", parameter, method.getReturnType())
                .addJavadoc("@param $N the value to deserialize\n", parameter)
                .addJavadoc("@return the deserialization of <tt>$N</tt>\n", parameter)
                .addModifiers(modifiers)
                .returns(method.getReturnType())
                .addParameter(parameter)
                .beginControlFlow("if ($N == null)", parameter)
                .addStatement("return null")
                .endControlFlow()
                .addCode(method.getCoreMethodBody())
                .build();
    }

}
