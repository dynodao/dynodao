package org.lemon.dynodao.processor.schema.parse;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import org.lemon.dynodao.processor.context.Processors;
import org.lemon.dynodao.processor.schema.SchemaContext;
import org.lemon.dynodao.processor.schema.attribute.DynamoAttribute;
import org.lemon.dynodao.processor.schema.attribute.MapDynamoAttribute;
import org.lemon.dynodao.processor.schema.serialize.DeserializationMappingMethod;
import org.lemon.dynodao.processor.schema.serialize.SerializationMappingMethod;

import javax.inject.Inject;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import static org.lemon.dynodao.processor.schema.serialize.DeserializationMappingMethod.parameter;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.attributeValue;

/**
 * Parses a map of some type keyed by string. The value type is a single known type, while the keys
 * are not known.
 */
class MapTypeSchemaParser implements SchemaParser {

    private static final TypeName MAP_OF_ATTRIBUTE_VALUE = ParameterizedTypeName.get(ClassName.get(Map.class), TypeName.get(String.class), attributeValue());
    private static final TypeName MAP_ENTRY_OF_ATTRIBUTE_VALUE = ParameterizedTypeName.get(ClassName.get(Map.Entry.class), TypeName.get(String.class), attributeValue());
    private static final TypeName MAP_OF_ATTRIBUTE_VALUE_ITERATOR = ParameterizedTypeName.get(ClassName.get(Iterator.class), MAP_ENTRY_OF_ATTRIBUTE_VALUE);

    private final Processors processors;

    @Inject MapTypeSchemaParser(Processors processors) {
        this.processors = processors;
    }

    @Override
    public boolean isApplicableTo(Element element, TypeMirror typeMirror, SchemaContext schemaContext) {
        TypeElement map = processors.getTypeElement(Map.class);
        TypeMirror string = processors.getDeclaredType(String.class);
        TypeMirror wildcard = processors.getWildcardType(null, null);
        TypeMirror mapOfStringToAnything = processors.getDeclaredType(map, string, wildcard);
        return processors.isAssignable(typeMirror, mapOfStringToAnything)
                && schemaContext.isApplicableTo(element, getValueType(typeMirror), schemaContext);
    }

    private TypeMirror getValueType(TypeMirror typeMirror) {
        return ((DeclaredType) typeMirror).getTypeArguments().get(1);
    }

    @Override
    public MapDynamoAttribute parseAttribute(Element element, TypeMirror typeMirror, String path, SchemaContext schemaContext) {
        DynamoAttribute mapElement = schemaContext.parseAttribute(element, getValueType(typeMirror), ".", schemaContext);
        return MapDynamoAttribute.builder()
                .element(element)
                .typeMirror(typeMirror)
                .path(path)
                .mapElement(mapElement)
                .serializationMethod(buildSerializationMethod(typeMirror, mapElement))
                .deserializationMethod(buildDeserializationMethod(typeMirror, mapElement))
                .build();
    }

    private SerializationMappingMethod buildSerializationMethod(TypeMirror typeMirror, DynamoAttribute mapElement) {
        ParameterSpec map = ParameterSpec.builder(TypeName.get(typeMirror), "map").build();

        String valueSerializationMethod = mapElement.getSerializationMethod().getMethodName();
        CodeBlock.Builder body = CodeBlock.builder()
                .addStatement("$T attrValueMap = new $T<>()", MAP_OF_ATTRIBUTE_VALUE, LinkedHashMap.class)
                .addStatement("$T it = $N.entrySet().iterator()", getIteratorOf(typeMirror), map)
                .beginControlFlow("while (it.hasNext())")
                .addStatement("$T entry = it.next()", getMapEntryOf(typeMirror))
                .addStatement("attrValueMap.put(entry.getKey(), $L(entry.getValue()))", valueSerializationMethod)
                .endControlFlow()
                .addStatement("return new $T().withM(attrValueMap)", attributeValue());

        String mapType = processors.asElement(typeMirror).getSimpleName().toString();
        return SerializationMappingMethod.builder()
                .methodName(String.format("serialize%sOf%s", mapType, valueSerializationMethod.replaceFirst("serialize", "")))
                .parameter(map)
                .coreMethodBody(body.build())
                .build();
    }

    private TypeName getIteratorOf(TypeMirror typeMirror) {
        return ParameterizedTypeName.get(ClassName.get(Iterator.class), getMapEntryOf(typeMirror));
    }

    private TypeName getMapEntryOf(TypeMirror typeMirror) {
        return ParameterizedTypeName.get(ClassName.get(Map.Entry.class), ((DeclaredType) typeMirror).getTypeArguments().stream()
                .map(TypeName::get)
                .toArray(TypeName[]::new));
    }

    private DeserializationMappingMethod buildDeserializationMethod(TypeMirror typeMirror, DynamoAttribute mapElement) {
        String valueDeserializationMethod = mapElement.getDeserializationMethod().getMethodName();
        CodeBlock.Builder body = CodeBlock.builder()
                .addStatement("$T map = new $T<>()", typeMirror, getMapImplementationType(typeMirror))
                .addStatement("$T it = $N.getM().entrySet().iterator()", MAP_OF_ATTRIBUTE_VALUE_ITERATOR, parameter())
                .beginControlFlow("while (it.hasNext())")
                .addStatement("$T entry = it.next()", MAP_ENTRY_OF_ATTRIBUTE_VALUE)
                .addStatement("map.put(entry.getKey(), $L(entry.getValue()))", valueDeserializationMethod)
                .endControlFlow()
                .addStatement("return map");

        String mapType = processors.asElement(typeMirror).getSimpleName().toString();
        return DeserializationMappingMethod.builder()
                .methodName(String.format("deserialize%sOf%s", mapType, valueDeserializationMethod.replaceFirst("deserialize", "")))
                .returnType(TypeName.get(typeMirror))
                .coreMethodBody(body.build())
                .build();
    }

    private Class<? extends Map> getMapImplementationType(TypeMirror typeMirror) {
        TypeMirror erasure = processors.erasure(typeMirror);
        if (processors.isAssignable(processors.getDeclaredType(LinkedHashMap.class), erasure)) {
            return LinkedHashMap.class;
        } else if (processors.isAssignable(processors.getDeclaredType(TreeMap.class), erasure)) {
            return TreeMap.class;
        } else {
            return HashMap.class;
        }
    }

}
