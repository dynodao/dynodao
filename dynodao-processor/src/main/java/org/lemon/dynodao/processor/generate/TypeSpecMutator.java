package org.lemon.dynodao.processor.generate;

import org.lemon.dynodao.processor.model.PojoClassBuilder;

import com.squareup.javapoet.TypeSpec;

/**
 * Builds a component of the pojo.
 */
public interface TypeSpecMutator {

    /**
     * Mutates the <tt>typeSpec</tt> to add necessary methods, fields, etc.
     *
     * @param typeSpec the type the pojo is being built into
     * @param pojo the pojo being built
     */
    void mutate(TypeSpec.Builder typeSpec, PojoClassBuilder pojo);
}
