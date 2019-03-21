package org.dynodao.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field as the range key for a local secondary index (LSI), or for global secondary index (GSI).
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface DynoDaoIndexRangeKey {

    /**
     * Returns the names of the local secondary indexes for which this attribute is the range key.
     * @return the names of the local secondary indexes for which this attribute is the range key
     */
    String[] lsiNames() default { };

    /**
     * Returns the names of the global secondary indexes for which this attribute is the range key.
     * @return the names of the global secondary indexes for which this attribute is the range key
     */
    String[] gsiNames() default { };
}
