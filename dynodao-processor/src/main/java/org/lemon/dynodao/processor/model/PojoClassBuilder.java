package org.lemon.dynodao.processor.model;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import org.lemon.dynodao.processor.dynamo.DynamoIndex;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

/**
 * Data model for a pojo type to generate.
 */
@Data
@Setter(AccessLevel.NONE)
public class PojoClassBuilder {

    private final TypeElement document;
    private final List<FieldSpec> fields = new ArrayList<>();

    private DynamoIndex dynamoIndex;
    private IndexLengthType indexLengthType = IndexLengthType.NONE;
    private InterfaceType interfaceType = InterfaceType.NONE;

    private final List<PojoTypeSpec> targetWithers = new ArrayList<>();
    private final List<PojoTypeSpec> targetUsingIndexes = new ArrayList<>();

    /**
     * @param index the index to use
     * @param indexLengthType the number of fields to use from the index
     * @return <tt>this</tt>
     */
    public PojoClassBuilder withIndex(DynamoIndex index, IndexLengthType indexLengthType) {
        this.dynamoIndex = index;
        this.indexLengthType = indexLengthType;

        indexLengthType.getFields(index).forEach(this::addField);
        this.interfaceType = InterfaceType.typeOf(index, indexLengthType);
        return this;
    }

    private void addField(VariableElement indexField) {
        FieldSpec field = FieldSpec.builder(TypeName.get(indexField.asType()), indexField.getSimpleName().toString())
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();
        fields.add(field);
    }

    /**
     * Adds the pojo which <tt>this</tt> pojo should have a <tt>with</tt> method for.
     * @param pojo the existing pojo type this class should have a <tt>with</tt> method for
     * @return <tt>this</tt>
     */
    public PojoClassBuilder addWither(PojoTypeSpec pojo) {
        targetWithers.add(pojo);
        return this;
    }

    /**
     * Adds the pojo which <tt>this</tt> pojo should have an <tt>using</tt> method for.
     * @param pojo the existing pojo type this class should have a <tt>using</tt> method for
     * @return <tt>this</tt>
     */
    public PojoClassBuilder addUser(PojoTypeSpec pojo) {
        targetUsingIndexes.add(pojo);
        return this;
    }

}
