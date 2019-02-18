package org.lemon.dynodao.processor.serialize.generate;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.schema.DynamoSchema;
import org.lemon.dynodao.processor.schema.attribute.DocumentDynamoAttribute;
import org.lemon.dynodao.processor.schema.attribute.DynamoAttribute;
import org.lemon.dynodao.processor.schema.serialize.MappingMethod;

import javax.inject.Inject;
import javax.lang.model.element.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static java.util.Collections.singletonList;

/**
 * Adds all of the methods which deserialize from {@link com.amazonaws.services.dynamodbv2.model.AttributeValue}.
 */
class DeserializationMethodsSerializerTypeSpecMutator implements SerializerTypeSpecMutator {

    @Inject DeserializationMethodsSerializerTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, DynamoSchema schema) {
        Iterable<Modifier> modifiers = getModifiers(schema.getDocument());

        Set<MappingMethod> addedMappings = new HashSet<>();
        schema.getDocument().getNestedAttributesRecursively().stream()
                .filter(attribute -> addedMappings.add(attribute.getDeserializationMethod()))
                .map(attribute -> toMethodSpec(attribute, modifiers))
                .forEach(typeSpec::addMethod);
    }

    private Iterable<Modifier> getModifiers(DocumentDynamoAttribute document) {
        if (document.getElement().getModifiers().contains(Modifier.PUBLIC)) {
            return Arrays.asList(Modifier.PUBLIC, Modifier.STATIC);
        } else {
            return singletonList(Modifier.STATIC);
        }
    }

    private MethodSpec toMethodSpec(DynamoAttribute attribute, Iterable<Modifier> modifiers) {
        MappingMethod method = attribute.getDeserializationMethod();
        ParameterSpec parameter = method.getParameter();
        return MethodSpec.methodBuilder(method.getMethodName())
                .addJavadoc("Deserializes <tt>$N</tt> into {@link $T}\n", parameter, method.getReturnType())
                .addJavadoc("@param $N the value to deserialize\n", parameter)
                .addJavadoc("@return the deserialization of <tt>$N</tt>\n", parameter)
                .addModifiers(modifiers)
                .returns(method.getReturnType())
                .addParameter(parameter)
                .beginControlFlow("if ($1N == null || $1N.get$2L() == null || $3T.TRUE.equals($1N.getNULL()))", parameter, attribute.getAttributeType().getDataTypeName(), Boolean.class)
                .addStatement("return $L", getDefaultLiteralForType(method.getReturnType()))
                .endControlFlow()
                .addCode(method.getCoreMethodBody())
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
