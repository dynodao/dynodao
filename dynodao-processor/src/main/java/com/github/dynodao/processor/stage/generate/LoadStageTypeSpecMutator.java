package com.github.dynodao.processor.stage.generate;

import com.github.dynodao.DynoDaoLoad;
import com.github.dynodao.internal.GetItemReadResult;
import com.github.dynodao.processor.context.Processors;
import com.github.dynodao.processor.schema.attribute.DynamoAttribute;
import com.github.dynodao.processor.stage.InterfaceType;
import com.github.dynodao.processor.stage.Stage;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.inject.Inject;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.stream.Stream;

import static com.github.dynodao.processor.util.DynamoDbUtil.amazonDynamoDb;
import static com.github.dynodao.processor.util.DynamoDbUtil.getItemRequest;
import static com.github.dynodao.processor.util.DynamoDbUtil.getItemResult;
import static com.github.dynodao.processor.util.DynamoDbUtil.item;

/**
 * Implements all methods defined in the {@link DynoDaoLoad} interface.
 * If the type being built does not implement the interface, then nothing is added.
 */
class LoadStageTypeSpecMutator implements StageTypeSpecMutator {

    private static final ParameterSpec AMAZON_DYNAMO_DB_PARAMETER = ParameterSpec.builder(amazonDynamoDb(), "amazonDynamoDb").build();

    private final MethodSpec loadWithNoReturnOrBody;
    private final MethodSpec asRequestWithNoBody;

    @Inject LoadStageTypeSpecMutator(Processors processors) {
        TypeElement interfaceType = processors.getTypeElement(InterfaceType.LOAD.getInterfaceClass());
        ExecutableElement load = processors.getMethodByName(interfaceType, "load");
        loadWithNoReturnOrBody = MethodSpec.methodBuilder(load.getSimpleName().toString())
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(AMAZON_DYNAMO_DB_PARAMETER)
                .build();

        ExecutableElement asRequest = processors.getMethodByName(interfaceType, "asGetItemRequest");
        asRequestWithNoBody = MethodSpec.overriding(asRequest).build();
    }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, Stage stage) {
        if (isLoad(stage)) {
            MethodSpec asRequest = buildAsRequest(stage);
            typeSpec.addMethod(asRequest);

            MethodSpec load = buildLoad(stage, asRequest);
            typeSpec.addMethod(load);
        }
    }

    private boolean isLoad(Stage stage) {
        return stage.getInterfaceTypes().contains(InterfaceType.LOAD);
    }

    private MethodSpec buildAsRequest(Stage stage) {
        MethodSpec.Builder asRequest = asRequestWithNoBody.toBuilder()
                .addStatement("$1T request = new $1T()", getItemRequest())
                .addStatement("request.setTableName($S)", stage.getSchema().getTableName());

        String serializerClassName = stage.getSerializer().getTypeSpec().name;
        for (DynamoAttribute attribute : stage.getAttributes()) {
            String serializeMethodName = attribute.getSerializationMethod().getMethodName();
            asRequest.addStatement("request.addKeyEntry($S, $L.$L($N))", attribute.getPath(), serializerClassName, serializeMethodName, attribute.asFieldSpec());
        }

        return asRequest
                .addStatement("return request")
                .build();
    }

    private MethodSpec buildLoad(Stage stage, MethodSpec asRequest) {
        TypeName documentType = TypeName.get(stage.getSchema().getDocument().getTypeMirror());

        ParameterSpec item = ParameterSpec.builder(item(), "item").build();
        String serializerClassName = stage.getSerializer().getTypeSpec().name;
        String deserializeMethodName = stage.getSchema().getDocument().getItemDeserializationMethod().getMethodName();
        TypeSpec getItemResult = TypeSpec.anonymousClassBuilder("result")
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(GetItemReadResult.class), documentType))
                .addMethod(MethodSpec.methodBuilder("deserialize")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PROTECTED)
                        .returns(documentType)
                        .addParameter(item)
                        .addStatement("return $L.$L($N)", serializerClassName, deserializeMethodName, item)
                        .build())
                .build();

        return loadWithNoReturnOrBody.toBuilder()
                .returns(ParameterizedTypeName.get(ClassName.get(Stream.class), documentType))
                .addStatement("$T result = $N.getItem($N())", getItemResult(), AMAZON_DYNAMO_DB_PARAMETER, asRequest)
                .addStatement("return $L.stream()", getItemResult)
                .build();
    }

}
