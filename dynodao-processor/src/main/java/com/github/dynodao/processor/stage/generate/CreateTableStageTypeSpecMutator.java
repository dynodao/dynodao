package com.github.dynodao.processor.stage.generate;

import com.github.dynodao.DynoDaoCreateTable;
import com.github.dynodao.processor.context.Processors;
import com.github.dynodao.processor.schema.index.DynamoIndex;
import com.github.dynodao.processor.stage.InterfaceType;
import com.github.dynodao.processor.stage.Stage;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec.Builder;

import javax.inject.Inject;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.github.dynodao.processor.util.DynamoDbUtil.amazonDynamoDb;
import static com.github.dynodao.processor.util.DynamoDbUtil.attributeDefinition;
import static com.github.dynodao.processor.util.DynamoDbUtil.createTableRequest;
import static com.github.dynodao.processor.util.DynamoDbUtil.globalSecondaryIndex;
import static com.github.dynodao.processor.util.DynamoDbUtil.keySchemaElement;
import static com.github.dynodao.processor.util.DynamoDbUtil.keyType;
import static com.github.dynodao.processor.util.DynamoDbUtil.localSecondaryIndex;
import static com.github.dynodao.processor.util.DynamoDbUtil.projection;
import static com.github.dynodao.processor.util.DynamoDbUtil.projectionType;
import static com.github.dynodao.processor.util.DynamoDbUtil.provisionedThroughput;
import static com.github.dynodao.processor.util.DynamoDbUtil.scalarAttributeType;
import static com.github.dynodao.processor.util.StreamUtil.concat;
import static com.github.dynodao.processor.util.StringUtil.repeat;
import static com.github.dynodao.processor.util.StringUtil.toClassCase;
import static com.github.dynodao.processor.util.StringUtil.uncapitalize;
import static java.util.stream.Collectors.toMap;

/**
 * Implements all methods defined in the {@link DynoDaoCreateTable} interface.
 * If the type being built does not implement the interface, then nothing is added.
 */
class CreateTableStageTypeSpecMutator implements StageTypeSpecMutator {

    private static final ParameterSpec AMAZON_DYNAMO_DB_PARAMETER = ParameterSpec.builder(amazonDynamoDb(), "amazonDynamoDb").build();

    private final MethodSpec createTableWithNoBody;
    private final MethodSpec asRequestWithNoBody;

