package org.lemon.dynodao.processor.node.generate;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeSpec.Builder;
import org.lemon.dynodao.internal.GetItemReadResult;
import org.lemon.dynodao.processor.context.Processors;
import org.lemon.dynodao.processor.node.InterfaceType;
import org.lemon.dynodao.processor.node.NodeClassData;
import org.lemon.dynodao.processor.schema.index.DynamoIndex;

import javax.inject.Inject;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.amazonDynamoDb;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.attributeDefinition;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.attributeValue;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.createTableRequest;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.getItemResult;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.globalSecondaryIndex;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.keySchemaElement;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.keyType;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.localSecondaryIndex;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.projection;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.projectionType;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.provisionedThroughput;
import static org.lemon.dynodao.processor.util.DynamoDbUtil.scalarAttributeType;
import static org.lemon.dynodao.processor.util.StreamUtil.concat;
import static org.lemon.dynodao.processor.util.StringUtil.repeat;
import static org.lemon.dynodao.processor.util.StringUtil.toClassCase;
import static org.lemon.dynodao.processor.util.StringUtil.uncapitalize;

/**
 * Adds a {@code asCreateTableRequest} method to the staged builder class.
 */
class CreateTableTypeSpecMutator implements NodeTypeSpecMutator {

    private static final ParameterSpec AMAZON_DYNAMO_DB_PARAMETER = ParameterSpec.builder(amazonDynamoDb(), "amazonDynamoDb").build();

    private final MethodSpec createTableWithNoBody;
    private final MethodSpec asRequestWithNoBody;

    @Inject CreateTableTypeSpecMutator(Processors processors) {
        TypeElement interfaceType = processors.getTypeElement(InterfaceType.CREATE.getInterfaceClass().get());
        ExecutableElement load = processors.getMethodByName(interfaceType, "createTable");
        createTableWithNoBody = MethodSpec.methodBuilder(load.getSimpleName().toString())
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(String.class)
                .addParameter(AMAZON_DYNAMO_DB_PARAMETER)
                .build();

        ExecutableElement asRequest = processors.getMethodByName(interfaceType, "asCreateTableRequest");
        asRequestWithNoBody = MethodSpec.overriding(asRequest).build();
    }

    @Override
    public void mutate(Builder typeSpec, NodeClassData node) {
        if (isCreateTable(node)) {
            MethodSpec asRequest = buildAsRequest(node);
            typeSpec.addMethod(asRequest);

            MethodSpec createTable = buildCreateTable(node, asRequest);
            typeSpec.addMethod(createTable);
        }
    }

    private boolean isCreateTable(NodeClassData node) {
        return node.isStagedBuilder() || node.getInterfaceType().equals(InterfaceType.CREATE);
    }

    private MethodSpec buildAsRequest(NodeClassData node) {
        MethodSpec.Builder asRequest = asRequestWithNoBody.toBuilder()
                .addStatement("$1T request = new $1T()", createTableRequest())
                .addStatement("request.setTableName($S)", node.getSchema().getTableName());

        appendAttributeDefinitions(asRequest, node);
        appendKeySchema(asRequest, node);
        appendProvisionedThroughput(asRequest, node);
        if (node.getSchema().hasLocalSecondaryIndexes()) {
            appendLocalSecondaryIndexes(asRequest, node);
        }
        if (node.getSchema().hasGlobalSecondaryIndexes()) {
            appendGlobalSecondaryIndexes(asRequest, node);
        }
        asRequest.addStatement("return request");
        return asRequest.build();
    }

    private void appendAttributeDefinitions(MethodSpec.Builder asRequest, NodeClassData node) {
        asRequest.addStatement("$T attributeDefinitions = new $T<>()", ParameterizedTypeName.get(ClassName.get(List.class), attributeDefinition()), ArrayList.class);

        node.getSchema().getIndexes().stream()
                .flatMap(index -> index.getKeys().stream())
                .distinct()
                .forEach(hashKey -> asRequest
                        .addStatement("attributeDefinitions.add(new $T($S, $T.$L))", attributeDefinition(), hashKey.getPath(), scalarAttributeType(), hashKey.getAttributeType().getDataTypeName()));

        asRequest.addStatement("request.setAttributeDefinitions(attributeDefinitions)");
    }

    private void appendKeySchema(MethodSpec.Builder asRequest, NodeClassData node) {
        addSetKeySchemaForIndex(asRequest, node.getSchema().getTableIndex(), "request");
    }

