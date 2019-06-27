package com.github.dynodao.processor.stage;

import com.github.dynodao.DynoDaoCreateTable;
import com.github.dynodao.DynoDaoLoad;
import com.github.dynodao.DynoDaoQuery;
import com.github.dynodao.DynoDaoScan;
import com.github.dynodao.processor.schema.index.DynamoIndex;
import com.github.dynodao.processor.schema.index.IndexType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Indicates which of the simplified interfaces like {@link DynoDaoLoad} or {@link DynoDaoQuery} a type implements.
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum InterfaceType {

    CREATE(DynoDaoCreateTable.class),
    SCAN(DynoDaoScan.class),
    LOAD(DynoDaoLoad.class),
    QUERY(DynoDaoQuery.class);

    private final Class<?> interfaceClass;

    /**
     * Returns the interface for the given index and length type.
     * @param index the dynamo index
     * @param keyLengthType the length type being used from the index
     * @return the interface type, either load or query
     */
    public static InterfaceType typeOf(DynamoIndex index, KeyLengthType keyLengthType) {
        if (keyLengthType.equals(KeyLengthType.NONE)) {
            return SCAN;
        } else if (KeyLengthType.lengthOf(index).equals(keyLengthType) && index.getIndexType().equals(IndexType.TABLE)) {
            return LOAD;
        } else {
            return QUERY;
        }
    }

}