    @Inject CreateTableStageTypeSpecMutator(Processors processors) {
        TypeElement interfaceType = processors.getTypeElement(InterfaceType.CREATE.getInterfaceClass());
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
    public void mutate(Builder typeSpec, Stage stage) {
        if (isCreateTable(stage)) {
            MethodSpec asRequest = buildAsRequest(stage);
            typeSpec.addMethod(asRequest);

            MethodSpec createTable = buildCreateTable(stage, asRequest);
            typeSpec.addMethod(createTable);
        }
    }

    private boolean isCreateTable(Stage stage) {
        return stage.isStagedBuilder() || stage.getInterfaceTypes().contains(InterfaceType.CREATE);
    }

    private MethodSpec buildAsRequest(Stage stage) {
        MethodSpec.Builder asRequest = asRequestWithNoBody.toBuilder()
                .addStatement("$1T request = new $1T()", createTableRequest())
                .addStatement("request.setTableName($S)", stage.getSchema().getTableName());

        appendAttributeDefinitions(asRequest, stage);
        appendKeySchema(asRequest, stage);
        appendProvisionedThroughput(asRequest, stage);
        if (stage.getSchema().hasLocalSecondaryIndexes()) {
            appendLocalSecondaryIndexes(asRequest, stage);
        }
        if (stage.getSchema().hasGlobalSecondaryIndexes()) {
            appendGlobalSecondaryIndexes(asRequest, stage);
        }
        asRequest.addStatement("return request");
        return asRequest.build();
    }

    private void appendAttributeDefinitions(MethodSpec.Builder asRequest, Stage stage) {
        asRequest.addStatement("$T attributeDefinitions = new $T<>()", ParameterizedTypeName.get(ClassName.get(List.class), attributeDefinition()), ArrayList.class);

        stage.getSchema().getIndexes().stream()
                .flatMap(index -> index.getKeys().stream())
                .distinct()
                .forEach(hashKey -> asRequest
                        .addStatement("attributeDefinitions.add(new $T($S, $T.$L))", attributeDefinition(), hashKey.getPath(), scalarAttributeType(), hashKey.getAttributeType().getDataTypeName()));

        asRequest.addStatement("request.setAttributeDefinitions(attributeDefinitions)");
    }

    private void appendKeySchema(MethodSpec.Builder asRequest, Stage stage) {
        addSetKeySchemaForIndex(asRequest, stage.getSchema().getTableIndex(), "request");
    }

    private void addSetKeySchemaForIndex(MethodSpec.Builder asRequest, DynamoIndex index, String variableTarget) {
        CodeBlock.Builder keys = CodeBlock.builder()
                .add("$L.setKeySchema($T.asList(", variableTarget, Arrays.class)
                .add("new $T($S, $T.HASH)", keySchemaElement(), index.getHashKey().getPath(), keyType());

        index.getRangeKey().ifPresent(rangeKey -> keys.add(", new $T($S, $T.RANGE)", keySchemaElement(), rangeKey.getPath(), keyType()));
        keys.add("))");

        asRequest.addStatement(keys.build());
    }

    private void appendProvisionedThroughput(MethodSpec.Builder asRequest, Stage stage) {
        addSetProvisionedThroughputForIndex(asRequest, "request");
    }

    private void addSetProvisionedThroughputForIndex(MethodSpec.Builder asRequest, String variableTarget) {
        asRequest.addStatement("$L.setProvisionedThroughput(new $T(5L, 5L))", variableTarget, provisionedThroughput());
    }

    private void appendLocalSecondaryIndexes(MethodSpec.Builder asRequest, Stage stage) {
        Map<DynamoIndex, String> variableNameByIndex = stage.getSchema().getLocalSecondaryIndexes().stream()
                .collect(toMap(Function.identity(), index -> uncapitalize(toClassCase(index.getName()))));

        variableNameByIndex.forEach((index, variableName) -> {
            asRequest.addStatement("$1T $2L = new $1T()", localSecondaryIndex(), variableName);
            asRequest.addStatement("$L.setIndexName($S)", variableName, index.getName());
            addSetKeySchemaForIndex(asRequest, index, variableName);
            addSetProjectionForIndex(asRequest, stage, index, variableName);
        });

        String format = repeat(variableNameByIndex.size(), "$L", ", ");
        Object[] args = concat(Arrays.class, variableNameByIndex.values()).toArray();
        asRequest.addStatement("request.setLocalSecondaryIndexes($T.asList(" + format + "))", args);
    }

    private void addSetProjectionForIndex(MethodSpec.Builder asRequest, Stage stage, DynamoIndex index, String variableTarget) {
        DynamoIndex table = stage.getSchema().getTableIndex();
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

    private void appendGlobalSecondaryIndexes(MethodSpec.Builder asRequest, Stage stage) {
        Map<DynamoIndex, String> variableNameByIndex = stage.getSchema().getGlobalSecondaryIndexes().stream()
                .collect(toMap(Function.identity(), index -> uncapitalize(toClassCase(index.getName()))));

        variableNameByIndex.forEach((index, variableName) -> {
            asRequest.addStatement("$1T $2L = new $1T()", globalSecondaryIndex(), variableName);
            asRequest.addStatement("$L.setIndexName($S)", variableName, index.getName());
            addSetKeySchemaForIndex(asRequest, index, variableName);
            addSetProjectionForIndex(asRequest, stage, index, variableName);
            addSetProvisionedThroughputForIndex(asRequest, variableName);
        });

        String format = repeat(variableNameByIndex.size(), "$L", ", ");
        Object[] args = concat(Arrays.class, variableNameByIndex.values()).toArray();
        asRequest.addStatement("request.setGlobalSecondaryIndexes($T.asList(" + format + "))", args);
    }

    private MethodSpec buildCreateTable(Stage stage, MethodSpec asRequest) {
        return createTableWithNoBody.toBuilder()
                .addStatement("return $N.createTable($N()).getTableDescription().getTableArn()", AMAZON_DYNAMO_DB_PARAMETER, asRequest)
                .build();
    }

}
