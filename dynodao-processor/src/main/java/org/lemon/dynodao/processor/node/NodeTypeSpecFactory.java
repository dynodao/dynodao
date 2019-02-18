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

    private final NodeTypeSpecMutators nodeTypeSpecMutators;

    @Inject NodeTypeSpecFactory(NodeTypeSpecMutators nodeTypeSpecMutators) {
        this.nodeTypeSpecMutators = nodeTypeSpecMutators;
    }

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
        return node.getDocumentElement().getSimpleName() + "StagedDynamoBuilder";
    }

    private String getIndexNodeClassName(NodeClassData node) {
        StringBuilder name = new StringBuilder();

        name.append(toClassCase(node.getDynamoIndex().getName()));

        if (node.getKeyLengthType().compareTo(KeyLengthType.HASH) >= 0) {
            name.append(capitalize(node.getDynamoIndex().getHashKey().getElement()));
        }
        if (node.getKeyLengthType().compareTo(KeyLengthType.RANGE) >= 0) {
            name.append(capitalize(node.getDynamoIndex().getRangeKey().get().getElement()));
        }

        name.append(node.getDocumentElement().getSimpleName());

        node.getInterfaceType().getInterfaceClass().ifPresent(clazz -> name.append(clazz.getSimpleName()));
        return name.toString();
    }

}
