package org.lemon.dynodao.processor.model;

import java.util.Optional;

import org.lemon.dynodao.DocumentLoad;
import org.lemon.dynodao.DocumentQuery;
import org.lemon.dynodao.processor.index.DynamoIndex;
import org.lemon.dynodao.processor.index.IndexType;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Indicates which of {@link DocumentLoad} or {@link DocumentQuery} interfaces a type
 * should implement, if any.
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum InterfaceType {

    NONE(Optional.empty()),
    DOCUMENT_LOAD(Optional.of(DocumentLoad.class)),
    DOCUMENT_QUERY(Optional.of(DocumentQuery.class));

    private final Optional<Class<?>> interfaceClass;

    /**
     * Returns the interface for the given index and length type.
     * @param index the dynamo index
     * @param indexLengthType the length type being used from the index
     * @return the interface type, either load or query
     */
    public static InterfaceType typeOf(DynamoIndex index, IndexLengthType indexLengthType) {
        if (indexLengthType.equals(IndexLengthType.NONE)) {
            return NONE;
        } else if (IndexLengthType.lengthOf(index).equals(indexLengthType) && index.getIndexType().equals(IndexType.TABLE)) {
            return DOCUMENT_LOAD;
        } else {
            return DOCUMENT_QUERY;
        }
    }

}
