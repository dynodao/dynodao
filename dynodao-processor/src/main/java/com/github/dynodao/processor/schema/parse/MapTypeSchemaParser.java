package com.github.dynodao.processor.schema.parse;

import com.github.dynodao.processor.context.Processors;
import com.github.dynodao.processor.schema.SchemaContext;
import com.github.dynodao.processor.schema.attribute.DynamoAttribute;
import com.github.dynodao.processor.schema.attribute.MapDynamoAttribute;
import com.github.dynodao.processor.schema.serialize.DeserializationMappingMethod;
import com.github.dynodao.processor.schema.serialize.SerializationMappingMethod;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import javax.inject.Inject;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.SimpleTypeVisitor8;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

import static com.github.dynodao.processor.util.DynamoDbUtil.attributeValue;
import static com.github.dynodao.processor.util.DynamoDbUtil.item;

/**
 * Parses a map of some type keyed by string. The value type is a single known type, while the keys
 * are not known.
 */
class MapTypeSchemaParser implements SchemaParser {

    private static final TypeName ITEM_MAP_ENTRY = ParameterizedTypeName.get(ClassName.get(Map.Entry.class), TypeName.get(String.class), attributeValue());
    private static final TypeName ITEM_MAP_ENTRY_ITERATOR = ParameterizedTypeName.get(ClassName.get(Iterator.class), ITEM_MAP_ENTRY);

    /**
     * The priority ordered list of map implementation types when the target is a map interface, abstract or an intermediary map,
     * like {@code HashMap}. If one is assignable to the type from the schema class, we use it as the implementation type
     * during deserialization. If all fail to be assigned, an instance of the same type as the one from the schema should be
     * used. A downside of this is we'd "successfully" new up an ImmutableMap, or similar. Perhaps a reasonable ask from users.
     * TODO validate the implementation type has a no-args ctor and is non-abstract
     */
    private static final List<Class<? extends Map>> MAP_IMPLEMENTATION_CLASSES = Arrays.asList(
            LinkedHashMap.class, TreeMap.class,
            ConcurrentHashMap.class, ConcurrentSkipListMap.class
    );

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
                && schemaContext.isApplicableTo(element, getValueType(typeMirror));
    }

    private TypeMirror getValueType(TypeMirror typeMirror) {
        return getMapInterface(typeMirror).getTypeArguments().get(1);
    }

    private DeclaredType getMapInterface(TypeMirror typeMirror) {
        return (DeclaredType) processors.getSupertypeWithErasureSameAs(typeMirror, processors.getDeclaredType(Map.class));
    }

    @Override
    public MapDynamoAttribute parseAttribute(Element element, TypeMirror typeMirror, String path, SchemaContext schemaContext) {
        DynamoAttribute mapElement = schemaContext.parseAttribute(element, getValueType(typeMirror), ".");
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
                .addStatement("$T attrValueMap = new $T<>()", item(), LinkedHashMap.class)
                .addStatement("$T it = $N.entrySet().iterator()", getIteratorOf(typeMirror), map)
                .beginControlFlow("while (it.hasNext())")
                .addStatement("$T entry = it.next()", getMapEntryOf(typeMirror))
                .beginControlFlow("if (entry.getKey() != null && entry.getValue() != null)")
                .addStatement("attrValueMap.put(entry.getKey(), $L(entry.getValue()))", valueSerializationMethod)
                .endControlFlow()
                .endControlFlow()
                .addStatement("return new $T().withM(attrValueMap)", attributeValue());

        return SerializationMappingMethod.builder()
                .methodName(getSerializationMethodName(typeMirror, valueSerializationMethod))
                .parameter(map)
                .coreMethodBody(body.build())
                .build();
    }

    private TypeName getIteratorOf(TypeMirror typeMirror) {
        return ParameterizedTypeName.get(ClassName.get(Iterator.class), getMapEntryOf(typeMirror));
    }

    private TypeName getMapEntryOf(TypeMirror typeMirror) {
        return ParameterizedTypeName.get(ClassName.get(Map.Entry.class), TypeName.get(String.class), TypeName.get(getValueType(typeMirror)));
    }

    private String getSerializationMethodName(TypeMirror typeMirror, String valueSerializationMethod) {
        return getMethodName(typeMirror, valueSerializationMethod, "serialize");
    }

    private String getMethodName(TypeMirror typeMirror, String valueMethod, String methodType) {
        String mapType = processors.asElement(typeMirror).getSimpleName().toString();
        if (hasTypeArguments(typeMirror)) {
            return String.format("%s%sOf%s", methodType, mapType, valueMethod.replaceFirst(methodType, ""));
        } else {
            return methodType + mapType;
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

    private DeserializationMappingMethod buildDeserializationMethod(TypeMirror typeMirror, DynamoAttribute mapElement) {
        String valueDeserializationMethod = mapElement.getDeserializationMethod().getMethodName();
        CodeBlock.Builder body = CodeBlock.builder()
                .addStatement("$T map = new $T$L()", typeMirror, getMapImplementationType(typeMirror), hasTypeArguments(typeMirror) ? "<>" : "")
                .addStatement("$T it = $N.getM().entrySet().iterator()", ITEM_MAP_ENTRY_ITERATOR, DeserializationMappingMethod.parameter())
                .beginControlFlow("while (it.hasNext())")
                .addStatement("$T entry = it.next()", ITEM_MAP_ENTRY)
                .addStatement("$T value = $L(entry.getValue())", getValueType(typeMirror), valueDeserializationMethod)
                .beginControlFlow("if (value != null)")
                .addStatement("map.put(entry.getKey(), value)")
                .endControlFlow()
                .endControlFlow()
                .addStatement("return map");

        return DeserializationMappingMethod.builder()
                .methodName(getDeserializationMethodName(typeMirror, valueDeserializationMethod))
                .returnType(TypeName.get(typeMirror))
                .coreMethodBody(body.build())
                .build();
    }

    private TypeName getMapImplementationType(TypeMirror typeMirror) {
        TypeMirror erasure = processors.erasure(typeMirror);
        return MAP_IMPLEMENTATION_CLASSES.stream()
                .map(processors::getDeclaredType)
                .filter(mapImplType -> processors.isAssignable(mapImplType, erasure))
                .map(TypeName::get)
                .findFirst()
                .orElseGet(() -> TypeName.get(erasure));
    }

    private String getDeserializationMethodName(TypeMirror typeMirror, String valueDeserializationMethod) {
        return getMethodName(typeMirror, valueDeserializationMethod, "deserialize");
    }

}
