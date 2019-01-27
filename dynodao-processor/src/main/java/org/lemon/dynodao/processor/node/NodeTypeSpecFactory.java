package org.lemon.dynodao.processor.node;

import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.node.generate.NodeTypeSpecMutators;

import javax.inject.Inject;

import static org.lemon.dynodao.processor.util.StringUtil.capitalize;
import static org.lemon.dynodao.processor.util.StringUtil.toClassCase;

/**
 * Produces {@link NodeTypeSpec} types from their {@link NodeClassData} specifications.
 */
public class NodeTypeSpecFactory {

    @Inject NodeTypeSpecMutators nodeTypeSpecMutators;

    @Inject NodeTypeSpecFactory() { }

    /**
     * @param node the node class to build
     * @return the built node
     */
    public NodeTypeSpec build(NodeClassData node) {
        TypeSpec.Builder typeSpec = TypeSpec.classBuilder(getClassName(node));
        nodeTypeSpecMutators.forEach(mutator -> mutator.mutate(typeSpec, node));
        return new NodeTypeSpec(node, typeSpec.build());
    }

    private String getClassName(NodeClassData node) {
        if (node.isStagedBuilder()) {
            return getStagedBuilderClassName(node);
        } else {
            return getIndexNodeClassName(node);
        }
    }

    private String getStagedBuilderClassName(NodeClassData node) {
        return node.getDocument().getSimpleName() + "StagedDynamoBuilder";
    }

    private String getIndexNodeClassName(NodeClassData node) {
        StringBuilder name = new StringBuilder();

        name.append(toClassCase(node.getDynamoIndex().getName()));

        if (node.getIndexLengthType().compareTo(IndexLengthType.HASH) >= 0) {
            name.append(capitalize(node.getDynamoIndex().getHashKeyAttribute().getField()));
        }
        if (node.getIndexLengthType().compareTo(IndexLengthType.RANGE) >= 0) {
            name.append(capitalize(node.getDynamoIndex().getRangeKeyAttribute().get().getField()));
        }

        name.append(node.getDocument().getSimpleName());

        node.getInterfaceType().getInterfaceClass().ifPresent(clazz -> name.append(clazz.getSimpleName()));
        return name.toString();
    }

}
