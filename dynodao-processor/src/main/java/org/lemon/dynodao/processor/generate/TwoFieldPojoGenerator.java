package org.lemon.dynodao.processor.generate;

import static org.lemon.dynodao.processor.generate.GenerateConstants.ATTRIBUTE_VALUE;
import static org.lemon.dynodao.processor.generate.GenerateConstants.DYNAMO_DB_MAPPER_PARAM;
import static org.lemon.dynodao.processor.generate.GenerateConstants.DYNAMO_DB_QUERY_EXPRESSION;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import org.lemon.dynodao.processor.context.ProcessorContext;
import org.lemon.dynodao.processor.index.DynamoIndex;
import org.lemon.dynodao.processor.index.IndexType;

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

class TwoFieldPojoGenerator {

    @Inject ProcessorContext processorContext;

    @Inject CtorTypeGenerator ctorGenerator;

    @Inject TwoFieldPojoGenerator() { }

    TypeSpec build(TypeElement document, DynamoIndex index) {
        InterfaceType interfaceType = getInterfaceType(index);
        List<FieldSpec> fields = getFields(document, index);
        return TypeSpec.classBuilder(getClassName(document, index, interfaceType))
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(interfaceType.asSuperInterface(document, processorContext))
                .addFields(fields)
                .addMethod(ctorGenerator.buildAllArgsCtor(fields))
                .addMethod(buildGetMethod(document, index, fields, interfaceType))
                .build();
    }

    private InterfaceType getInterfaceType(DynamoIndex index) {
        return index.getIndexType().equals(IndexType.TABLE) ? InterfaceType.DOCUMENT_LOAD : InterfaceType.DOCUMENT_QUERY;
    }

    private List<FieldSpec> getFields(TypeElement document, DynamoIndex index) {
        FieldSpec hash = FieldSpec.builder(TypeName.get(index.getHashKey().asType()), index.getHashKey().getSimpleName().toString())
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();
        FieldSpec range = FieldSpec.builder(TypeName.get(index.getRangeKey().get().asType()), index.getRangeKey().get().getSimpleName().toString())
                .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                .build();
        return Arrays.asList(hash, range);
    }

    private String getClassName(TypeElement document, DynamoIndex index, InterfaceType interfaceType) {
        VariableElement hash = index.getHashKey();
        VariableElement range = index.getRangeKey().get();
        return String.format("%s%s%s%s", capitalize(hash), capitalize(range), document.getSimpleName(), interfaceType.getInterfaceName());
    }

    private String capitalize(Element element) {
        return capitalize(element.getSimpleName().toString());
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    private MethodSpec buildGetMethod(TypeElement document, DynamoIndex index, List<FieldSpec> fields, InterfaceType interfaceType) {
        VariableElement hash = index.getHashKey();
        VariableElement range = index.getRangeKey().get();
        ExecutableElement method = interfaceType.getMethod(processorContext);
        MethodSpec.Builder load = MethodSpec.methodBuilder(method.getSimpleName().toString())
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(interfaceType.asReturnType(document, processorContext))
                .addParameter(DYNAMO_DB_MAPPER_PARAM);

        if (interfaceType.equals(InterfaceType.DOCUMENT_LOAD)) {
            load.addStatement("return $N.load($T.class, $L, $L)", DYNAMO_DB_MAPPER_PARAM, document.asType(), hash, range);
        } else if (interfaceType.equals(InterfaceType.DOCUMENT_QUERY)) {
            TypeName queryExpression = ParameterizedTypeName.get(DYNAMO_DB_QUERY_EXPRESSION, TypeName.get(document.asType()));
            load
                    .addStatement("$T query = new $T()", queryExpression, queryExpression)
                    .addStatement("query.setIndexName($S)", index.getName())
                    .addStatement("query.setKeyConditionExpression($S)", String.format("#%s = :%s AND #%s = :%s", hash, hash, range, range))
                    .addStatement("query.addExpressionAttributeNamesEntry($S, $S)", "#" + hash, hash)
                    .addStatement("query.addExpressionAttributeNamesEntry($S, $S)", "#" + range, range)
                    // TODO need to select the proper withX method here
                    // https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/HowItWorks.CoreComponents.html#HowItWorks.CoreComponents.TablesItemsAttributes
                    // The only data types allowed for primary key attributes are string, number, or binary. 
                    .addStatement("query.addExpressionAttributeValuesEntry($S, new $T().withS($L))", ":" + hash, ATTRIBUTE_VALUE, hash)
                    .addStatement("query.addExpressionAttributeValuesEntry($S, new $T().withS($L))", ":" + range, ATTRIBUTE_VALUE, range)
                    .addStatement("return $N.query($T.class, query)", DYNAMO_DB_MAPPER_PARAM, document.asType());
        }
        return load.build();
    }

}
