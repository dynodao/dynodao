package org.dynodao.processor.stage;

import com.squareup.javapoet.TypeSpec;
import org.dynodao.processor.stage.generate.StageTypeSpecMutators;

import javax.inject.Inject;

import static org.dynodao.processor.util.StringUtil.capitalize;
import static org.dynodao.processor.util.StringUtil.toClassCase;

/**
 * Produces {@link StageTypeSpec} types from their {@link Stage} specifications.
 */
public class StageTypeSpecFactory {

    private final StageTypeSpecMutators stageTypeSpecMutators;

    @Inject StageTypeSpecFactory(StageTypeSpecMutators stageTypeSpecMutators) {
        this.stageTypeSpecMutators = stageTypeSpecMutators;
    }

    /**
     * @param stage the stage class to build
     * @return the built stage
     */
    public StageTypeSpec build(Stage stage) {
        TypeSpec.Builder typeSpec = TypeSpec.classBuilder(getClassName(stage));
        stageTypeSpecMutators.forEach(mutator -> mutator.mutate(typeSpec, stage));
        return new StageTypeSpec(stage, typeSpec.build());
    }

    private String getClassName(Stage stage) {
        if (stage.isStagedBuilder()) {
            return getStagedBuilderClassName(stage);
        } else {
            return getIndexStageClassName(stage);
        }
    }

    private String getStagedBuilderClassName(Stage stage) {
        return stage.getDocumentElement().getSimpleName() + "StagedDynamoBuilder";
    }

    private String getIndexStageClassName(Stage stage) {
        StringBuilder name = new StringBuilder();

        name.append(toClassCase(stage.getDynamoIndex().getName()));

        if (stage.getKeyLengthType().compareTo(KeyLengthType.HASH) >= 0) {
            name.append(capitalize(stage.getDynamoIndex().getHashKey().getElement()));
        }
        if (stage.getKeyLengthType().compareTo(KeyLengthType.RANGE) >= 0) {
            name.append(capitalize(stage.getDynamoIndex().getRangeKey().get().getElement()));
        }

        name.append(stage.getDocumentElement().getSimpleName());
        return name.toString();
    }

}
