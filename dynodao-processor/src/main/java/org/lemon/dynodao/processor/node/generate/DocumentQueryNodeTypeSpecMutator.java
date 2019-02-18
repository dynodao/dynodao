package org.lemon.dynodao.processor.node.generate;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.lemon.dynodao.processor.context.Processors;
import org.lemon.dynodao.processor.node.InterfaceType;
import org.lemon.dynodao.processor.node.NodeClassData;
import org.lemon.dynodao.processor.schema.attribute.DynamoAttribute;
import org.lemon.dynodao.processor.schema.index.IndexType;
import org.lemon.dynodao.processor.schema.serialize.MappingMethod;

import javax.inject.Inject;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import static java.util.stream.Collectors.joining;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.dynamoDbMapper;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.dynamoDbQueryExpression;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.paginatedList;

/**
 * Implements the {@link org.lemon.dynodao.DocumentQuery#query(com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper)} method.
 * If the type does not implement {@link org.lemon.dynodao.DocumentQuery}, then nothing is added.
 */
class DocumentQueryNodeTypeSpecMutator implements NodeTypeSpecMutator {

    private static final ParameterSpec DYNAMO_DB_MAPPER_PARAM = ParameterSpec.builder(dynamoDbMapper(), "dynamoDbMapper").build();

    private final MethodSpec queryWithNoReturnOrBody;

    @Inject DocumentQueryNodeTypeSpecMutator(Processors processors) {
        TypeElement interfaceType = processors.getTypeElement(InterfaceType.DOCUMENT_QUERY.getInterfaceClass().get());
        ExecutableElement method = (ExecutableElement) interfaceType.getEnclosedElements().iterator().next();
        queryWithNoReturnOrBody = MethodSpec.methodBuilder(method.getSimpleName().toString())
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(DYNAMO_DB_MAPPER_PARAM)
                .build();
    }

    @Override
    public void mutate(TypeSpec.Builder typeSpec, NodeClassData node) {
        if (isDocumentQuery(node)) {
            MethodSpec query = buildQuery(node);
            typeSpec.addMethod(query);
        }
    }

    private boolean isDocumentQuery(NodeClassData node) {
        return node.getInterfaceType().equals(InterfaceType.DOCUMENT_QUERY);
    }

    private MethodSpec buildQuery(NodeClassData node) {
        MethodSpec.Builder query = queryWithNoReturnOrBody.toBuilder()
                .returns(paginatedList(node.getDocumentElement()));

        TypeName queryExpression = dynamoDbQueryExpression(node.getDocumentElement());

        query.addStatement("$T query = new $T()", queryExpression, queryExpression);
        appendIndexName(query, node);
        appendKeyConditionExpression(query, node);
        appendExpressionAttributeNames(query, node);
        appendExpressionAttributeValues(query, node);
        query.addStatement("return $N.query($T.class, query)", DYNAMO_DB_MAPPER_PARAM, node.getDocumentElement().asType());
        return query.build();
    }

    private void appendIndexName(MethodSpec.Builder query, NodeClassData node) {
        if (!node.getDynamoIndex().getIndexType().equals(IndexType.TABLE)) {
            query.addStatement("query.setIndexName($S)", node.getDynamoIndex().getName());
        }
    }

    private void appendKeyConditionExpression(MethodSpec.Builder query, NodeClassData node) {
        String expression = node.getAttributesAsFields().stream()
                .map(field -> String.format("#%s = :%s", field.name, field.name))
                .collect(joining(" AND "));
        query.addStatement("query.setKeyConditionExpression($S)", expression);
    }

    private void appendExpressionAttributeNames(MethodSpec.Builder query, NodeClassData node) {
        node.getAttributesAsFields().forEach(field -> query.addStatement("query.addExpressionAttributeNamesEntry($S, $S)", "#" + field.name, field.name));
    }

    private void appendExpressionAttributeValues(MethodSpec.Builder query, NodeClassData node) {
        String serializerClass = node.getSerializer().getTypeSpec().name;
        for (DynamoAttribute attribute : node.getAttributes()) {
            MappingMethod method = attribute.getSerializationMethod();
            FieldSpec field = attribute.asFieldSpec();
            query.addStatement("query.addExpressionAttributeValuesEntry($S, $L.$L($N))", ":" + field.name, serializerClass, method.getMethodName(), field);
        }
    }

}
