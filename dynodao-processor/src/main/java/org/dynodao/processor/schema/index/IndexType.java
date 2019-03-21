package org.dynodao.processor.schema.index;

/**
 * The types of indexes possible on a dynamo table.
 */
public enum IndexType {

    /**
     * Not an "index" per say, indicates the table itself.
     */
    TABLE,

    /**
     * The index is a local secondary index (LSI).
     */
    LOCAL_SECONDARY_INDEX,

    /**
     * The index is a global secondary index (GSI).
     */
    GLOBAL_SECONDARY_INDEX;

}

