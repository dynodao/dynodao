package org.lemon.dynodao.processor.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import org.lemon.dynodao.processor.index.DynamoIndex;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

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
    private IndexLengthType indexLengthType;
    private InterfaceType interfaceType;

    private final List<TypeSpec> targetWithers = new ArrayList<>();

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
     * Adds the pojos which <tt>this</tt> pojo should have a <tt>with</tt> method for.
     * @param pojos the existing pojo types
     * @return <tt>this</tt>
     */
    public PojoClassBuilder addApplicableWithers(Collection<PojoTypeSpec> pojos) {
        pojos.forEach(this::tryAddWither);
        return this;
    }

    private void tryAddWither(PojoTypeSpec targetPojoWither) {
        PojoClassBuilder pojo = targetPojoWither.getPojo();
        if (Objects.equals(dynamoIndex, pojo.getDynamoIndex()) && pojo.getFields().containsAll(fields)) {
            targetWithers.add(targetPojoWither.getTypeSpec());
        }
    }

}
