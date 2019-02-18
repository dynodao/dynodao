package org.lemon.dynodao.processor.schema;

import org.lemon.dynodao.processor.context.Processors;
import org.lemon.dynodao.processor.schema.attribute.DynamoAttribute;
import org.lemon.dynodao.processor.schema.parse.SchemaParser;
import org.lemon.dynodao.processor.schema.parse.SchemaParsers;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

/**
 * Houses contextual data for parsing a {@link org.lemon.dynodao.annotation.DynoDaoSchema} type and nested classes.
 * Parsing entails serialization and deserialization of each attribute into an {@link com.amazonaws.services.dynamodbv2.model.AttributeValue},
 * which then implies the attribute value types, and eventually the structure.
 */
public class SchemaContext implements SchemaParser {

    private final Processors processors;
    private final SchemaParsers schemaParsers;

    private final Collection<DynamoAttribute> attributes = new ArrayList<>();

    SchemaContext(Processors processors, SchemaParsers schemaParsers) {
        this.processors = processors;
        this.schemaParsers = schemaParsers;
    }

    @Override
    public boolean isApplicableTo(Element element, TypeMirror typeMirror, SchemaContext schemaContext) {
        return schemaParsers.stream().anyMatch(schemaParser -> schemaParser.isApplicableTo(element, typeMirror, this));
    }

    @Override
    public DynamoAttribute parseAttribute(Element element, TypeMirror typeMirror, String path, SchemaContext schemaContext) {
        Optional<DynamoAttribute> attribute = getCachedAttribute(element, typeMirror, path);
        if (!attribute.isPresent()) {
            attribute = parseAttribute(element, typeMirror, path);
        }
        return attribute.orElseThrow(() -> new AssertionError("attribute is at least NullDynamoAttribute"));
    }

    private Optional<DynamoAttribute> getCachedAttribute(Element element, TypeMirror typeMirror, String path) {
        return attributes.stream()
                .filter(attribute -> attribute.getElement().equals(element))
                .filter(attribute -> processors.isSameType(attribute.getTypeMirror(), typeMirror))
                .filter(attribute -> attribute.getPath().equals(path))
                .findFirst();
    }

    private Optional<DynamoAttribute> parseAttribute(Element element, TypeMirror typeMirror, String path) {
        return schemaParsers.stream()
                .filter(schemaParser -> schemaParser.isApplicableTo(element, typeMirror, this))
                .map(schemaParser -> schemaParser.parseAttribute(element, typeMirror, path, this))
                .findFirst();
    }

}
