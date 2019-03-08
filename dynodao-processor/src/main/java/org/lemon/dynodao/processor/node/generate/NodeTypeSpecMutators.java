package org.lemon.dynodao.processor.node.generate;

import org.lemon.dynodao.processor.util.StreamUtil.Streamable;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Stores all of the implementations of {@link NodeTypeSpecMutators} in the appropriate order.
 */
public class NodeTypeSpecMutators implements Streamable<NodeTypeSpecMutator> {

    @Inject GeneratedAnnotationNodeTypeSpecMutator generatedAnnotationNodeTypeSpecMutator;
    @Inject ModifiersNodeTypeSpecMutator modifiersNodeTypeSpecMutator;
    @Inject SuperInterfaceNodeTypeSpecMutator superInterfaceNodeTypeSpecMutator;
    @Inject FieldsNodeTypeSpecMutator fieldsNodeTypeSpecMutator;
    @Inject CtorNodeTypeSpecMutator ctorNodeTypeSpecMutator;
    @Inject WitherNodeTypeSpecMutator witherNodeTypeSpecMutator;
    @Inject UserNodeTypeSpecMutator userNodeTypeSpecMutator;
    @Inject CreateTableTypeSpecMutator createTableTypeSpecMutator;
    @Inject LoadNodeTypeSpecMutator loadNodeTypeSpecMutator;
    @Inject QueryNodeTypeSpecMutator queryNodeTypeSpecMutator;
    @Inject EqualsNodeTypeSpecMutator equalsNodeTypeSpecMutator;
    @Inject HashCodeNodeTypeSpecMutator hashCodeNodeTypeSpecMutator;
    @Inject ToStringNodeTypeSpecMutator toStringNodeTypeSpecMutator;

    private final List<NodeTypeSpecMutator> nodeTypeSpecMutators = new ArrayList<>();

    @Inject NodeTypeSpecMutators() { }

    /**
     * Populates the nodeTypeSpecMutators field. The list is ordered, the first elements are added to the
     * generated class first.
     */
    @Inject void initNoteTypeSpecMutators() {
        nodeTypeSpecMutators.add(generatedAnnotationNodeTypeSpecMutator);
        nodeTypeSpecMutators.add(modifiersNodeTypeSpecMutator);
        nodeTypeSpecMutators.add(superInterfaceNodeTypeSpecMutator);
        nodeTypeSpecMutators.add(fieldsNodeTypeSpecMutator);
        nodeTypeSpecMutators.add(ctorNodeTypeSpecMutator);

        nodeTypeSpecMutators.add(witherNodeTypeSpecMutator);
        nodeTypeSpecMutators.add(userNodeTypeSpecMutator);

        nodeTypeSpecMutators.add(createTableTypeSpecMutator);
        nodeTypeSpecMutators.add(loadNodeTypeSpecMutator);
        nodeTypeSpecMutators.add(queryNodeTypeSpecMutator);

        nodeTypeSpecMutators.add(equalsNodeTypeSpecMutator);
        nodeTypeSpecMutators.add(hashCodeNodeTypeSpecMutator);
        nodeTypeSpecMutators.add(toStringNodeTypeSpecMutator);
    }

    @Override
    public Iterator<NodeTypeSpecMutator> iterator() {
        return nodeTypeSpecMutators.iterator();
    }

}
