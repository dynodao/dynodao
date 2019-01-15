package org.lemon.dynodao;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a model class for dynamo dao generation.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface DynoDao {

    /**
     * Sets the target implementation package to generate all of the classes in. By default, the classes
     * are generated in the same package as the annotated class.
     * @return the name of the package, if empty use the same package as annotated class
     */
    String implPackage() default "";

}
