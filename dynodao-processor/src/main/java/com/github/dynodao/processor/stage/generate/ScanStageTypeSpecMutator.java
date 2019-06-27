package com.github.dynodao.processor.stage.generate;

import com.github.dynodao.DynoDaoScan;
import com.github.dynodao.internal.ParallelScanReadResult;
import com.github.dynodao.internal.ScanReadResult;
import com.github.dynodao.processor.context.Processors;
import com.github.dynodao.processor.schema.index.IndexType;
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
import static com.github.dynodao.processor.util.DynamoDbUtil.item;
import static com.github.dynodao.processor.util.DynamoDbUtil.scanRequest;
import static com.github.dynodao.processor.util.DynamoDbUtil.scanResult;

/**
 * Implements all methods defined in the {@link DynoDaoScan} interface.
 * If the type being built does not implement the interface, then nothing is added.
 */
class ScanStageTypeSpecMutator implements StageTypeSpecMutator {

    private static final ParameterSpec AMAZON_DYNAMO_DB_PARAMETER = ParameterSpec.builder(amazonDynamoDb(), "amazonDynamoDb").build();
    private static final ParameterSpec TOTAL_SEGMENTS_PARAMETER = ParameterSpec.builder(int.class, "totalSegments").build();

    private final MethodSpec scanWithNoReturnOrBody;
    private final MethodSpec asRequestWithNoBody;
    private final MethodSpec parallelScanWithNoReturnOrBody;
    private final MethodSpec asParallelRequestWithNoBody;

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

        ExecutableElement parallelScan = processors.getMethodByName(interfaceType, "parallelScan");
        parallelScanWithNoReturnOrBody = MethodSpec.methodBuilder(parallelScan.getSimpleName().toString())
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(AMAZON_DYNAMO_DB_PARAMETER)
                .addParameter(TOTAL_SEGMENTS_PARAMETER)
                .build();

        ExecutableElement asParallelRequest = processors.getMethodByName(interfaceType, "asParallelScanRequest");
        asParallelRequestWithNoBody = MethodSpec.methodBuilder(asParallelRequest.getSimpleName().toString())
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.get(asParallelRequest.getReturnType()))
                .addParameter(TOTAL_SEGMENTS_PARAMETER)
                .build();
    }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, Stage stage) {
        if (isScan(stage)) {
            MethodSpec asRequest = buildAsRequest(stage);
            typeSpec.addMethod(asRequest);

            MethodSpec scan = buildScan(stage, asRequest);
            typeSpec.addMethod(scan);

            MethodSpec asParallelRequest = buildAsParallelRequest(stage, asRequest);
            typeSpec.addMethod(asParallelRequest);

            MethodSpec parallelScan = buildParallelScan(stage, asParallelRequest);
            typeSpec.addMethod(parallelScan);
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

        ParameterSpec item = ParameterSpec.builder(item(), "item").build();
        String serializerClassName = stage.getSerializer().getTypeSpec().name;
        String deserializeMethodName = stage.getSchema().getDocument().getItemDeserializationMethod().getMethodName();
        TypeSpec scanResult = TypeSpec.anonymousClassBuilder("$N, request, result", AMAZON_DYNAMO_DB_PARAMETER)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(ScanReadResult.class), documentType))
                .addMethod(MethodSpec.methodBuilder("deserialize")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PROTECTED)
                        .returns(documentType)
                        .addParameter(item)
                        .addStatement("return $L.$L($N)", serializerClassName, deserializeMethodName, item)
                        .build())
                .build();

        return scanWithNoReturnOrBody.toBuilder()
                .returns(ParameterizedTypeName.get(ClassName.get(Stream.class), documentType))
                .addStatement("$T request = $N()", asRequest.returnType, asRequest)
                .addStatement("$T result = $N.scan(request)", scanResult(), AMAZON_DYNAMO_DB_PARAMETER)
                .addStatement("return $L.stream()", scanResult)
                .build();
    }

    private MethodSpec buildAsParallelRequest(Stage stage, MethodSpec asRequest) {
        return asParallelRequestWithNoBody.toBuilder()
                .addStatement("$T request = $N()", scanRequest(), asRequest)
                .addStatement("request.setSegment(0)")
                .addStatement("request.setTotalSegments($N)", TOTAL_SEGMENTS_PARAMETER)
                .addStatement("return request")
                .build();
    }

    private MethodSpec buildParallelScan(Stage stage, MethodSpec asParallelRequest) {
        TypeName documentType = TypeName.get(stage.getSchema().getDocument().getTypeMirror());

        ParameterSpec item = ParameterSpec.builder(item(), "item").build();
        String serializerClassName = stage.getSerializer().getTypeSpec().name;
        String deserializeMethodName = stage.getSchema().getDocument().getItemDeserializationMethod().getMethodName();
        TypeSpec parallelScanResult = TypeSpec.anonymousClassBuilder("$N, request", AMAZON_DYNAMO_DB_PARAMETER)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(ParallelScanReadResult.class), documentType))
                .addMethod(MethodSpec.methodBuilder("deserialize")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PROTECTED)
                        .returns(documentType)
                        .addParameter(item)
                        .addStatement("return $L.$L($N)", serializerClassName, deserializeMethodName, item)
                        .build())
                .build();

        return parallelScanWithNoReturnOrBody.toBuilder()
                .returns(ParameterizedTypeName.get(ClassName.get(Stream.class), documentType))
                .addStatement("$T request = $N($N)", asParallelRequest.returnType, asParallelRequest, TOTAL_SEGMENTS_PARAMETER)
                .addStatement("return $L.stream()", parallelScanResult)
                .build();
    }

}
