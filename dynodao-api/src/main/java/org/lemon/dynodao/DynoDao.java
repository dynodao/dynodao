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
     * Make a new class? Would require (prefer) adding lombok to the build path...
     * @return true to automatically save an audit trail in the record itself
     * @deprecated not implemented, just a todo note
     */
    @Deprecated
    boolean auditTrail() default false;

    /**
     * change the target implementation package
     * @return the name of the package, if empty use the same package as annotated class
     */
    String implPackage() default "";

}
