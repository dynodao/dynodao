package org.lemon.dynodao.processor.schema.parse;

import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
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
import org.lemon.dynodao.processor.schema.serialize.MappingMethod;
import org.lemon.dynodao.processor.schema.serialize.MappingMethodImpl;
import org.lemon.dynodao.processor.schema.serialize.SerializationMappingMethod;

import javax.inject.Inject;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.LinkedHashMap;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.lemon.dynodao.processor.schema.serialize.DeserializationMappingMethod.parameter;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.attributeValue;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.item;
import static org.lemon.dynodao.processor.util.StringUtil.capitalize;

/**
 * Parses a type which is annotated with {@link DynoDaoSchema} or {@link DynoDaoDocument}.
 */
class DocumentSchemaParser implements SchemaParser {

    private static final ParameterSpec ITEM_PARAMETER = ParameterSpec.builder(item(), "item").build();

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
        MappingMethod itemSerializationMethod = buildItemSerializationMethod(typeElement, typeMirror, nestedAttributes);
        MappingMethod itemDeserializationMethod = buildItemDeserializationMethod(typeElement, typeMirror, nestedAttributes);

        return DocumentDynamoAttribute.builder()
                .element(element)
                .typeMirror(typeMirror)
                .path(path)
                .attributes(nestedAttributes)
                .itemSerializationMethod(itemSerializationMethod)
                .serializationMethod(buildSerializationMethod(itemSerializationMethod))
                .itemDeserializationMethod(itemDeserializationMethod)
                .deserializationMethod(buildDeserializationMethod(itemDeserializationMethod))
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

    private MappingMethod buildItemSerializationMethod(Element typeElement, TypeMirror typeMirror, List<DynamoAttribute> attributes) {
        ParameterSpec document = ParameterSpec.builder(TypeName.get(typeMirror), "document").build();

        CodeBlock.Builder body = CodeBlock.builder()
                .addStatement("$T item = new $T<>()", item(), LinkedHashMap.class);

        for (DynamoAttribute attribute : attributes) {
            body.addStatement("item.put($S, $L($N.$L()))", attribute.getPath(),
                    attribute.getSerializationMethod().getMethodName(), document, accessorOf(attribute.getElement()));
        }
        body.addStatement("return item");
        return MappingMethodImpl.builder()
                .methodName("serialize" + typeElement.getSimpleName() + "AsItem")
                .returnType(item())
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

    private SerializationMappingMethod buildSerializationMethod(MappingMethod itemSerializationMethod) {
        return SerializationMappingMethod.builder()
                .methodName(itemSerializationMethod.getMethodName().replaceAll("AsItem$", ""))
                .parameter(itemSerializationMethod.getParameter())
                .coreMethodBody(CodeBlock.builder()
                        .addStatement("return new $T().withM($L($N))", attributeValue(), itemSerializationMethod.getMethodName(),
                                itemSerializationMethod.getParameter())
                        .build())
                .build();
    }

    private MappingMethod buildItemDeserializationMethod(Element typeElement, TypeMirror typeMirror, List<DynamoAttribute> attributes) {
        CodeBlock.Builder body = CodeBlock.builder()
                .addStatement("$T document = new $T()", typeMirror, typeMirror);

        for (DynamoAttribute attribute : attributes) {
            body.addStatement("document.$L($L($N.get($S)))", mutatorOf(attribute.getElement()),
                    attribute.getDeserializationMethod().getMethodName(), ITEM_PARAMETER, attribute.getPath());
        }
        body.addStatement("return document");

        return MappingMethodImpl.builder()
                .methodName("deserialize" + typeElement.getSimpleName() + "FromItem")
                .returnType(TypeName.get(typeMirror))
                .parameter(ITEM_PARAMETER)
                .coreMethodBody(body.build())
                .build();
    }

    private String mutatorOf(Element field) {
        return "set" + capitalize(field);
    }

    private DeserializationMappingMethod buildDeserializationMethod(MappingMethod itemDeserializationMethod) {
        return DeserializationMappingMethod.builder()
                .methodName(itemDeserializationMethod.getMethodName().replaceAll("FromItem$", ""))
                .returnType(itemDeserializationMethod.getReturnType())
                .coreMethodBody(CodeBlock.builder()
                        .addStatement("return $L($N.getM())", itemDeserializationMethod.getMethodName(), parameter())
                        .build())
                .build();
    }

}
