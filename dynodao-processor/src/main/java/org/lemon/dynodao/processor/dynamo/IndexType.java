package org.lemon.dynodao.processor.dynamo;

/**
 * The types of indexes possible on a dynamo table.
 */
public enum IndexType {

    TABLE,
    LOCAL_SECONDARY_INDEX,
    GLOBAL_SECONDARY_INDEX;

}
