package org.lemon.dynodao;

/**
 * Marks a model class for dynamo dao generation.
 */
public @interface DynoDao {


    /**
     * Make a new class? Would require (prefer) adding lombok to the build path...
     * @return true to automatically save an audit trail in the record itself
     */
    boolean auditTrail() default false;

    /**
     * change the target implementation package
     * @return the name of the package, if empty use the same package as annotated class
     */
    String implPackage() default "";

}
