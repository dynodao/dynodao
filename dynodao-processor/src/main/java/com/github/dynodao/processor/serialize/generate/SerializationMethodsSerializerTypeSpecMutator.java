package com.github.dynodao.processor.serialize.generate;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import com.github.dynodao.processor.schema.DynamoSchema;
import com.github.dynodao.processor.schema.attribute.DocumentDynamoAttribute;
import com.github.dynodao.processor.schema.attribute.DynamoAttribute;
import com.github.dynodao.processor.schema.serialize.MappingMethod;

import javax.inject.Inject;
import javax.lang.model.element.Modifier;
import java.util.Arrays;

import static java.util.Collections.singletonList;
import static com.github.dynodao.processor.util.DynamoDbUtil.attributeValue;

/**
 * Adds all of the methods which serialize to {@link com.amazonaws.services.dynamodbv2.model.AttributeValue}.
 */
class SerializationMethodsSerializerTypeSpecMutator implements SerializerTypeSpecMutator {

    private static final FieldSpec NULL_ATTRIBUTE_VALUE = FieldSpec.builder(attributeValue(), "NULL_ATTRIBUTE_VALUE", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
            .initializer("new $T().withNULL(true)", attributeValue())
            .build();

    @Inject SerializationMethodsSerializerTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, DynamoSchema schema) {
        typeSpec.addField(NULL_ATTRIBUTE_VALUE);

        Iterable<Modifier> modifiers = getModifiers(schema.getDocument());
        schema.getDocument().getNestedAttributesRecursively().stream()
                .map(DynamoAttribute::getSerializationMethod)
                .distinct()
                .map(method -> toMethodSpec(method, modifiers))
                .forEach(typeSpec::addMethod);
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
        MethodSpec.Builder serialize = MethodSpec.methodBuilder(method.getMethodName())
                .addJavadoc("Serializes <tt>$N</tt> as an {@code $T}.\n", parameter, attributeValue())
                .addJavadoc("@param $N the value to serialize\n", parameter)
                .addJavadoc("@return the serialization of <tt>$N</tt>\n", parameter)
                .addModifiers(modifiers)
                .returns(method.getReturnType())
                .addParameter(parameter);

        if (!parameter.type.isPrimitive()) {
            serialize
                    .beginControlFlow("if ($N == null)", parameter)
                    .addStatement("return $N", NULL_ATTRIBUTE_VALUE)
                    .endControlFlow();
        }

        return serialize
                .addCode(method.getCoreMethodBody())
                .build();
    }

}
