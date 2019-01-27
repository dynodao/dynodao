package org.lemon.dynodao.processor.node;

import org.lemon.dynodao.processor.dynamo.DynamoAttribute;
import org.lemon.dynodao.processor.dynamo.DynamoIndex;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * The length of a dynamo index, or length of a query against an index.
 */
public enum IndexLengthType {

    /**
     * A placeholder length, to specify there is no index.
     */
    NONE() {
        @Override
        public List<DynamoAttribute> getKeyAttributes(DynamoIndex index) {
            return emptyList();
        }
    },

    /**
     * The index only has a hash key.
     */
    HASH() {
        @Override
        public List<DynamoAttribute> getKeyAttributes(DynamoIndex index) {
            return singletonList(index.getHashKeyAttribute());
        }
    },

    /**
     * The index has a hash key and a range key.
     */
    RANGE() {
        @Override
        public List<DynamoAttribute> getKeyAttributes(DynamoIndex index) {
            return Arrays.asList(index.getHashKeyAttribute(), index.getRangeKeyAttribute().get());
        }
    };

    /**
     * Returns the key attributes from the index according to the length of <tt>this</tt>.
     * @param index the index to extract attributes from
     * @return the key attributes from the index
     */
    public abstract List<DynamoAttribute> getKeyAttributes(DynamoIndex index);

    /**
     * Returns the full index length of <tt>index</tt>.
     * @param index the index to get the length of
     * @return the length of <tt>index</tt>
     */
    public static IndexLengthType lengthOf(DynamoIndex index) {
        return index.getRangeKeyAttribute().isPresent() ? RANGE : HASH;
    }
}