    private void addSetKeySchemaForIndex(MethodSpec.Builder asRequest, DynamoIndex index, String variableTarget) {
        CodeBlock.Builder keys = CodeBlock.builder()
                .add("$L.setKeySchema($T.asList(", variableTarget, Arrays.class)
                .add("new $T($S, $T.HASH)", keySchemaElement(), index.getHashKey().getPath(), keyType());

        index.getRangeKey().ifPresent(rangeKey -> keys.add(", new $T($S, $T.RANGE)", keySchemaElement(), rangeKey.getPath(), keyType()));
        keys.add("))");

        asRequest.addStatement(keys.build());
    }

    private void appendProvisionedThroughput(MethodSpec.Builder asRequest, NodeClassData node) {
        addSetProvisionedThroughputForIndex(asRequest, "request");
    }

    private void addSetProvisionedThroughputForIndex(MethodSpec.Builder asRequest, String variableTarget) {
        asRequest.addStatement("$L.setProvisionedThroughput(new $T(5L, 5L))", variableTarget, provisionedThroughput());
    }

    private void appendLocalSecondaryIndexes(MethodSpec.Builder asRequest, NodeClassData node) {
        Map<DynamoIndex, String> variableNameByIndex = node.getSchema().getLocalSecondaryIndexes().stream()
                .collect(toMap(Function.identity(), index -> uncapitalize(toClassCase(index.getName()))));

        variableNameByIndex.forEach((index, variableName) -> {
            asRequest.addStatement("$1T $2L = new $1T()", localSecondaryIndex(), variableName);
            asRequest.addStatement("$L.setIndexName($S)", variableName, index.getName());
            addSetKeySchemaForIndex(asRequest, index, variableName);
            addSetProjectionForIndex(asRequest, node, index, variableName);
        });

        String format = repeat(variableNameByIndex.size(), "$L", ", ");
        Object[] args = concat(Arrays.class, variableNameByIndex.values()).toArray();
        asRequest.addStatement("request.setLocalSecondaryIndexes($T.asList(" + format + "))", args);
    }

    private void addSetProjectionForIndex(MethodSpec.Builder asRequest, NodeClassData node, DynamoIndex index, String variableTarget) {
        DynamoIndex table = node.getSchema().getTableIndex();
        if (index.getProjectedAttributes().containsAll(table.getProjectedAttributes())) {
            asRequest.addStatement("$L.setProjection(new $T().withProjectionType($T.ALL))", variableTarget, projection(), projectionType());
        } else if (index.getProjectedAttributes().containsAll(index.getKeys())) {
            asRequest.addStatement("$L.setProjection(new $T().withProjectionType($T.KEYS_ONLY))", variableTarget, projection(), projectionType());
        } else {
            String projection = variableTarget + "Projection";
            asRequest.addStatement("$1T $2L = new $1T().withProjectionType($3T.INCLUDE)", projection(), projection, projectionType());

            String includeAttributes = repeat(index.getProjectedAttributes().size(), "$S", ", ");
            Object[] args = concat(projection, index.getProjectedAttributes().stream().filter(attribute -> !index.getKeys().contains(attribute)))
                    .toArray();
            asRequest.addStatement("$L.setNonKeyAttributes(" + includeAttributes + ")", args);
        }
    }

    private void appendGlobalSecondaryIndexes(MethodSpec.Builder asRequest, NodeClassData node) {
        Map<DynamoIndex, String> variableNameByIndex = node.getSchema().getGlobalSecondaryIndexes().stream()
                .collect(toMap(Function.identity(), index -> uncapitalize(toClassCase(index.getName()))));

        variableNameByIndex.forEach((index, variableName) -> {
            asRequest.addStatement("$1T $2L = new $1T()", globalSecondaryIndex(), variableName);
            asRequest.addStatement("$L.setIndexName($S)", variableName, index.getName());
            addSetKeySchemaForIndex(asRequest, index, variableName);
            addSetProjectionForIndex(asRequest, node, index, variableName);
            addSetProvisionedThroughputForIndex(asRequest, variableName);
        });

        String format = repeat(variableNameByIndex.size(), "$L", ", ");
        Object[] args = concat(Arrays.class, variableNameByIndex.values()).toArray();
        asRequest.addStatement("request.setGlobalSecondaryIndexes($T.asList(" + format + "))", args);
    }

    private MethodSpec buildCreateTable(NodeClassData node, MethodSpec asRequest) {
        return createTableWithNoBody.toBuilder()
                .addStatement("return $N.createTable($N()).getTableDescription().getTableArn()", AMAZON_DYNAMO_DB_PARAMETER, asRequest)
                .build();
    }

}
