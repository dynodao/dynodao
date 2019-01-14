package org.lemon.dynodao.processor.generate;

import static org.lemon.dynodao.processor.util.StringUtil.capitalize;
import static org.lemon.dynodao.processor.util.StringUtil.toClassCase;

import javax.inject.Inject;

import org.lemon.dynodao.processor.index.IndexType;
import org.lemon.dynodao.processor.model.IndexLengthType;
import org.lemon.dynodao.processor.model.PojoClassBuilder;
import org.lemon.dynodao.processor.model.PojoTypeSpec;

import com.squareup.javapoet.TypeSpec;

public class PojoTypeSpecFactory {

    @Inject ModifiersTypeSpecMutator modifiersTypeSpecMutator;
    @Inject SuperInterfaceTypeSpecMutator superInterfaceTypeSpecMutator;
    @Inject FieldTypeSpecMutator fieldTypeSpecMutator;
    @Inject CtorTypeSpecMutator ctorTypeSpecMutator;
    @Inject WitherTypeSpecMutator witherTypeSpecMutator;
    @Inject DocumentLoadTypeSpecMutator documentLoadTypeSpecMutator;
    @Inject DocumentQueryTypeSpecMutator documentQueryTypeSpecMutator;
    @Inject EqualsTypeSpecMutator equalsTypeSpecMutator;
    @Inject HashCodeTypeSpecMutator hashCodeTypeSpecMutator;
    @Inject ToStringTypeSpecMutator toStringTypeSpecMutator;

    @Inject PojoTypeSpecFactory() { }

    public PojoTypeSpec build(PojoClassBuilder pojo) {
        TypeSpec.Builder typeSpec = TypeSpec.classBuilder(getClassName(pojo));
        mutate(typeSpec, pojo);
        return new PojoTypeSpec(pojo, typeSpec.build());
    }

    private String getClassName(PojoClassBuilder pojo) {
        StringBuilder name = new StringBuilder();

        if (pojo.getDynamoIndex().getIndexType().equals(IndexType.TABLE)) {
            name.append("Table");
        } else {
            name.append(toClassCase(pojo.getDynamoIndex().getName()));
        }

        if (pojo.getIndexLengthType().compareTo(IndexLengthType.HASH) >= 0) {
            name.append(capitalize(pojo.getDynamoIndex().getHashKey()));
        }
        if (pojo.getIndexLengthType().compareTo(IndexLengthType.RANGE) >= 0) {
            name.append(capitalize(pojo.getDynamoIndex().getRangeKey().get()));
        }

        name.append(pojo.getDocument().getSimpleName());

        name.append(pojo.getInterfaceType().getInterfaceClass().getSimpleName());
        return name.toString();
    }

    private void mutate(TypeSpec.Builder typeSpec, PojoClassBuilder pojo) {
        modifiersTypeSpecMutator.mutate(typeSpec, pojo);
        superInterfaceTypeSpecMutator.mutate(typeSpec, pojo);
        fieldTypeSpecMutator.mutate(typeSpec, pojo);
        ctorTypeSpecMutator.mutate(typeSpec, pojo);

        witherTypeSpecMutator.mutate(typeSpec, pojo);

        documentLoadTypeSpecMutator.mutate(typeSpec, pojo);
        documentQueryTypeSpecMutator.mutate(typeSpec, pojo);

        equalsTypeSpecMutator.mutate(typeSpec, pojo);
        hashCodeTypeSpecMutator.mutate(typeSpec, pojo);
        toStringTypeSpecMutator.mutate(typeSpec, pojo);
    }
}
