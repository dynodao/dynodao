package com.github.dynodao.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field as the hash key for a global secondary index (GSI).
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface DynoDaoIndexHashKey {

    /**
     * Returns the names of the global secondary indexes for which this attribute is the hash key.
     * @return the names of the global secondary indexes for which this attribute is the hash key
     */
    String[] gsiNames();
}
