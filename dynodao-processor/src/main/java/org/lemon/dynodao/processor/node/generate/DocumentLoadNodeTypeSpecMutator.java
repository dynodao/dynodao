package org.lemon.dynodao.processor.node.generate;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.context.Processors;
import org.lemon.dynodao.processor.node.InterfaceType;
import org.lemon.dynodao.processor.node.NodeClassData;

import javax.inject.Inject;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.Collections;
import java.util.List;

import static org.lemon.dynodao.processor.util.DynamoDbUtil.dynamoDbMapper;
import static org.lemon.dynodao.processor.util.StreamUtil.concat;
import static org.lemon.dynodao.processor.util.StringUtil.repeat;

/**
 * Implements the {@link org.lemon.dynodao.DocumentLoad#load(DynamoDBMapper)} method. If the type does not implement
 * {@link org.lemon.dynodao.DocumentLoad}, then nothing is added.
 */
class DocumentLoadNodeTypeSpecMutator implements NodeTypeSpecMutator {

    @Inject Processors processors;

    private MethodSpec loadWithNoReturnOrBody;
    private ParameterSpec dynamoDbMapperParam;

    @Inject DocumentLoadNodeTypeSpecMutator() { }

    @Inject void init() {
        dynamoDbMapperParam = ParameterSpec.builder(dynamoDbMapper(), "dynamoDbMapper").build();

        TypeElement interfaceType = processors.getTypeElement(InterfaceType.DOCUMENT_LOAD.getInterfaceClass().get());
        ExecutableElement method = (ExecutableElement) interfaceType.getEnclosedElements().iterator().next();
        loadWithNoReturnOrBody = MethodSpec.methodBuilder(method.getSimpleName().toString())
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(dynamoDbMapperParam)
                .build();
    }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, NodeClassData node) {
        if (isDocumentLoad(node)) {
            MethodSpec load = buildLoad(node);
            typeSpec.addMethod(load);
        }
    }

    private boolean isDocumentLoad(NodeClassData node) {
        return node.getInterfaceType().equals(InterfaceType.DOCUMENT_LOAD);
    }

    private MethodSpec buildLoad(NodeClassData node) {
        List<FieldSpec> fields = node.getAttributesAsFields();
        String argsFormat = repeat(fields.size(), "$N", ", ");
        Object[] args = concat(Collections.class, dynamoDbMapperParam, node.getDocument().asType(), fields).toArray();

        return loadWithNoReturnOrBody.toBuilder()
                .returns(ParameterizedTypeName.get(ClassName.get(List.class), TypeName.get(node.getDocument().asType())))
                .addStatement("return $T.singletonList($N.load($T.class, " + argsFormat + "))", args)
                .build();
    }

}
