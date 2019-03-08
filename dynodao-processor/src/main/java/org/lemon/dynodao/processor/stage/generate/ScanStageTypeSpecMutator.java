package org.lemon.dynodao.processor.stage.generate;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.internal.ScanReadResult;
import org.lemon.dynodao.processor.context.Processors;
import org.lemon.dynodao.processor.schema.index.IndexType;
import org.lemon.dynodao.processor.stage.InterfaceType;
import org.lemon.dynodao.processor.stage.Stage;

import javax.inject.Inject;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.stream.Stream;

import static org.lemon.dynodao.processor.util.DynamoDbUtil.amazonDynamoDb;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.attributeValue;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.scanRequest;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.scanResult;

/**
 * Implements all methods defined in the {@link org.lemon.dynodao.DynoDaoScan} interface.
 * If the type being built does not implement the interface, then nothing is added.
 */
class ScanStageTypeSpecMutator implements StageTypeSpecMutator {

    private static final ParameterSpec AMAZON_DYNAMO_DB_PARAMETER = ParameterSpec.builder(amazonDynamoDb(), "amazonDynamoDb").build();

    private final MethodSpec scanWithNoReturnOrBody;
    private final MethodSpec asRequestWithNoBody;

    @Inject
    ScanStageTypeSpecMutator(Processors processors) {
        TypeElement interfaceType = processors.getTypeElement(InterfaceType.SCAN.getInterfaceClass());
        ExecutableElement scan = processors.getMethodByName(interfaceType, "scan");
        scanWithNoReturnOrBody = MethodSpec.methodBuilder(scan.getSimpleName().toString())
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(AMAZON_DYNAMO_DB_PARAMETER)
                .build();

        ExecutableElement asRequest = processors.getMethodByName(interfaceType, "asScanRequest");
        asRequestWithNoBody = MethodSpec.overriding(asRequest).build();
    }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, Stage stage) {
        if (isScan(stage)) {
            MethodSpec asRequest = buildAsRequest(stage);
            typeSpec.addMethod(asRequest);

            MethodSpec scan = buildScan(stage, asRequest);
            typeSpec.addMethod(scan);
        }
    }

    private boolean isScan(Stage stage) {
        return stage.getInterfaceTypes().contains(InterfaceType.SCAN);
    }

    private MethodSpec buildAsRequest(Stage stage) {
        MethodSpec.Builder asRequest = asRequestWithNoBody.toBuilder()
                .addStatement("$1T request = new $1T()", scanRequest());

        appendTableName(asRequest, stage);
        appendIndexName(asRequest, stage);
        asRequest.addStatement("return request");
        return asRequest.build();
    }

    private void appendTableName(MethodSpec.Builder asRequest, Stage stage) {
        asRequest.addStatement("request.setTableName($S)", stage.getSchema().getTableName());
    }

    private void appendIndexName(MethodSpec.Builder asRequest, Stage stage) {
        if (!stage.getDynamoIndex().getIndexType().equals(IndexType.TABLE)) {
            asRequest.addStatement("request.setIndexName($S)", stage.getDynamoIndex().getName());
        }
    }

    private MethodSpec buildScan(Stage stage, MethodSpec asRequest) {
        TypeName documentType = TypeName.get(stage.getSchema().getDocument().getTypeMirror());

        ParameterSpec attributeValue = ParameterSpec.builder(attributeValue(), "attributeValue").build();
        String serializerClassName = stage.getSerializer().getTypeSpec().name;
        String deserializeMethodName = stage.getSchema().getDocument().getDeserializationMethod().getMethodName();
        TypeSpec scanResult = TypeSpec.anonymousClassBuilder("$N, request, result", AMAZON_DYNAMO_DB_PARAMETER)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(ScanReadResult.class), documentType))
                .addMethod(MethodSpec.methodBuilder("deserialize")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PROTECTED)
                        .returns(documentType)
                        .addParameter(attributeValue)
                        .addStatement("return $L.$L($N)", serializerClassName, deserializeMethodName, attributeValue)
                        .build())
                .build();

        return scanWithNoReturnOrBody.toBuilder()
                .returns(ParameterizedTypeName.get(ClassName.get(Stream.class), documentType))
                .addStatement("$T request = $N()", asRequest.returnType, asRequest)
                .addStatement("$T result = $N.scan(request)", scanResult(), AMAZON_DYNAMO_DB_PARAMETER)
                .addStatement("return $L.stream()", scanResult)
                .build();
    }

}
