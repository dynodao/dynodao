package org.lemon.dynodao.processor.schema.attribute;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import org.lemon.dynodao.processor.schema.serialize.MappingMethod;
import org.lemon.dynodao.processor.util.SimpleDynamoAttributeVisitor;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single attribute in dynamo. This may be top level or nested within another attribute.
 */
public interface DynamoAttribute {

    /**
     * The name of this attribute as it appears in Dynamo, or the relative path to it in the case of nested attributes.
     * @return the name of this attribute
     */
    String getPath();

    /**
     * The element of the schema (or nested therein) which models this attribute. One element may force the creation of
     * multiple nested attributes.
     * @return the schema element which models this attribute
     */
    Element getElement();

    /**
     * The type in the schema (or nested therein) which models this attribute.
     * While the element may represent multiple attributes, each type models one attribute.
     * @return the type in the schema which models this attribute
     */
    TypeMirror getTypeMirror();

    /**
     * The type of data stored in this attribute in dynamo.
     * @return the type of data stored in this attribute
     */
    DynamoAttributeType getAttributeType();

    /**
     * A method that converts this attribute into an {@link com.amazonaws.services.dynamodbv2.model.AttributeValue}.
     * @return a method specification that converts this this attribute to an {@link com.amazonaws.services.dynamodbv2.model.AttributeValue}
     */
    MappingMethod getSerializationMethod();

    /**
     * A method that converts an {@link com.amazonaws.services.dynamodbv2.model.AttributeValue} into the javaland version
     * of this attribute.
     * @return a method specification that converts an {@link com.amazonaws.services.dynamodbv2.model.AttributeValue} to this attribute
     */
    MappingMethod getDeserializationMethod();

    /**
     * @return this attribute as a {@link FieldSpec}, to be placed in some generated class
     */
    default FieldSpec asFieldSpec() {
        return FieldSpec.builder(TypeName.get(getTypeMirror()), getElement().getSimpleName().toString())
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();
    }

    /**
     * @return this attribute as a parameter to a method
     */
    default ParameterSpec asParameterSpec() {
        return ParameterSpec.builder(TypeName.get(getTypeMirror()), getElement().getSimpleName().toString()).build();
    }

    /**
     * Applies a visitor to this attribute.
     * @param visitor the visitor operating on this attribute
     * @param arg the additional parameter to give to the visitor
     * @param <R> the return type of the visitor's methods
     * @param <P> the type of the additional parameter the visitor accepts
     * @return the visitor specified result
     */
    <R, P> R accept(DynamoAttributeVisitor<R, P> visitor, P arg);

    /**
     * A shorthand for <tt>accept(visitor, null)</tt> when the parameter type is not used.
     * @param visitor the visitor operating on this attribute
     * @param <R> the return type of the visitor's methods
     * @return the visitor specified result
     */
    default <R> R accept(DynamoAttributeVisitor<R, ?> visitor) {
        return accept(visitor, null);
    }

    /**
     * A utility method to retrieve all attributes nested within this attribute, including <tt>this</tt>, recursively.
     * @return all attributes nested within this attribute, including <tt>this</tt>
     */
    default List<DynamoAttribute> getNestedAttributesRecursively() {
        List<DynamoAttribute> attributes = new ArrayList<>();
        DynamoAttributeVisitor<?, ?> visitor = new SimpleDynamoAttributeVisitor<Void, Void>(null) {

            @Override
            protected Void defaultAction(DynamoAttribute attribute, Void arg) {
                attributes.add(attribute);
                return super.defaultAction(attribute, arg);
            }

            @Override
            public Void visitBinarySet(BinarySetDynamoAttribute binarySet, Void arg) {
                binarySet.getSetElement().accept(this, arg);
                return super.visitBinarySet(binarySet, arg);
            }

            @Override
            public Void visitDocument(DocumentDynamoAttribute document, Void arg) {
                document.getAttributes().forEach(attribute -> attribute.accept(this, arg));
                return super.visitDocument(document, arg);
            }

            @Override
            public Void visitList(ListDynamoAttribute list, Void arg) {
                list.getListElement().accept(this, arg);
                return super.visitList(list, arg);
            }

            @Override
            public Void visitMap(MapDynamoAttribute map, Void arg) {
                map.getMapElement().accept(this, arg);
                return super.visitMap(map, arg);
            }

            @Override
            public Void visitNumberSet(NumberSetDynamoAttribute numberSet, Void arg) {
                numberSet.getSetElement().accept(this, arg);
                return super.visitNumberSet(numberSet, arg);
            }

            @Override
            public Void visitStringSet(StringSetDynamoAttribute stringSet, Void arg) {
                stringSet.getSetElement().accept(this, arg);
                return super.visitStringSet(stringSet, arg);
            }

        };
        accept(visitor);
        return attributes;
    }

}
