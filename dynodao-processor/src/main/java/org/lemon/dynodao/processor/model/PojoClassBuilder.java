package org.lemon.dynodao.processor.model;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.lemon.dynodao.processor.index.DynamoIndex;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data model for a pojo type to generate.
 */
@Data
@Setter(AccessLevel.NONE)
public class PojoClassBuilder {

    private final TypeElement document;
    private final List<FieldSpec> fields = new ArrayList<>();

    private Optional<DynamoIndex> dynamoIndex;
    private IndexLengthType indexLengthType = IndexLengthType.NONE;
    private InterfaceType interfaceType = InterfaceType.NONE;

    private final List<TypeSpec> targetWithers = new ArrayList<>();

    /**
     * @param index the index to use
     * @param indexLengthType the number of fields to use from the index
     */
    public void setIndex(DynamoIndex index, IndexLengthType indexLengthType) {
        this.dynamoIndex = Optional.of(index);
        this.indexLengthType = indexLengthType;

        indexLengthType.getFields(index).forEach(this::addField);
        this.interfaceType = InterfaceType.typeOf(index, indexLengthType);
    }

    private void addField(VariableElement indexField) {
        FieldSpec field = FieldSpec.builder(TypeName.get(indexField.asType()), indexField.getSimpleName().toString())
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();
        fields.add(field);
    }

    /**
     * Specifies this pojo should have a <tt>with</tt> method to create the given type.
     * The type is creating using fields of this pojo, plus arguments sent to the wither method.
     * @param targetWither the type of class to create using a wither
     */
    public void addWither(TypeSpec targetWither) {
        targetWithers.add(targetWither);
    }

}
