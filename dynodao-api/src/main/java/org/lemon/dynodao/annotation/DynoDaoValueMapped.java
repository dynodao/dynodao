package org.lemon.dynodao.annotation;

import org.lemon.dynodao.DynoDaoAttributeValueMapper;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field as the table range key.
 * @see {@link DynoDaoAttributeValueMapper}
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface DynoDaoValueMapped {

    /**
     * Returns the {@link DynoDaoAttributeValueMapper} which will be responsible for converting the annotated field
     * to and from {@link com.amazonaws.services.dynamodbv2.model.AttributeValue}.
     * @return the Class of the {@link DynoDaoAttributeValueMapper} which will handle convert the annotated field
     */
    Class<? extends DynoDaoAttributeValueMapper> value();
}
