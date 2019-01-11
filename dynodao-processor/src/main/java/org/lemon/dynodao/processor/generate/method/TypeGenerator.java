package org.lemon.dynodao.processor.generate.method;

import org.lemon.dynodao.processor.context.ProcessorContext;

import com.squareup.javapoet.TypeSpec;

/**
 * Creates a method to add to a pojo.
 */
interface TypeGenerator {

    /**
     * Creates a method to add to the pojo.
     *
     * @param pojo
     * @param processorContext
     * @return
     */
    void build(TypeSpec.Builder pojo, ProcessorContext processorContext);

}
