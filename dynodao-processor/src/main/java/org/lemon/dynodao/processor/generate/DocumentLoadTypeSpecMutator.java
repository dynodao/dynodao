package org.lemon.dynodao.processor.generate;

import static org.lemon.dynodao.processor.util.DynamoDbUtil.dynamoDbMapper;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.context.ProcessorContext;
import org.lemon.dynodao.processor.model.IndexLengthType;
import org.lemon.dynodao.processor.model.InterfaceType;
import org.lemon.dynodao.processor.model.PojoClassBuilder;

import javax.inject.Inject;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.Iterator;

/**
 * Implements the {@link org.lemon.dynodao.DocumentLoad#load(DynamoDBMapper)} method. If the type does not implement
 * {@link org.lemon.dynodao.DocumentLoad}, then nothing is added.
 */
class DocumentLoadTypeSpecMutator implements TypeSpecMutator {

    @Inject ProcessorContext processorContext;

    private MethodSpec loadWithNoReturnOrBody;
    private ParameterSpec dynamoDbMapperParam;

    @Inject DocumentLoadTypeSpecMutator() { }

    @Inject void init() {
        dynamoDbMapperParam = ParameterSpec.builder(dynamoDbMapper(), "dynamoDbMapper").build();

        TypeElement interfaceType = processorContext.getElementUtils().getTypeElement(InterfaceType.DOCUMENT_LOAD.getInterfaceClass().get().getCanonicalName());
        ExecutableElement method = (ExecutableElement) interfaceType.getEnclosedElements().iterator().next();
        loadWithNoReturnOrBody = MethodSpec.methodBuilder(method.getSimpleName().toString())
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(dynamoDbMapperParam)
                .build();
    }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, PojoClassBuilder pojo) {
        if (isDocumentLoad(pojo)) {
            MethodSpec load = buildLoad(pojo);
            typeSpec.addMethod(load);
        }
    }

    private boolean isDocumentLoad(PojoClassBuilder pojo) {
        return pojo.getInterfaceType().equals(InterfaceType.DOCUMENT_LOAD);
    }

    private MethodSpec buildLoad(PojoClassBuilder pojo) {
        MethodSpec.Builder load = loadWithNoReturnOrBody.toBuilder()
                .returns(TypeName.get(pojo.getDocument().asType()));

        Iterator<FieldSpec> fields = pojo.getFields().iterator();
        if (pojo.getIndexLengthType().equals(IndexLengthType.HASH)) {
            load.addStatement("return $N.load($T.class, $N)", dynamoDbMapperParam, pojo.getDocument().asType(), fields.next());
        } else {
            load.addStatement("return $N.load($T.class, $N, $N)", dynamoDbMapperParam, pojo.getDocument().asType(), fields.next(), fields.next());
        }

        return load.build();
    }

}
