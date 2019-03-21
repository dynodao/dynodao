package org.dynodao.processor;

import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.TypeElement;

/**
 * Marks a type as containing a {@link TypeSpec} which should be written to file.
 */
public interface BuiltTypeSpec {

    /**
     * @return the document class for which this class was generated in response to
     */
    TypeElement getDocumentElement();

    /**
     * @return the type to write to file
     */
    TypeSpec getTypeSpec();

}
