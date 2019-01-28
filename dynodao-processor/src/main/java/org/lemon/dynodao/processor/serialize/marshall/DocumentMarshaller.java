package org.lemon.dynodao.processor.serialize.marshall;

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
import org.lemon.dynodao.processor.serialize.MarshallMethod;
import org.lemon.dynodao.processor.serialize.SerializationContext;

import javax.inject.Inject;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.attributeValue;
import static org.lemon.dynodao.processor.util.StringUtil.capitalize;

/**
 * Handles serialization of objects annotated with either {@link org.lemon.dynodao.annotation.DynoDaoSchema}
 * or {@link org.lemon.dynodao.annotation.DynoDaoDocument}.
 * FIXME this implementation requires knowledge of the dynodao annotations present, it should be possible to model those instead of accessing them here
 */
class DocumentMarshaller implements AttributeValueMarshaller {

    private static final TypeName MAP_OF_ATTRIBUTE_VALUE = ParameterizedTypeName.get(ClassName.get(Map.class), TypeName.get(String.class), attributeValue());

    private final Processors processors;

    @Inject DocumentMarshaller(Processors processors) {
        this.processors = processors;
    }

    @Override
    public boolean isApplicableTo(TypeMirror type) {
        if (type.getKind().equals(TypeKind.DECLARED)) {
            TypeElement element = (TypeElement) processors.asElement(type);
            return element.getAnnotation(DynoDaoSchema.class) != null || element.getAnnotation(DynoDaoDocument.class) != null;
        } else {
            return false;
        }
    }

    @Override
    public Collection<? extends TypeMirror> getTypeDependencies(TypeMirror type) {
        return getFieldsOf((TypeElement) processors.asElement(type)).stream()
                .map(Element::asType)
                .collect(toList());
    }

    private List<Element> getFieldsOf(TypeElement typeElement) {
        return typeElement.getEnclosedElements().stream()
                .filter(element -> element.getKind().equals(ElementKind.FIELD))
                .filter(element -> element.getAnnotation(DynoDaoIgnore.class) == null)
                .collect(toList());
    }

    @Override
    public MarshallMethod serialize(TypeMirror type, SerializationContext serializationContext) {
        return serialize((DeclaredType) type, serializationContext);
    }

    private MarshallMethod serialize(DeclaredType type, SerializationContext serializationContext) {
        ParameterSpec param = getParameter(type);
        CodeBlock body = getBody(type, param, serializationContext);
        return MarshallMethod.builder()
                .methodName(getMethodName(type))
                .parameter(param)
                .body(body)
                .build();
    }

    private ParameterSpec getParameter(DeclaredType type) {
        return ParameterSpec.builder(TypeName.get(type), "document").build();
    }

    private String getMethodName(DeclaredType type) {
        return "serialize" + processors.asElement(type).getSimpleName();
    }

    private CodeBlock getBody(DeclaredType type, ParameterSpec param, SerializationContext serializationContext) {
        CodeBlock.Builder body = CodeBlock.builder()
                .addStatement("$T attrValueMap = new $T<>()", MAP_OF_ATTRIBUTE_VALUE, HashMap.class);

        for (Element field : getFieldsOf((TypeElement) processors.asElement(type))) {
            MarshallMethod method = serializationContext.getMarshallMethodForType(field.asType());
            body.addStatement("attrValueMap.put($S, $L($N.$L))", attributeName(field), method.getMethodName(), param, accessField(field));
        }

        return body
                .addStatement("return new $T().withM(attrValueMap)", attributeValue())
                .build();
    }

    private String attributeName(Element field) {
        DynoDaoAttribute attribute = field.getAnnotation(DynoDaoAttribute.class);
        if (attribute != null && !attribute.attributeName().isEmpty()) {
            return attribute.attributeName();
        } else {
            return field.getSimpleName().toString();
        }
    }

    /**
     * TODO validate the method exists
     */
    private String accessField(Element field) {
        if (isPackageAccessible(field)) {
            return field.getSimpleName().toString();
        } else if (processors.isSameType(processors.getPrimitiveType(TypeKind.BOOLEAN), field.asType())) {
            return "is" + capitalize(field) + "()";
        } else {
            return "get" + capitalize(field) + "()";
        }
    }

    private boolean isPackageAccessible(Element element) {
        return !element.getModifiers().contains(Modifier.PRIVATE);
    }

}