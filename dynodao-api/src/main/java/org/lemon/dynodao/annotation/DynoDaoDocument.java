package org.lemon.dynodao.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as serializable to a single map attribute.
 * The serialization of the annotated class is a map with the field names for keys. You can further specify
 * {@link DynoDaoAttribute} to change the key names, and {@link DynoDaoValueMapped} to specify serialization.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface DynoDaoDocument {
}
