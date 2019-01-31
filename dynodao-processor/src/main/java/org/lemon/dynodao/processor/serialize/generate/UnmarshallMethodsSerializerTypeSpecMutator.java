package org.lemon.dynodao.processor.serialize.generate;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec.Builder;
import org.lemon.dynodao.processor.serialize.SerializerClassData;
import org.lemon.dynodao.processor.serialize.UnmarshallMethod;

import javax.inject.Inject;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.Arrays;

import static java.util.Collections.singletonList;

/**
 * Adds all of the methods which deserialize from {@link com.amazonaws.services.dynamodbv2.model.AttributeValue}.
 */
class UnmarshallMethodsSerializerTypeSpecMutator implements SerializerTypeSpecMutator {

    @Inject UnmarshallMethodsSerializerTypeSpecMutator() { }

    @Override
    public void mutate(Builder typeSpec, SerializerClassData serializerClassData) {
        Iterable<Modifier> modifiers = getModifiers(serializerClassData.getDocument());
        for (UnmarshallMethod method : serializerClassData.getAllUnmarshallMethods()) {
            MethodSpec unmarshallMethod = toMethodSpec(method, modifiers);
            typeSpec.addMethod(unmarshallMethod);
        }
    }

    private Iterable<Modifier> getModifiers(TypeElement document) {
        if (document.getModifiers().contains(Modifier.PUBLIC)) {
            return Arrays.asList(Modifier.PUBLIC, Modifier.STATIC);
        } else {
            return singletonList(Modifier.STATIC);
        }
    }

    private MethodSpec toMethodSpec(UnmarshallMethod method, Iterable<Modifier> modifiers) {
        ParameterSpec parameter = method.getParameter();
        return MethodSpec.methodBuilder(method.getMethodName())
                .addJavadoc("Deserializes <tt>$N</tt> into {@link $T}\n", parameter, method.getReturnType())
                .addJavadoc("@param $N the value to deserialize\n", parameter)
                .addJavadoc("@return the deserialization of <tt>$N</tt>\n", parameter)
                .addModifiers(modifiers)
                .returns(method.getReturnType())
                .addParameter(parameter)
                .beginControlFlow("if ($1N == null || $1N.get$2L() == null || $3T.TRUE.equals($1N.getNULL()))", parameter, method.getExpectedPresentAttribute(), Boolean.class)
                .addStatement("return $L", getDefaultLiteralForType(method.getReturnType()))
                .endControlFlow()
                .addCode(method.getBody())
                .build();
    }

    private String getDefaultLiteralForType(TypeName type) {
        if (!type.isPrimitive()) {
            return "null";
        } else if (TypeName.BOOLEAN.equals(type)) {
            return "false";
        } else if (TypeName.CHAR.equals(type)) {
            return "'\0'";
        } else {
            return "0";
        }
    }

}
