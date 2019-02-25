package org.lemon.dynodao.processor.node;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.lemon.dynodao.DynoDaoLoad;
import org.lemon.dynodao.DynoDaoQuery;
import org.lemon.dynodao.processor.schema.index.DynamoIndex;
import org.lemon.dynodao.processor.schema.index.IndexType;

import java.util.Optional;

/**
 * Indicates which of {@link DynoDaoLoad} or {@link DynoDaoQuery} interfaces a type
 * should implement, if any.
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum InterfaceType {

    NONE(Optional.empty()),
    LOAD(Optional.of(DynoDaoLoad.class)),
    QUERY(Optional.of(DynoDaoQuery.class));

    private final Optional<Class<?>> interfaceClass;

    /**
     * Returns the interface for the given index and length type.
     * @param index the dynamo index
     * @param keyLengthType the length type being used from the index
     * @return the interface type, either load or query
     */
    public static InterfaceType typeOf(DynamoIndex index, KeyLengthType keyLengthType) {
        if (keyLengthType.equals(KeyLengthType.NONE)) {
            return NONE;
        } else if (KeyLengthType.lengthOf(index).equals(keyLengthType) && index.getIndexType().equals(IndexType.TABLE)) {
            return LOAD;
        } else {
            return QUERY;
        }
    }

}
