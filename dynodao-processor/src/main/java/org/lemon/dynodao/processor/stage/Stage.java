package org.lemon.dynodao.processor.stage;

import com.squareup.javapoet.FieldSpec;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.lemon.dynodao.processor.schema.DynamoSchema;
import org.lemon.dynodao.processor.schema.attribute.DynamoAttribute;
import org.lemon.dynodao.processor.schema.index.DynamoIndex;
import org.lemon.dynodao.processor.serialize.SerializerTypeSpec;

import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

/**
 * Data model for a stage type to generate. Each model models a portion of a single dynamo index,
 * including the staged builder entry point to the chain.
 */
@Data
@Setter(AccessLevel.NONE)
public final class Stage {

    private final DynamoSchema schema;
    private final SerializerTypeSpec serializer;
    private final List<DynamoAttribute> attributes = new ArrayList<>();

    private DynamoIndex dynamoIndex;
    private KeyLengthType keyLengthType = KeyLengthType.NONE;
    private Set<InterfaceType> interfaceTypes = EnumSet.noneOf(InterfaceType.class);

    private final List<StageTypeSpec> targetWithers = new ArrayList<>();
    private final List<StageTypeSpec> targetUsingIndexes = new ArrayList<>();

    /**
     * @param index the index to use
     * @param keyLengthType the number of fields to use from the index
     * @return <tt>this</tt>
     */
    public Stage withIndex(DynamoIndex index, KeyLengthType keyLengthType) {
        this.dynamoIndex = index;
        this.keyLengthType = keyLengthType;

        this.attributes.addAll(keyLengthType.getKeyAttributes(index));
        interfaceTypes.add(InterfaceType.typeOf(index, keyLengthType));
        return this;
    }

    /**
     * Adds the model which <tt>this</tt> model should have a <tt>with</tt> method for.
     * @param stage the existing stage type this class should have a <tt>with</tt> method for
     * @return <tt>this</tt>
     */
    public Stage addWither(StageTypeSpec stage) {
        targetWithers.add(stage);
        return this;
    }

    /**
     * Adds the model which <tt>this</tt> model should have an <tt>using</tt> method for.
     * @param stage the existing stage type this class should have a <tt>using</tt> method for
     * @return <tt>this</tt>
     */
    public Stage addUser(StageTypeSpec stage) {
        interfaceTypes.add(InterfaceType.CREATE);
        targetUsingIndexes.add(stage);
        return this;
    }

    /**
     * @return <tt>true</tt> if this class models the staged builder entry point, <tt>false</tt> otherwise
     */
    public boolean isStagedBuilder() {
        return dynamoIndex == null;
    }

    /**
     * @return the attributes of this model as {@link FieldSpec} as per {@link DynamoAttribute#asFieldSpec()}
     */
    public List<FieldSpec> getAttributesAsFields() {
        return attributes.stream()
                .map(DynamoAttribute::asFieldSpec)
                .collect(toList());
    }

    /**
     * @return the schema document element
     */
    public TypeElement getDocumentElement() {
        return schema.getDocumentElement();
    }

}
