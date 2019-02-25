package org.lemon.dynodao.processor.node.generate;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.internal.GetItemReadResult;
import org.lemon.dynodao.processor.context.Processors;
import org.lemon.dynodao.processor.node.InterfaceType;
import org.lemon.dynodao.processor.node.NodeClassData;
import org.lemon.dynodao.processor.schema.attribute.DynamoAttribute;

import javax.inject.Inject;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.stream.Stream;

import static org.lemon.dynodao.processor.util.DynamoDbUtil.amazonDynamoDb;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.attributeValue;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.getItemRequest;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.getItemResult;

/**
 * Implements all methods defined in the {@link org.lemon.dynodao.DynoDaoLoad} interface.
 * If the type being built does not implement the interface, then nothing is added.
 */
class DocumentLoadNodeTypeSpecMutator implements NodeTypeSpecMutator {

    private static final ParameterSpec AMAZON_DYNAMO_DB_PARAMETER = ParameterSpec.builder(amazonDynamoDb(), "amazonDynamoDb").build();

    private final MethodSpec loadWithNoReturnOrBody;
    private final MethodSpec asRequestWithNoBody;

    @Inject DocumentLoadNodeTypeSpecMutator(Processors processors) {
        TypeElement interfaceType = processors.getTypeElement(InterfaceType.LOAD.getInterfaceClass().get());
        ExecutableElement load = processors.getMethodByName(interfaceType, "load");
        loadWithNoReturnOrBody = MethodSpec.methodBuilder(load.getSimpleName().toString())
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(AMAZON_DYNAMO_DB_PARAMETER)
                .build();

        ExecutableElement asRequest = processors.getMethodByName(interfaceType, "asRequest");
        asRequestWithNoBody = MethodSpec.overriding(asRequest).build();
    }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, NodeClassData node) {
        if (isDocumentLoad(node)) {
            MethodSpec asRequest = buildAsRequest(node);
            typeSpec.addMethod(asRequest);

            MethodSpec load = buildLoad(node, asRequest);
            typeSpec.addMethod(load);
        }
    }

    private boolean isDocumentLoad(NodeClassData node) {
        return node.getInterfaceType().equals(InterfaceType.LOAD);
    }

    private MethodSpec buildAsRequest(NodeClassData node) {
        MethodSpec.Builder asRequest = asRequestWithNoBody.toBuilder()
                .addStatement("$1T request = new $1T()", getItemRequest())
                .addStatement("request.setTableName($S)", node.getSchema().getTableName());

        String serializerClassName = node.getSerializer().getTypeSpec().name;
        for (DynamoAttribute attribute : node.getAttributes()) {
            String serializeMethodName = attribute.getSerializationMethod().getMethodName();
            asRequest.addStatement("request.addKeyEntry($S, $L.$L($N))", attribute.getPath(), serializerClassName, serializeMethodName, attribute.asFieldSpec());
        }

        return asRequest
                .addStatement("return request")
                .build();
    }

    private MethodSpec buildLoad(NodeClassData node, MethodSpec asRequest) {
        TypeName documentType = TypeName.get(node.getSchema().getDocument().getTypeMirror());

        ParameterSpec attributeValue = ParameterSpec.builder(attributeValue(), "attributeValue").build();
        String serializerClassName = node.getSerializer().getTypeSpec().name;
        String deserializeMethodName = node.getSchema().getDocument().getDeserializationMethod().getMethodName();
        TypeSpec getItemResult = TypeSpec.anonymousClassBuilder("result")
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(GetItemReadResult.class), documentType))
                .addMethod(MethodSpec.methodBuilder("deserialize")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PROTECTED)
                        .returns(documentType)
                        .addParameter(attributeValue)
                        .addStatement("return $L.$L($N)", serializerClassName, deserializeMethodName, attributeValue)
                        .build())
                .build();

        return loadWithNoReturnOrBody.toBuilder()
                .returns(ParameterizedTypeName.get(ClassName.get(Stream.class), documentType))
                .addStatement("$T result = $N.getItem($N())", getItemResult(), AMAZON_DYNAMO_DB_PARAMETER, asRequest)
                .addStatement("return $L.stream()", getItemResult)
                .build();
    }

}
