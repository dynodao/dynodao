package org.lemon.dynodao.processor.generate;

import static org.lemon.dynodao.processor.util.StringUtil.capitalize;
import static org.lemon.dynodao.processor.util.StringUtil.toClassCase;

import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.model.IndexLengthType;
import org.lemon.dynodao.processor.model.PojoClassBuilder;
import org.lemon.dynodao.processor.model.PojoTypeSpec;

import javax.inject.Inject;

/**
 * Produces {@link PojoTypeSpec} types from their {@link PojoClassBuilder} specifications.
 */
public class PojoTypeSpecFactory {

    @Inject GeneratedAnnotationTypeSpecMutator generatedAnnotationTypeSpecMutator;
    @Inject ModifiersTypeSpecMutator modifiersTypeSpecMutator;
    @Inject SuperInterfaceTypeSpecMutator superInterfaceTypeSpecMutator;
    @Inject FieldTypeSpecMutator fieldTypeSpecMutator;
    @Inject CtorTypeSpecMutator ctorTypeSpecMutator;
    @Inject WitherTypeSpecMutator witherTypeSpecMutator;
    @Inject UserTypeSpecMutator userTypeSpecMutator;
    @Inject DocumentLoadTypeSpecMutator documentLoadTypeSpecMutator;
    @Inject DocumentQueryTypeSpecMutator documentQueryTypeSpecMutator;
    @Inject EqualsTypeSpecMutator equalsTypeSpecMutator;
    @Inject HashCodeTypeSpecMutator hashCodeTypeSpecMutator;
    @Inject ToStringTypeSpecMutator toStringTypeSpecMutator;

    @Inject PojoTypeSpecFactory() { }

    /**
     * @param pojo the pojo to build
     * @return the built pojo
     */
    public PojoTypeSpec build(PojoClassBuilder pojo) {
        TypeSpec.Builder typeSpec = TypeSpec.classBuilder(getClassName(pojo));
        mutate(typeSpec, pojo);
        return new PojoTypeSpec(pojo, typeSpec.build());
    }

    private String getClassName(PojoClassBuilder pojo) {
        if (pojo.getDynamoIndex() == null) {
            return getStagedBuilderClassName(pojo);
        } else {
            return getIndexPojoClassName(pojo);
        }
    }

    private String getStagedBuilderClassName(PojoClassBuilder pojo) {
        return pojo.getDocument().getSimpleName() + "StagedDynamoBuilder";
    }

    private String getIndexPojoClassName(PojoClassBuilder pojo) {
        StringBuilder name = new StringBuilder();

        name.append(toClassCase(pojo.getDynamoIndex().getName()));

        if (pojo.getIndexLengthType().compareTo(IndexLengthType.HASH) >= 0) {
            name.append(capitalize(pojo.getDynamoIndex().getHashKeyAttribute().getField()));
        }
        if (pojo.getIndexLengthType().compareTo(IndexLengthType.RANGE) >= 0) {
            name.append(capitalize(pojo.getDynamoIndex().getRangeKeyAttribute().get().getField()));
        }

        name.append(pojo.getDocument().getSimpleName());

        pojo.getInterfaceType().getInterfaceClass().ifPresent(clazz -> name.append(clazz.getSimpleName()));
        return name.toString();
    }

    private void mutate(TypeSpec.Builder typeSpec, PojoClassBuilder pojo) {
        generatedAnnotationTypeSpecMutator.mutate(typeSpec, pojo);
        modifiersTypeSpecMutator.mutate(typeSpec, pojo);
        superInterfaceTypeSpecMutator.mutate(typeSpec, pojo);
        fieldTypeSpecMutator.mutate(typeSpec, pojo);
        ctorTypeSpecMutator.mutate(typeSpec, pojo);

        witherTypeSpecMutator.mutate(typeSpec, pojo);
        userTypeSpecMutator.mutate(typeSpec, pojo);

        documentLoadTypeSpecMutator.mutate(typeSpec, pojo);
        documentQueryTypeSpecMutator.mutate(typeSpec, pojo);

        equalsTypeSpecMutator.mutate(typeSpec, pojo);
        hashCodeTypeSpecMutator.mutate(typeSpec, pojo);
        toStringTypeSpecMutator.mutate(typeSpec, pojo);
    }

}
