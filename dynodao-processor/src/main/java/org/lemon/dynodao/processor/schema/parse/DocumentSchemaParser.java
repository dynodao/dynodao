package org.lemon.dynodao.processor.schema.parse;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import org.lemon.dynodao.annotation.DynoDaoAttribute;
import org.lemon.dynodao.annotation.DynoDaoDocument;
import org.lemon.dynodao.annotation.DynoDaoIgnore;
import org.lemon.dynodao.annotation.DynoDaoSchema;
import org.lemon.dynodao.processor.context.Processors;
import org.lemon.dynodao.processor.schema.SchemaContext;
import org.lemon.dynodao.processor.schema.attribute.DocumentDynamoAttribute;
import org.lemon.dynodao.processor.schema.attribute.DynamoAttribute;
import org.lemon.dynodao.processor.schema.serialize.DeserializationMappingMethod;
import org.lemon.dynodao.processor.schema.serialize.SerializationMappingMethod;

import javax.inject.Inject;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.lemon.dynodao.processor.schema.serialize.DeserializationMappingMethod.parameter;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.attributeValue;
import static org.lemon.dynodao.processor.util.StringUtil.capitalize;

/**
 * Parses a type which is annotated with {@link DynoDaoSchema} or {@link DynoDaoDocument}.
 */
class DocumentSchemaParser implements SchemaParser {

    private static final TypeName MAP_OF_ATTRIBUTE_VALUE = ParameterizedTypeName.get(ClassName.get(Map.class), TypeName.get(String.class), attributeValue());

    private final Processors processors;

    @Inject DocumentSchemaParser(Processors processors) {
        this.processors = processors;
    }

    @Override
    public boolean isApplicableTo(Element element, TypeMirror typeMirror, SchemaContext schemaContext) {
        Element typeElement = processors.asElement(typeMirror);
        return isDocument(typeElement) && getFieldsOf(typeElement).stream()
                .allMatch(field -> schemaContext.isApplicableTo(field, field.asType(), schemaContext));
    }

    private boolean isDocument(Element element) {
        return element != null && (element.getAnnotation(DynoDaoSchema.class) != null || element.getAnnotation(DynoDaoDocument.class) != null);
    }

    private List<VariableElement> getFieldsOf(Element element) {
        return element.getEnclosedElements().stream()
                .filter(enclosed -> enclosed.getKind().equals(ElementKind.FIELD))
                .filter(enclosed -> enclosed.getAnnotation(DynoDaoIgnore.class) == null)
                .map(enclosed -> (VariableElement) enclosed)
                .collect(toList());
    }

    @Override
    public DocumentDynamoAttribute parseAttribute(Element element, TypeMirror typeMirror, String path, SchemaContext schemaContext) {
        Element typeElement = processors.asElement(typeMirror);
        List<DynamoAttribute> nestedAttributes = getNestedAttributes(typeElement, schemaContext);
        return DocumentDynamoAttribute.builder()
                .element(element)
                .typeMirror(typeMirror)
                .path(path)
                .attributes(nestedAttributes)
                .serializationMethod(buildSerializationMethod(typeElement, typeMirror, nestedAttributes))
                .deserializationMethod(buildDeserializationMethod(typeElement, typeMirror, nestedAttributes))
                .build();
    }

    private List<DynamoAttribute> getNestedAttributes(Element typeElement, SchemaContext schemaContext) {
        return getFieldsOf(typeElement).stream()
                .map(field -> schemaContext.parseAttribute(field, field.asType(), getPath(field), schemaContext))
                .collect(toList());
    }

    private String getPath(Element element) {
        DynoDaoAttribute attribute = element.getAnnotation(DynoDaoAttribute.class);
        if (attribute != null && !attribute.value().isEmpty()) {
            return attribute.value();
        } else {
            return element.getSimpleName().toString();
        }
    }

    private SerializationMappingMethod buildSerializationMethod(Element typeElement, TypeMirror typeMirror, List<DynamoAttribute> nestedAttributes) {
        ParameterSpec document = ParameterSpec.builder(TypeName.get(typeMirror), "document").build();

        CodeBlock.Builder body = CodeBlock.builder()
                .addStatement("$T attrValueMap = new $T<>()", MAP_OF_ATTRIBUTE_VALUE, HashMap.class);

        for (DynamoAttribute attribute : nestedAttributes) {
            body.addStatement("attrValueMap.put($S, $L($N.$L()))", attribute.getPath(),
                    attribute.getSerializationMethod().getMethodName(), document, accessorOf(attribute.getElement()));
        }
        body.addStatement("return new $T().withM(attrValueMap)", attributeValue());

        return SerializationMappingMethod.builder()
                .methodName("serialize" + typeElement.getSimpleName())
                .parameter(document)
                .coreMethodBody(body.build())
                .build();
    }

    private String accessorOf(Element field) {
        if (processors.isSameType(processors.getPrimitiveType(TypeKind.BOOLEAN), field.asType())) {
            return "is" + capitalize(field);
        } else {
            return "get" + capitalize(field);
        }
    }

    private DeserializationMappingMethod buildDeserializationMethod(Element typeElement, TypeMirror typeMirror, List<DynamoAttribute> nestedAttributes) {
        CodeBlock.Builder body = CodeBlock.builder()
                .addStatement("$T attrValueMap = $N.getM()", MAP_OF_ATTRIBUTE_VALUE, parameter())
                .addStatement("$T document = new $T()", typeMirror, typeMirror);

        for (DynamoAttribute attribute : nestedAttributes) {
            body.addStatement("document.$L($L(attrValueMap.get($S)))", mutatorOf(attribute.getElement()),
                    attribute.getDeserializationMethod().getMethodName(), attribute.getPath());
        }
        body.addStatement("return document");

        return DeserializationMappingMethod.builder()
                .methodName("deserialize" + typeElement.getSimpleName())
                .returnType(TypeName.get(typeMirror))
                .coreMethodBody(body.build())
                .build();
    }

    private String mutatorOf(Element field) {
        return "set" + capitalize(field);
    }

}
