package org.lemon.dynodao.processor.generate;

import static org.lemon.dynodao.processor.util.StringUtil.capitalize;

import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.model.IndexLengthType;
import org.lemon.dynodao.processor.model.PojoClassBuilder;

import javax.inject.Inject;

public class PojoTypeSpecFactory {

    @Inject ModifiersTypeSpecMutator modifiersTypeSpecMutator;
    @Inject SuperInterfaceTypeSpecMutator superInterfaceTypeSpecMutator;
    @Inject FieldTypeSpecMutator fieldTypeSpecMutator;
    @Inject CtorTypeSpecMutator ctorTypeSpecMutator;
    @Inject DocumentLoadTypeSpecMutator documentLoadTypeSpecMutator;
    @Inject DocumentQueryTypeSpecMutator documentQueryTypeSpecMutator;
    @Inject EqualsTypeSpecMutator equalsTypeSpecMutator;
    @Inject HashCodeTypeSpecMutator hashCodeTypeSpecMutator;
    @Inject ToStringTypeSpecMutator toStringTypeSpecMutator;

    @Inject PojoTypeSpecFactory() { }

    public TypeSpec build(PojoClassBuilder pojo) {
        TypeSpec.Builder typeSpec = TypeSpec.classBuilder(getClassName(pojo));
        mutate(typeSpec, pojo);
        return typeSpec.build();
    }

    private String getClassName(PojoClassBuilder pojo) {
        StringBuilder name = new StringBuilder();
        if (pojo.getIndexLengthType().compareTo(IndexLengthType.HASH) >= 0) {
            name.append(capitalize(pojo.getDynamoIndex().get().getHashKey()));
        }
        if (pojo.getIndexLengthType().compareTo(IndexLengthType.RANGE) >= 0) {
            name.append(capitalize(pojo.getDynamoIndex().get().getRangeKey().get()));
        }

        name.append(pojo.getDocument().getSimpleName());

        pojo.getInterfaceType().getInterfaceClass().ifPresent(clazz -> name.append(clazz.getSimpleName()));
        return name.toString();
    }

    private void mutate(TypeSpec.Builder typeSpec, PojoClassBuilder pojo) {
        modifiersTypeSpecMutator.mutate(typeSpec, pojo);
        superInterfaceTypeSpecMutator.mutate(typeSpec, pojo);
        fieldTypeSpecMutator.mutate(typeSpec, pojo);
        ctorTypeSpecMutator.mutate(typeSpec, pojo);

        documentLoadTypeSpecMutator.mutate(typeSpec, pojo);
        documentQueryTypeSpecMutator.mutate(typeSpec, pojo);

        equalsTypeSpecMutator.mutate(typeSpec, pojo);
        hashCodeTypeSpecMutator.mutate(typeSpec, pojo);
        toStringTypeSpecMutator.mutate(typeSpec, pojo);
    }
}
