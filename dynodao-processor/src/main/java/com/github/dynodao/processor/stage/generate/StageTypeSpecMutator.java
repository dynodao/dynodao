package com.github.dynodao.processor.stage.generate;

import com.github.dynodao.processor.stage.Stage;
import com.squareup.javapoet.TypeSpec;

/**
 * Builds a component of {@link TypeSpec} for a stage class.
 */
public interface StageTypeSpecMutator {

    /**
     * Mutates the <tt>typeSpec</tt> to add necessary methods, fields, etc.
     * @param typeSpec the type the stage is being built into
     * @param stage the stage being built
     */
    void mutate(TypeSpec.Builder typeSpec, Stage stage);

}
