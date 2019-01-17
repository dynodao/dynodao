package org.lemon.dynodao.processor.model;

import static java.util.stream.Collectors.toList;

import com.squareup.javapoet.FieldSpec;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.lemon.dynodao.processor.dynamo.DynamoAttribute;
import org.lemon.dynodao.processor.dynamo.DynamoIndex;

import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data model for a pojo type to generate. Each pojo models a portion of a single dynamo index.
 */
@Data
@Setter(AccessLevel.NONE)
public class PojoClassBuilder {

    private final TypeElement document;
    private final List<DynamoAttribute> attributes = new ArrayList<>();

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

        this.attributes.addAll(indexLengthType.getKeyAttributes(index));
        this.interfaceType = InterfaceType.typeOf(index, indexLengthType);
        return this;
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

    /**
     * @return the attributes of this pojo as {@link FieldSpec} as per {@link DynamoAttribute#asFieldSpec()}
     */
    public List<FieldSpec> getAttributesAsFields() {
        return attributes.stream()
                .map(DynamoAttribute::asFieldSpec)
                .collect(toList());
    }

}
