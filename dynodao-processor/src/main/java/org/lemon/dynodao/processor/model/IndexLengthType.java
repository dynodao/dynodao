package org.lemon.dynodao.processor.model;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import org.lemon.dynodao.processor.index.DynamoIndex;

import javax.lang.model.element.VariableElement;
import java.util.Arrays;
import java.util.List;

public enum IndexLengthType {

    NONE() {
        @Override
        public List<VariableElement> getFields(DynamoIndex index) {
            return emptyList();
        }
    },
    HASH() {
        @Override
        public List<VariableElement> getFields(DynamoIndex index) {
            return singletonList(index.getHashKey());
        }
    },
    RANGE() {
        @Override
        public List<VariableElement> getFields(DynamoIndex index) {
            return Arrays.asList(index.getHashKey(), index.getRangeKey().get());
        }
    };

    /**
     * Returns the key fields from the index according to the length of <tt>this</tt>.
     * @param index the index to extract fields from
     * @return the key fields from the index
     */
    public abstract List<VariableElement> getFields(DynamoIndex index);

    /**
     * Returns the full index length of <tt>index</tt>.
     * @param index the index to get the length of
     * @return the length of <tt>index</tt>
     */
    public static IndexLengthType lengthOf(DynamoIndex index) {
        return index.getRangeKey().isPresent() ? RANGE : HASH;
    }
}
