package org.dynodao.processor.schema.parse;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import org.dynodao.processor.context.Processors;
import org.dynodao.processor.schema.SchemaContext;
import org.dynodao.processor.schema.attribute.DynamoAttribute;
import org.dynodao.processor.schema.attribute.ListDynamoAttribute;
import org.dynodao.processor.schema.serialize.DeserializationMappingMethod;
import org.dynodao.processor.schema.serialize.SerializationMappingMethod;

import javax.inject.Inject;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor8;
import java.util.ArrayList;
import java.util.List;

import static org.dynodao.processor.schema.serialize.DeserializationMappingMethod.parameter;
import static org.dynodao.processor.util.DynamoDbUtil.attributeValue;

/**
 * Parses a list. The value type is a single known attribute type, it does not allow for mixing types.
 */
class ListTypeSchemaParser implements SchemaParser {

    private static final TypeName LIST_OF_ATTRIBUTE_VALUE = ParameterizedTypeName.get(ClassName.get(List.class), attributeValue());

    private final Processors processors;

    @Inject ListTypeSchemaParser(Processors processors) {
        this.processors = processors;
    }

    @Override
    public boolean isApplicableTo(Element element, TypeMirror typeMirror, SchemaContext schemaContext) {
        TypeElement list = processors.getTypeElement(List.class);
        TypeMirror wildcard = processors.getWildcardType(null, null);
        TypeMirror listOfAnything = processors.getDeclaredType(list, wildcard);
        return processors.isAssignable(typeMirror, listOfAnything)
                && schemaContext.isApplicableTo(element, getValueType(typeMirror));
    }

    private TypeMirror getValueType(TypeMirror typeMirror) {
        return getListInterface(typeMirror).getTypeArguments().get(0);
    }

    private DeclaredType getListInterface(TypeMirror typeMirror) {
        return (DeclaredType) processors.getSupertypeWithErasureSameAs(typeMirror, processors.getDeclaredType(List.class));
    }

    @Override
    public ListDynamoAttribute parseAttribute(Element element, TypeMirror typeMirror, String path, SchemaContext schemaContext) {
        DynamoAttribute listElement = schemaContext.parseAttribute(element, getValueType(typeMirror), "[]");
        return ListDynamoAttribute.builder()
                .element(element)
                .typeMirror(typeMirror)
                .path(path)
                .listElement(listElement)
                .serializationMethod(buildSerializationMethod(typeMirror, listElement))
                .deserializationMethod(buildDeserializationMethod(typeMirror, listElement))
                .build();
    }

    private SerializationMappingMethod buildSerializationMethod(TypeMirror typeMirror, DynamoAttribute listElement) {
        ParameterSpec list = ParameterSpec.builder(TypeName.get(typeMirror), "list").build();

        String valueSerializationMethod = listElement.getSerializationMethod().getMethodName();
        CodeBlock.Builder body = CodeBlock.builder()
                .addStatement("$T attrValueList = new $T<>(list.size())", LIST_OF_ATTRIBUTE_VALUE, ArrayList.class)
                .beginControlFlow("for ($T element : $N)", listElement.getTypeMirror(), list)
                .addStatement("attrValueList.add($L(element))", valueSerializationMethod)
                .endControlFlow()
                .addStatement("return new $T().withL(attrValueList)", attributeValue());

        return SerializationMappingMethod.builder()
                .methodName(getSerializationMethodName(typeMirror, valueSerializationMethod))
                .parameter(list)
                .coreMethodBody(body.build())
                .build();
    }

    private String getSerializationMethodName(TypeMirror typeMirror, String valueSerializationMethod) {
        return getMethodName(typeMirror, valueSerializationMethod, "serialize");
    }

    private String getMethodName(TypeMirror typeMirror, String valueMethod, String methodType) {
        String listType = processors.asElement(typeMirror).getSimpleName().toString();
        if (hasTypeArguments(typeMirror)) {
            return String.format("%s%sOf%s", methodType, listType, valueMethod.replaceFirst(methodType, ""));
        } else {
            return methodType + listType;
        }
    }

    private boolean hasTypeArguments(TypeMirror typeMirror) {
        return typeMirror.accept(new SimpleTypeVisitor8<Boolean, Void>(false) {
            @Override
            public Boolean visitDeclared(DeclaredType declaredType, Void aVoid) {
                return !declaredType.getTypeArguments().isEmpty();
            }
        }, null);
    }

    private DeserializationMappingMethod buildDeserializationMethod(TypeMirror typeMirror, DynamoAttribute listElement) {
        String valueDeserializationMethod = listElement.getDeserializationMethod().getMethodName();
        CodeBlock.Builder body = CodeBlock.builder()
                .addStatement("$T list = new $T$L()", typeMirror, getListImplementationType(typeMirror), hasTypeArguments(typeMirror) ? "<>" : "")
                .beginControlFlow("for ($T element : $N.getL())", attributeValue(), parameter())
                .addStatement("list.add($L(element))", valueDeserializationMethod)
                .endControlFlow()
                .addStatement("return list");

        return DeserializationMappingMethod.builder()
                .methodName(getDeserializationMethodName(typeMirror, valueDeserializationMethod))
                .returnType(TypeName.get(typeMirror))
                .coreMethodBody(body.build())
                .build();
    }

    private TypeName getListImplementationType(TypeMirror typeMirror) {
        TypeMirror erasure = processors.erasure(typeMirror);
        if (processors.isSameType(processors.getDeclaredType(List.class), erasure)) {
            return TypeName.get(ArrayList.class);
        } else {
            return TypeName.get(erasure);
        }
    }

    private String getDeserializationMethodName(TypeMirror typeMirror, String valueDeserializationMethod) {
        return getMethodName(typeMirror, valueDeserializationMethod, "deserialize");
    }

}
