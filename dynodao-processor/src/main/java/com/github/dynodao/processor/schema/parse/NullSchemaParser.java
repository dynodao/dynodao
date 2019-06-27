package com.github.dynodao.processor.schema.parse;

import com.github.dynodao.processor.context.ProcessorMessager;
import com.github.dynodao.processor.schema.attribute.NullDynamoAttribute;
import com.github.dynodao.processor.schema.serialize.DeserializationMappingMethod;
import com.github.dynodao.processor.schema.serialize.SerializationMappingMethod;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.github.dynodao.processor.schema.SchemaContext;

import javax.inject.Inject;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

import static com.github.dynodao.processor.util.StringUtil.capitalize;

/**
 * Parses an element and type into a null attribute. This is a special case, we always model all fields
 * of a schema, but we may model them as null. If we end up at null, we must submit an error.
 */
class NullSchemaParser implements SchemaParser {

    private final ProcessorMessager processorMessager;

    @Inject NullSchemaParser(ProcessorMessager processorMessager) {
        this.processorMessager = processorMessager;
    }

    @Override
    public boolean isApplicableTo(Element element, TypeMirror typeMirror, SchemaContext schemaContext) {
        return true;
    }

    @Override
    public NullDynamoAttribute parseAttribute(Element element, TypeMirror typeMirror, String path, SchemaContext schemaContext) {
        processorMessager.submitError("unable to parse [%s] into a dynamo attribute; specify either a type or field level override", element)
                .atElement(element);
        return NullDynamoAttribute.builder()
                .element(element)
                .typeMirror(typeMirror)
                .path(path)
                .serializationMethod(buildSerializationMethod(element, typeMirror))
                .deserializationMethod(buildDeserializationMethod(element, typeMirror))
                .build();
    }

    private SerializationMappingMethod buildSerializationMethod(Element element, TypeMirror typeMirror) {
        return SerializationMappingMethod.builder()
                .methodName("serialize" + capitalize(element) + "_toNull")
                .parameter(ParameterSpec.builder(TypeName.get(typeMirror), "nil").build())
                .coreMethodBody(CodeBlock.builder()
                        .addStatement("return null")
                        .build())
                .build();
    }

    private DeserializationMappingMethod buildDeserializationMethod(Element element, TypeMirror typeMirror) {
        return DeserializationMappingMethod.builder()
                .methodName("deserialize" + capitalize(element) + "_toNull")
                .returnType(TypeName.get(typeMirror))
                .coreMethodBody(CodeBlock.builder()
                        .addStatement("return null")
                        .build())
                .build();
    }

}
