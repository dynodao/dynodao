package org.lemon.dynodao.processor.node.generate;

import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.node.NodeClassData;

/**
 * Builds a component of {@link TypeSpec} for a node pojo class.
 */
public interface NodeTypeSpecMutator {

    /**
     * Mutates the <tt>typeSpec</tt> to add necessary methods, fields, etc.
     * @param typeSpec the type the node is being built into
     * @param node the node being built
     */
    void mutate(TypeSpec.Builder typeSpec, NodeClassData node);
}
