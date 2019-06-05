package org.dynodao.processor.schema;

import org.dynodao.annotation.DynoDaoSchema;
import org.dynodao.processor.schema.attribute.DynamoAttribute;
import org.dynodao.processor.schema.parse.SchemaParsers;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

/**
 * Houses contextual data for parsing a {@link DynoDaoSchema} type and nested classes.
 * Parsing entails serialization and deserialization of each attribute into an {@link com.amazonaws.services.dynamodbv2.model.AttributeValue},
 * which then implies the attribute value types, and eventually the structure.
 */
public class SchemaContext {

    private final SchemaParsers schemaParsers;

    SchemaContext(SchemaParsers schemaParsers) {
        this.schemaParsers = schemaParsers;
    }

    /**
     * Returns <tt>true</tt> if this parser instance applies to the element provided.
     * @param element the schema element to test
     * @param typeMirror the specific type to test, may be <tt>element</tt> or nested within
     * @return <tt>true</tt> if this applies to the element, <tt>false</tt> otherwise
     */
    public boolean isApplicableTo(Element element, TypeMirror typeMirror) {
        return schemaParsers.stream().anyMatch(schemaParser -> schemaParser.isApplicableTo(element, typeMirror, this));
    }

    /**
     * Returns a new {@link DynamoAttribute} from the given contextual data.
     * @param element the element to parse
     * @param typeMirror the specific type to parse, may be <tt>element</tt> or nested within
     * @param path the relative path to the attribute
     * @return the attribute
     */
    public DynamoAttribute parseAttribute(Element element, TypeMirror typeMirror, String path) {
        return schemaParsers.stream()
                .filter(schemaParser -> schemaParser.isApplicableTo(element, typeMirror, this))
                .map(schemaParser -> schemaParser.parseAttribute(element, typeMirror, path, this))
                .findFirst()
                .orElseThrow(() -> new AssertionError("attribute is at least NullDynamoAttribute"));
    }

}
