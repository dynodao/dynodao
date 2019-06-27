package com.github.dynodao.processor.stage;

import com.github.dynodao.processor.schema.attribute.DynamoAttribute;
import com.github.dynodao.processor.schema.index.DynamoIndex;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * The length of a dynamo index, or length of a query or load operation against an index.
 */
public enum KeyLengthType {

    /**
     * No keys are used. This is a placeholder, or indicates a scan operation.
     */
    NONE() {
        @Override
        public List<DynamoAttribute> getKeyAttributes(DynamoIndex index) {
            return emptyList();
        }
    },

    /**
     * The only key is a hash key.
     */
    HASH() {
        @Override
        public List<DynamoAttribute> getKeyAttributes(DynamoIndex index) {
            return singletonList(index.getHashKey());
        }
    },

    /**
     * Both hash and range keys are used.
     */
    RANGE() {
        @Override
        public List<DynamoAttribute> getKeyAttributes(DynamoIndex index) {
            return Arrays.asList(index.getHashKey(), index.getRangeKey().get());
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
    public static KeyLengthType lengthOf(DynamoIndex index) {
        return index.getRangeKey().isPresent() ? RANGE : HASH;
    }

}
