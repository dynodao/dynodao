package org.lemon.dynodao.processor.node.generate;

import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.node.NodeClassData;

import javax.annotation.Generated;
import javax.inject.Inject;

import static org.lemon.dynodao.processor.util.DynoDaoUtil.generatedAnnotation;

/**
 * Adds the {@link Generated} annotation to the type.
 */
class GeneratedAnnotationNodeTypeSpecMutator implements NodeTypeSpecMutator {

    @Inject GeneratedAnnotationNodeTypeSpecMutator() { }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, NodeClassData node) {
        typeSpec.addAnnotation(generatedAnnotation());
    }
}
