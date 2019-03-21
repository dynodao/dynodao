package org.dynodao.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a type as representative of a "schema" of a DynamoDb table.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface DynoDaoSchema {

    /**
     * Returns the name of the table for which the annotated class is the schema.
     * @return the DynamoDb table name
     */
    String tableName();
}
