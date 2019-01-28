package org.lemon.dynodao.processor.serialize.generate;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.serialize.MarshallMethod;
import org.lemon.dynodao.processor.serialize.SerializerClassData;

import javax.inject.Inject;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.Arrays;

import static java.util.Collections.singletonList;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.attributeValue;

/**
 * Adds all of the methods which serialize to {@link com.amazonaws.services.dynamodbv2.model.AttributeValue}.
 */
class MarshallMethodsSerializerTypeSpecMutator implements SerializerTypeSpecMutator {

    private static final FieldSpec NULL_ATTRIBUTE_VALUE = FieldSpec.builder(attributeValue(), "NULL_ATTRIBUTE_VALUE", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
            .initializer("new $T().withNULL(true)", attributeValue())
            .build();

    @Inject MarshallMethodsSerializerTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, SerializerClassData serializerClassData) {
        typeSpec.addField(NULL_ATTRIBUTE_VALUE);

        Iterable<Modifier> modifiers = getModifiers(serializerClassData.getDocument());
        for (MarshallMethod method : serializerClassData.getAllMarshallMethods()) {
            MethodSpec marshallMethod = toMethodSpec(method, modifiers);
            typeSpec.addMethod(marshallMethod);
        }
    }

    private Iterable<Modifier> getModifiers(TypeElement document) {
        if (document.getModifiers().contains(Modifier.PUBLIC)) {
            return Arrays.asList(Modifier.PUBLIC, Modifier.STATIC);
        } else {
            return singletonList(Modifier.STATIC);
        }
    }

    private MethodSpec toMethodSpec(MarshallMethod method, Iterable<Modifier> modifiers) {
        ParameterSpec parameter = method.getParameter();
        MethodSpec.Builder serialize = MethodSpec.methodBuilder(method.getMethodName())
                .addJavadoc("Serializes <tt>$N</tt> as an {@link $T}\n", parameter, attributeValue())
                .addJavadoc("@param $N the value to serialize\n", parameter)
                .addJavadoc("@return the serialization of <tt>$N</tt>\n", parameter)
                .addModifiers(modifiers)
                .returns(attributeValue())
                .addParameter(parameter);

        if (!parameter.type.isPrimitive()) {
            serialize
                    .beginControlFlow("if ($N == null)", parameter)
                    .addStatement("return $N", NULL_ATTRIBUTE_VALUE)
                    .endControlFlow();
        }

        return serialize
                .addCode(method.getBody())
                .build();
    }

}
