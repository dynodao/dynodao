package com.github.dynodao.processor.schema.parse;

import com.github.dynodao.processor.schema.attribute.DynamoAttribute;
import com.github.dynodao.processor.schema.SchemaContext;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

/**
 * Parses a schema code element into a {@link DynamoAttribute}.
 */
public interface SchemaParser {

    /**
     * Returns <tt>true</tt> if this parser instance applies to the element provided.
     * @param element the schema element to test
     * @param typeMirror the specific type to test, may be <tt>element</tt> or nested within
     * @param schemaContext the context in which the schema parsing occurs
     * @return <tt>true</tt> if this applies to the element, <tt>false</tt> otherwise
     */
    boolean isApplicableTo(Element element, TypeMirror typeMirror, SchemaContext schemaContext);

    /**
     * Returns a new {@link DynamoAttribute} from the given contextual data.
     * @param element the element to parse
     * @param typeMirror the specific type to parse, may be <tt>element</tt> or nested within
     * @param path the relative path to the attribute
     * @param schemaContext the context in which the schema parsing occurs
     * @return the attribute
     */
    DynamoAttribute parseAttribute(Element element, TypeMirror typeMirror, String path, SchemaContext schemaContext);

}
