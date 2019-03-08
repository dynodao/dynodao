package org.lemon.dynodao.processor.node.generate;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.internal.QueryReadResult;
import org.lemon.dynodao.processor.context.Processors;
import org.lemon.dynodao.processor.node.InterfaceType;
import org.lemon.dynodao.processor.node.NodeClassData;
import org.lemon.dynodao.processor.schema.attribute.DynamoAttribute;
import org.lemon.dynodao.processor.schema.index.IndexType;

import javax.inject.Inject;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.amazonDynamoDb;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.attributeValue;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.queryRequest;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.queryResult;

/**
 * Implements all methods defined in the {@link org.lemon.dynodao.DynoDaoQuery} interface.
 * If the type being built does not implement the interface, then nothing is added.
 */
class QueryNodeTypeSpecMutator implements NodeTypeSpecMutator {

    private static final ParameterSpec AMAZON_DYNAMO_DB_PARAMETER = ParameterSpec.builder(amazonDynamoDb(), "amazonDynamoDb").build();

    private final MethodSpec queryWithNoReturnOrBody;
    private final MethodSpec asRequestWithNoBody;

    @Inject QueryNodeTypeSpecMutator(Processors processors) {
        TypeElement interfaceType = processors.getTypeElement(InterfaceType.QUERY.getInterfaceClass().get());
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
    public void mutate(TypeSpec.Builder typeSpec, NodeClassData node) {
        if (isQuery(node)) {
            MethodSpec asRequest = buildAsRequest(node);
            typeSpec.addMethod(asRequest);

            MethodSpec load = buildQuery(node, asRequest);
            typeSpec.addMethod(load);
        }
    }

    private boolean isQuery(NodeClassData node) {
        return node.getInterfaceType().equals(InterfaceType.QUERY);
    }

    private MethodSpec buildAsRequest(NodeClassData node) {
        MethodSpec.Builder asRequest = asRequestWithNoBody.toBuilder()
                .addStatement("$1T request = new $1T()", queryRequest());

        appendTableName(asRequest, node);
        appendIndexName(asRequest, node);
        appendKeyCondition(asRequest, node);
        appendExpressionAttributeNames(asRequest, node);
        appendExpressionAttributeValues(asRequest, node);
        asRequest.addStatement("return request");
        return asRequest.build();
    }

    private void appendTableName(MethodSpec.Builder asRequest, NodeClassData node) {
        asRequest.addStatement("request.setTableName($S)", node.getSchema().getTableName());
    }

    private void appendIndexName(MethodSpec.Builder asRequest, NodeClassData node) {
        if (!node.getDynamoIndex().getIndexType().equals(IndexType.TABLE)) {
            asRequest.addStatement("request.setIndexName($S)", node.getDynamoIndex().getName());
        }
    }

    private void appendKeyCondition(MethodSpec.Builder asRequest, NodeClassData node) {
        String expression = node.getAttributesAsFields().stream()
                .map(field -> String.format("#%s = :%s", field.name, field.name))
                .collect(joining(" AND "));
        asRequest.addStatement("request.setKeyConditionExpression($S)", expression);
    }

    private void appendExpressionAttributeNames(MethodSpec.Builder asRequest, NodeClassData node) {
        for (DynamoAttribute attribute : node.getAttributes()) {
            asRequest.addStatement("request.addExpressionAttributeNamesEntry($S, $S)", "#" + attribute.asFieldSpec().name, attribute.getPath());
        }
    }

    private void appendExpressionAttributeValues(MethodSpec.Builder asRequest, NodeClassData node) {
        String serializerClassName = node.getSerializer().getTypeSpec().name;
        for (DynamoAttribute attribute : node.getAttributes()) {
            String serializeMethodName = attribute.getSerializationMethod().getMethodName();
            FieldSpec field = attribute.asFieldSpec();
            asRequest.addStatement("request.addExpressionAttributeValuesEntry($S, $L.$L($N))", ":" + field.name, serializerClassName,
                    serializeMethodName, field);
        }
    }

    private MethodSpec buildQuery(NodeClassData node, MethodSpec asRequest) {
        TypeName documentType = TypeName.get(node.getSchema().getDocument().getTypeMirror());

        ParameterSpec attributeValue = ParameterSpec.builder(attributeValue(), "attributeValue").build();
        String serializerClassName = node.getSerializer().getTypeSpec().name;
        String deserializeMethodName = node.getSchema().getDocument().getDeserializationMethod().getMethodName();
        TypeSpec queryResult = TypeSpec.anonymousClassBuilder("$N, request, result", AMAZON_DYNAMO_DB_PARAMETER)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(QueryReadResult.class), documentType))
                .addMethod(MethodSpec.methodBuilder("deserialize")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PROTECTED)
                        .returns(documentType)
                        .addParameter(attributeValue)
                        .addStatement("return $L.$L($N)", serializerClassName, deserializeMethodName, attributeValue)
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
