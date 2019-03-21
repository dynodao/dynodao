package org.dynodao.processor.stage.generate;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.dynodao.DynoDaoQuery;
import org.dynodao.internal.QueryReadResult;
import org.dynodao.processor.context.Processors;
import org.dynodao.processor.schema.attribute.DynamoAttribute;
import org.dynodao.processor.schema.index.IndexType;
import org.dynodao.processor.stage.InterfaceType;
import org.dynodao.processor.stage.Stage;

import javax.inject.Inject;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static org.dynodao.processor.util.DynamoDbUtil.amazonDynamoDb;
import static org.dynodao.processor.util.DynamoDbUtil.item;
import static org.dynodao.processor.util.DynamoDbUtil.queryRequest;
import static org.dynodao.processor.util.DynamoDbUtil.queryResult;

/**
 * Implements all methods defined in the {@link DynoDaoQuery} interface.
 * If the type being built does not implement the interface, then nothing is added.
 */
class QueryStageTypeSpecMutator implements StageTypeSpecMutator {

    private static final ParameterSpec AMAZON_DYNAMO_DB_PARAMETER = ParameterSpec.builder(amazonDynamoDb(), "amazonDynamoDb").build();

    private final MethodSpec queryWithNoReturnOrBody;
    private final MethodSpec asRequestWithNoBody;

    @Inject QueryStageTypeSpecMutator(Processors processors) {
        TypeElement interfaceType = processors.getTypeElement(InterfaceType.QUERY.getInterfaceClass());
        ExecutableElement query = processors.getMethodByName(interfaceType, "query");
        queryWithNoReturnOrBody = MethodSpec.methodBuilder(query.getSimpleName().toString())
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(AMAZON_DYNAMO_DB_PARAMETER)
                .build();

        ExecutableElement asRequest = processors.getMethodByName(interfaceType, "asQueryRequest");
        asRequestWithNoBody = MethodSpec.overriding(asRequest).build();
    }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, Stage stage) {
        if (isQuery(stage)) {
            MethodSpec asRequest = buildAsRequest(stage);
            typeSpec.addMethod(asRequest);

            MethodSpec query = buildQuery(stage, asRequest);
            typeSpec.addMethod(query);
        }
    }

    private boolean isQuery(Stage stage) {
        return stage.getInterfaceTypes().contains(InterfaceType.QUERY);
    }

    private MethodSpec buildAsRequest(Stage stage) {
        MethodSpec.Builder asRequest = asRequestWithNoBody.toBuilder()
                .addStatement("$1T request = new $1T()", queryRequest());

        appendTableName(asRequest, stage);
        appendIndexName(asRequest, stage);
        appendKeyCondition(asRequest, stage);
        appendExpressionAttributeNames(asRequest, stage);
        appendExpressionAttributeValues(asRequest, stage);
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

    private void appendKeyCondition(MethodSpec.Builder asRequest, Stage stage) {
        String expression = stage.getAttributesAsFields().stream()
                .map(field -> String.format("#%s = :%s", field.name, field.name))
                .collect(joining(" AND "));
        asRequest.addStatement("request.setKeyConditionExpression($S)", expression);
    }

    private void appendExpressionAttributeNames(MethodSpec.Builder asRequest, Stage stage) {
        for (DynamoAttribute attribute : stage.getAttributes()) {
            asRequest.addStatement("request.addExpressionAttributeNamesEntry($S, $S)", "#" + attribute.asFieldSpec().name, attribute.getPath());
        }
    }

    private void appendExpressionAttributeValues(MethodSpec.Builder asRequest, Stage stage) {
        String serializerClassName = stage.getSerializer().getTypeSpec().name;
        for (DynamoAttribute attribute : stage.getAttributes()) {
            String serializeMethodName = attribute.getSerializationMethod().getMethodName();
            FieldSpec field = attribute.asFieldSpec();
            asRequest.addStatement("request.addExpressionAttributeValuesEntry($S, $L.$L($N))", ":" + field.name, serializerClassName,
                    serializeMethodName, field);
        }
    }

    private MethodSpec buildQuery(Stage stage, MethodSpec asRequest) {
        TypeName documentType = TypeName.get(stage.getSchema().getDocument().getTypeMirror());

        ParameterSpec item = ParameterSpec.builder(item(), "item").build();
        String serializerClassName = stage.getSerializer().getTypeSpec().name;
        String deserializeMethodName = stage.getSchema().getDocument().getItemDeserializationMethod().getMethodName();
        TypeSpec queryResult = TypeSpec.anonymousClassBuilder("$N, request, result", AMAZON_DYNAMO_DB_PARAMETER)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(QueryReadResult.class), documentType))
                .addMethod(MethodSpec.methodBuilder("deserialize")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PROTECTED)
                        .returns(documentType)
                        .addParameter(item)
                        .addStatement("return $L.$L($N)", serializerClassName, deserializeMethodName, item)
                        .build())
                .build();

        return queryWithNoReturnOrBody.toBuilder()
                .returns(ParameterizedTypeName.get(ClassName.get(Stream.class), documentType))
                .addStatement("$T request = $N()", asRequest.returnType, asRequest)
                .addStatement("$T result = $N.query(request)", queryResult(), AMAZON_DYNAMO_DB_PARAMETER)
                .addStatement("return $L.stream()", queryResult)
                .build();
    }

}
