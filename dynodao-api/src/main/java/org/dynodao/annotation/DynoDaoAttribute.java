package org.dynodao.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field as an attribute in the DynamoDb schema. This annotation is optional, all fields are assuming to be
 * part of the schema unless explicitly annotation with {@link DynoDaoIgnore}. This annotation is only required when
 * the name of the field (in the class) is different than the name of the attribute as stored in DynamoDb.
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface DynoDaoAttribute {

    /**
     * Returns the name of the attribute in DynamoDb. If left as default (the empty String), then the attribute
     * name is the same as the field name.
     * @return the name of the attribute in DynamoDb
     */
    String value();
}
