package org.dynodao.processor.stage.generate;

import org.dynodao.processor.util.StreamUtil.Streamable;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Stores all of the implementations of {@link StageTypeSpecMutator} in the appropriate order.
 */
public class StageTypeSpecMutators implements Streamable<StageTypeSpecMutator> {

    @Inject GeneratedAnnotationStageTypeSpecMutator generatedAnnotationStageTypeSpecMutator;
    @Inject ModifiersStageTypeSpecMutator modifiersStageTypeSpecMutator;
    @Inject SuperInterfaceStageTypeSpecMutator superInterfaceStageTypeSpecMutator;
    @Inject FieldsStageTypeSpecMutator fieldsStageTypeSpecMutator;
    @Inject CtorStageTypeSpecMutator ctorStageTypeSpecMutator;
    @Inject WitherStageTypeSpecMutator witherStageTypeSpecMutator;
    @Inject UserStageTypeSpecMutator userStageTypeSpecMutator;
    @Inject CreateTableStageTypeSpecMutator createTableStageTypeSpecMutator;
    @Inject ScanStageTypeSpecMutator scanStageTypeSpecMutator;
    @Inject LoadStageTypeSpecMutator loadStageTypeSpecMutator;
    @Inject QueryStageTypeSpecMutator queryStageTypeSpecMutator;
    @Inject EqualsStageTypeSpecMutator equalsStageTypeSpecMutator;
    @Inject HashCodeStageTypeSpecMutator hashCodeStageTypeSpecMutator;
    @Inject ToStringStageTypeSpecMutator toStringStageTypeSpecMutator;

    private final List<StageTypeSpecMutator> stageTypeSpecMutators = new ArrayList<>();

    @Inject StageTypeSpecMutators() { }

    /**
     * Populates the stageTypeSpecMutators field. The list is ordered, the first elements are added to the
     * generated class first.
     */
    @Inject void initStageTypeSpecMutators() {
        stageTypeSpecMutators.add(generatedAnnotationStageTypeSpecMutator);
        stageTypeSpecMutators.add(modifiersStageTypeSpecMutator);
        stageTypeSpecMutators.add(superInterfaceStageTypeSpecMutator);
        stageTypeSpecMutators.add(fieldsStageTypeSpecMutator);
        stageTypeSpecMutators.add(ctorStageTypeSpecMutator);

        stageTypeSpecMutators.add(witherStageTypeSpecMutator);
        stageTypeSpecMutators.add(userStageTypeSpecMutator);

        stageTypeSpecMutators.add(createTableStageTypeSpecMutator);
        stageTypeSpecMutators.add(scanStageTypeSpecMutator);
        stageTypeSpecMutators.add(loadStageTypeSpecMutator);
        stageTypeSpecMutators.add(queryStageTypeSpecMutator);

        stageTypeSpecMutators.add(equalsStageTypeSpecMutator);
        stageTypeSpecMutators.add(hashCodeStageTypeSpecMutator);
        stageTypeSpecMutators.add(toStringStageTypeSpecMutator);
    }

    @Override
    public Iterator<StageTypeSpecMutator> iterator() {
        return stageTypeSpecMutators.iterator();
    }

}
