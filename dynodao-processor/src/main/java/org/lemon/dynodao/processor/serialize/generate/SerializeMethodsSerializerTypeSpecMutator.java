package org.lemon.dynodao.processor.serialize.generate;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.serialize.SerializeMethod;
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
class SerializeMethodsSerializerTypeSpecMutator implements SerializerTypeSpecMutator {

    @Inject SerializeMethodsSerializerTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, SerializerClassData serializerClassData) {
        Iterable<Modifier> modifiers = getModifiers(serializerClassData.getDocument());
        for (SerializeMethod method : serializerClassData.getAllSerializationMethods()) {
            MethodSpec serializeMethod = toMethodSpec(method, modifiers);
            typeSpec.addMethod(serializeMethod);
        }
    }

    private Iterable<Modifier> getModifiers(TypeElement document) {
        if (document.getModifiers().contains(Modifier.PUBLIC)) {
            return Arrays.asList(Modifier.PUBLIC, Modifier.STATIC);
        } else {
            return singletonList(Modifier.STATIC);
        }
    }

    private MethodSpec toMethodSpec(SerializeMethod method, Iterable<Modifier> modifiers) {
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
                    .addStatement("return null")
                    .endControlFlow();
        }

        return serialize
                .addCode(method.getBody())
                .build();
    }

}
